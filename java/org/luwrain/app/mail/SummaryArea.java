/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.mail;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.pim.*;

class SummaryArea implements Area
{
    private static final int INITIAL_HOT_POINT_X = 2;

    private StringConstructor stringConstructor;
    private Actions actions;
    private StoredMailMessage[] messages;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public SummaryArea(Actions actions, StringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
	this.actions = actions;
    }

    public void show(StoredMailMessage[] messages)
    {
	this.messages = messages;
	hotPointY = 0;
	hotPointX = this.messages != null && this.messages.length >= 1?INITIAL_HOT_POINT_X:0;
	Luwrain.onAreaNewHotPoint(this);
	Luwrain.onAreaNewContent(this);
    }

    public int getLineCount()
    {
	return messages != null && messages.length >= 1?messages.length:1;
    }

    public String getLine(int index)
    {
	return messages != null && index < messages.length?constructStringForScreen(messages[index]):"";
    }

    public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	//Tab;
	if (event.isCommand() && !event.isModified() &&
	    event.getCommand() == KeyboardEvent.TAB)
	{
	    actions.gotoMessage();
	    return true;
	}
	if (!event.isCommand() || event.isModified())
	    return false;
	if (messages == null || messages.length <= 0)
	{
	    Speech.say(stringConstructor.emptySummaryArea(), Speech.PITCH_HIGH);
	    return true;
	}
	switch(event.getCommand())
	{
	case KeyboardEvent.ARROW_DOWN:
	    onKeyDown(event);
	    return true;
	case KeyboardEvent.ARROW_UP:
	    onKeyUp(event);
	    return true; 
	default:
	    return false;
	}
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.CLOSE:
	    actions.close();
	    return true;
	case EnvironmentEvent.REFRESH:
	    //FIXME:
	    return true;
	default:
	    return false;
	}
    }

    public String getName()
    {
	return stringConstructor.summaryAreaName();
    }

    private void onKeyDown(KeyboardEvent event)
    {
	if (hotPointY >= messages.length)
	{
	    Speech.say(stringConstructor.lastSummaryLine(), Speech.PITCH_HIGH);
	    return;
	}
	hotPointY++;
	if (hotPointY < messages.length)
	{
	    hotPointX = INITIAL_HOT_POINT_X;
	    Speech.say(constructStringForSpeech(messages[hotPointY]));
	} else
	{
	    hotPointX = 0;
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	}
	Luwrain.onAreaNewHotPoint(this);
    }

    public void onKeyUp(KeyboardEvent event)
    {
	if (hotPointY < 1)
	{
	    Speech.say(stringConstructor.firstSummaryLine(), Speech.PITCH_HIGH);
	    return;
	}
	hotPointY--;
	if (hotPointY >= messages.length)
	{
	    hotPointY = messages.length;
	    hotPointX = 0;
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    Luwrain.onAreaNewHotPoint(this);
	    return;
	}
	hotPointX = INITIAL_HOT_POINT_X;
	Speech.say(constructStringForSpeech(messages[hotPointY]));
	Luwrain.onAreaNewHotPoint(this);
    }

    private String constructStringForSpeech(StoredMailMessage message)
    {
	if (message == null)
	    return "";
	return message.getFromAddr() + " " + message.getSubject();
    }

    private String constructStringForScreen(StoredMailMessage message)
    {
	if (message == null)
	    return "";
	return message.getFromAddr() + " " + message.getSubject();
    }
}
