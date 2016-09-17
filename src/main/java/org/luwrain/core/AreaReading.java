/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

class AreaReading
{
    private Area area;
    private EventConsumer eventConsumer;
    private Channel channel;
    private Speech speech;

    boolean start()
    {
channel = speech.getReadingChannel();
if (channel == null)
    return false;
	Log.debug("core", "using the channel \'" + channel.getChannelName() + " for area reading");
	final VoicedFragmentQuery query = new VoicedFragmentQuery();
	if (area.onAreaQuery(query) && query.hasAnswer())
	    startReading(query.text(), query.nextPointX(), query.nextPointY()); else
	    startReadingGeneralText(area.getHotPointX(), area.getHotPointY());
	    return true;
    }

    private void fragmentReadingFinished(String text,
					 int nextPointX, int nextPointY)
    {
	area.onEnvironmentEvent(new ReadingPointEvent(nextPointX, nextPointY));
	final VoicedFragmentQuery query = new VoicedFragmentQuery();
	if (area.onAreaQuery(query) && query.hasAnswer())
	    startReading(query.text(), query.nextPointX(), query.nextPointY()); else
	    startReadingGeneralText(nextPointX, nextPointY);
    }

    private void startReadingGeneralText(int fromPosX, int fromPosY)
    {
	final StringBuilder b = new StringBuilder();
	final int count = area.getLineCount();
	if (fromPosY >= count)
	    return;
	int index = fromPosY;
	String line = area.getLine(index);
	if (line == null)
	    return;
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
	    b.append(line);
	    pos = 0;
	    ++index;
	    if (index >= count)
		break;
	    line = area.getLine(index);
	    if (line == null)
		return;
	    continue;
	}
	b.append(line.substring(0, pos + 1));
	break;
	}
	int nextPosX = pos + 1;
	int nextPosY = index;
	if (nextPosX >= line.length())
	{
	    nextPosX = 0;
	    //We may be careless that nextPosY would be greater than number of lines, a corresponding check will be performed on next step
	    ++nextPosY;
	}
	//If it is still a first line, we must restore a text to fromPosX
	if (nextPosY == fromPosY)
	    nextPosX += fromPosX;
	startReading(new String(b), nextPosX, nextPosY);
    }

    private void startReading(String text,
			      int nextPointX, int nextPointY)
    {
	final Channel.Listener listener = new Channel.Listener(){
		@Override public void onFinished(long id)
		{
		    eventConsumer.enqueueEvent(new RunnableEvent(()->{fragmentReadingFinished(text, nextPointX, nextPointY);}));
		}};
channel .speak(text, listener, 0, 0);
    }

    private void cancel()
    {
	if (channel == null)
	    return;
	channel.silence();
	channel = null;
    }
}
