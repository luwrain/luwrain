/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class YesNoPopup implements Area, Popup, PopupClosingRequest
{
    public PopupClosing closing = new PopupClosing(this);
    private Luwrain luwrain;
    private String name = "";
    private String text = "";
    private boolean res;
    private boolean defaultRes;
    private boolean noMultipleCopies = false;

    public YesNoPopup(Luwrain luwrain,
		      String name,
		      String text,
		      boolean defaultRes)
    {
	this.luwrain = luwrain;
	this.name = name;
	this.text = text;
	this.defaultRes = defaultRes;
	this.res = defaultRes;
	this.noMultipleCopies = false;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (text == null)
	    throw new NullPointerException("text may not be null");
    }

    public YesNoPopup(Luwrain luwrain,
		      String name,
		      String text,
		      boolean defaultRes,
		      boolean noMultipleCopies)
    {
	this.luwrain = luwrain;
	this.name = name;
	this.text = text;
	this.defaultRes = defaultRes;
	this.res = defaultRes;
	this.noMultipleCopies = noMultipleCopies;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (text == null)
	    throw new NullPointerException("text may not be null");
    }

    @Override public int getLineCount()
    {
	return 1;
    }

    @Override public String getLine(int index)
    {
	return index == 0?text:"";
    }

    @Override public int getHotPointX()
    {
	return text.length();
    }

    @Override public int getHotPointY()
    {
	return 0;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
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
	    luwrain.say(text);
	    return true;
	}
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch (event.getCode())
	{
	case EnvironmentEvent.INTRODUCE:
	    luwrain.say(text);
	    return true;
	default:
	return closing.onEnvironmentEvent(event);
	}
    }

    @Override public String getName()
    {
	return name;
    }

    public boolean getResult()
    {
	return res;
    }

    @Override public boolean onOk()
    {
	return true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    @Override public Luwrain getLuwrainObject()
    {
	return luwrain;
    }

    @Override public EventLoopStopCondition getStopCondition()
    {
	return closing;
    }

    @Override public boolean noMultipleCopies()
    {
	return noMultipleCopies;
    }

    @Override public boolean isWeakPopup()
    {
	return false;
    }
}
