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

//FIXME:ControlEnvironment interface support;

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class YesNoPopup implements Area, Popup, PopupClosingRequest
{
    public PopupClosing closing = new PopupClosing(this);
    private Object instance;
    private String name = "";
    private String text = "";
    private boolean res;
    private boolean defaultRes;
    private boolean noMultipleCopies = false;

    public YesNoPopup(Object instance,
		      String name,
		      String text,
		      boolean defaultRes)
    {
	this.instance = instance;
	this.name = name != null?name:"";
	this.text = text != null?text:"";
	this.defaultRes = defaultRes;
	this.res = defaultRes;
	this.noMultipleCopies = false;
    }

    public YesNoPopup(Object instance,
		      String name,
		      String text,
		      boolean defaultRes,
		      boolean noMultipleCopies)
    {
	this.instance = instance;
	this.name = name != null?name:"";
	this.text = text != null?text:"";
	this.defaultRes = defaultRes;
	this.res = defaultRes;
	this.noMultipleCopies = false;
	this.noMultipleCopies = noMultipleCopies;
    }

    public int getLineCount()
    {
	return 1;
    }

    public String getLine(int index)
    {
	return index == 0?text:"";
    }

    public int getHotPointX()
    {
	return text.length();
    }

    public int getHotPointY()
    {
	return 0;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (closing.onKeyboardEvent(event))
	    return true;
	if (!event.isCommand())
	{
	    final int c = event.getCharacter();
	    if (c == 'y' || c == 'Y')
	    {
		res = true;
		closing.doOk();
		return true;
	    }
	    if (c == 'n' || c == 'N')
	    {
		res = false;
		closing.doOk();
		return true;
	    }
	    return false;
	}
	if (event.isModified())
	    return false;
	final int cmd = event.getCommand();
	if (cmd == KeyboardEvent.ENTER)
	{
	    closing.doOk();
	    return true;
	}
	if (cmd == KeyboardEvent.ARROW_UP ||
	    cmd == KeyboardEvent.ARROW_DOWN ||
	    cmd == KeyboardEvent.ARROW_LEFT ||
	    cmd == KeyboardEvent.ARROW_RIGHT)
	{
	    Speech.say(text);
	    return true;
	}
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch (event.getCode())
	{
	case EnvironmentEvent.INTRODUCE:
	    Speech.say(text);
	    return true;
	default:
	return closing.onEnvironmentEvent(event);
	}
    }

    public String getName()
    {
	return name;
    }

    public boolean getResult()
    {
	return res;
    }

    public boolean onOk()
    {
	return true;
    }

    public boolean onCancel()
    {
	return true;
    }

    @Override public Object getInstance()
    {
	return instance;
    }

    @Override public EventLoopStopCondition getStopCondition()
    {
	return closing;
    }

    @Override public boolean noMultipleCopies()
    {
	return noMultipleCopies;
    }
}
