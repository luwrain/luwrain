/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.speech.*;

class Listening
{
    private final Luwrain luwrain;
    private final Speech speech;
    private final Area area;
    private Channel channel;

    Listening(Luwrain luwrain, Speech speech, Area area)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(speech, "speech");
	NullCheck.notNull(area, "area");
	this.luwrain = luwrain;
	this.speech = speech;
	this.area = area;
    }

    boolean start()
    {
	channel = speech.getReadingChannel();
	if (channel == null)
	    return false;
	Log.debug("core", "using the channel \'" + channel.getChannelName() + " for listening area of class " + area.getClass().getName());
	channel.setDefaultRate(45);
	channel.setDefaultPitch(30);
	luwrain.playSound(Sounds.PLAYING);
	onFinish(null, null);
	return true;
    }

    void cancel()
    {
	if (channel == null)
	    return;
	channel.silence();
	channel = null;
    }

    private void onFinish(String text, Object extraInfo)
    {
	if (text != null)
	{
	    if (extraInfo == null || !(extraInfo instanceof PositionInfo))
		area.onSystemEvent(new ListeningFinishedEvent(extraInfo)); else
		area.onSystemEvent(new MoveHotPointEvent(((PositionInfo)extraInfo).x, ((PositionInfo)extraInfo).y, false));
	}
	final BeginListeningQuery query = new BeginListeningQuery();
	if (AreaQuery.ask(area, query))
	    startNormal(query.getAnswer().getText(), query.getAnswer().getExtraInfo()); else
	    startGeneral();
    }

    private void startNormal(String text, Object extraInfo)
    {
	final Channel.Listener listener = new Channel.Listener(){
		@Override public void onFinished(long id)
		{
		    luwrain.runUiSafely(()->onFinish(text, extraInfo));
		}};
	channel .speak(text, listener, 0, 0, false);
    }

    private void startGeneral()
    {
	final int fromPosX = area.getHotPointX(); 
	final int fromPosY = area.getHotPointY();
	final StringBuilder b = new StringBuilder();
	final int count = area.getLineCount();
	if (fromPosY >= count)
	    return;
	int index = fromPosY;
	String line = area.getLine(index);
	if (line == null)
	    line = "";
	if (fromPosX < line.length())
	    line = line.substring(fromPosX); else
	    line = "";
	int pos = 0;
	while(true)
	{
	    while (pos < line.length() && 
		   line.charAt(pos) != '.' && line.charAt(pos) != '!' && line.charAt(pos) != '?')
		++pos;
	    if (pos >= line.length())
	    {
		b.append(line + " ");
		pos = 0;
		++index;
		if (index >= count)
		    break;
		line = area.getLine(index);
		if (line == null)
		    line = "";
		continue;
	    }
	    b.append(line.substring(0, pos + 1));
	    break;
	} //while(true)
	int nextPosX = pos + 1;
	int nextPosY = index;
	if (nextPosX >= line.length())
	{
	    nextPosX = 0;
	    //We may be careless that nextPosY is greater than number of lines, a corresponding check will be performed on next step
	    ++nextPosY;
	}
	//If it is still a first line, we must restore a shift for the text prior to fromPosX
	if (nextPosY == fromPosY)
	    nextPosX += fromPosX;
	startNormal(new String(b), new PositionInfo(nextPosX, nextPosY));
    }

static private class PositionInfo
{
    final int x;
    final int y;

    PositionInfo(int x, int y)
    {
	this.x = x;
	this.y = y;
    }
}
}
