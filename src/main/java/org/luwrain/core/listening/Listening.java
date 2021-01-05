/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.listening;

import org.luwrain .core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.speech.*;
import org.luwrain.core.ListenableArea.ListeningInfo;


public final class Listening
{
    private final Luwrain luwrain;
    private final Speech speech;
    private final Runnable completion;
    private final Settings.SpeechParams sett;
    private final Channel channel;
        private final Area area;
    private ListenableArea listenableArea = null;

    public Listening(Luwrain luwrain, Speech speech, Area area, Runnable completion)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(speech, "speech");
	NullCheck.notNull(area, "area");
	NullCheck.notNull(completion, "completion");
	this.luwrain = luwrain;
	this.speech = speech;
	this.area = area;
	this.completion = completion;
	this.sett = Settings.createSpeechParams(luwrain.getRegistry());
		if (sett.getListeningEngineName("").isEmpty())
		{
		    this.channel = null;
	    return;
		}
	channel = speech.loadChannel(sett.getListeningEngineName(""), sett.getListeningEngineParams(""));
		//channel.setDefaultRate(45);
	//channel.setDefaultPitch(30);
    }

    public boolean start()
    {
	if (channel == null)
	    return false;
	if (area instanceof ListenableArea)
	{
	    this.listenableArea = (ListenableArea)this.area;
	    final ListeningInfo info = listenableArea.onListeningStart();
	    if (info == null || info.noMore())
	    {
		this.listenableArea = null;
		this.channel.close();
		return false;
	    }
	speak(info);
	return true;
	}
	this.listenableArea = new CompatArea(area);
	ListeningInfo info = listenableArea.onListeningStart();
	if (info != null && !info.noMore())
	{
		speak(info);
		return true;
	}
	this.listenableArea = new PlainArea(area);
	info = listenableArea.onListeningStart();
	if (info != null && !info.noMore())
	{
		speak(info);
		return true;
	}
	this.listenableArea = null;
	this.channel .close();
	return false;
	    }

    public void cancel()
    {
	if (channel == null || listenableArea == null)
	    return;
	this.channel.silence();
	this.channel.close();
	this.listenableArea = null;
	this.completion.run();
    }

    private void onFinish(ListeningInfo listeningInfo)
    {
	NullCheck.notNull(listeningInfo, "listeningInfo");
	if (listenableArea == null)
	    return;
	listenableArea.onListeningFinish(listeningInfo);
	final ListeningInfo nextInfo = listenableArea.onListeningStart();
	if (nextInfo == null || nextInfo.noMore())
	{
	    listenableArea = null;
	    channel.close();
	    this.completion.run();
	    return;
	}
	speak(nextInfo);
    }

    private void speak(ListeningInfo listeningInfo)
    {
	NullCheck.notNull(listeningInfo, "listeningInfo");
	final Channel.Listener listener = (id)->luwrain.runUiSafely(()->onFinish(listeningInfo));
	channel .speak(luwrain.getSpeakableText(listeningInfo.getText(), Luwrain.SpeakableTextType.NATURAL), listener, sett.getListeningPitch(50) - 50, 50 - sett.getListeningRate(50), false);
    }
}
