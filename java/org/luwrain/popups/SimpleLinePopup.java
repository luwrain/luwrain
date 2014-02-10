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
//FIXME:Improper behaviour on backspace;

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class SimpleLinePopup implements Area, PopupClosingRequest, HotPointInfo, EmbeddedEditLines
{
    public PopupClosing closing = new PopupClosing(this);
    private EmbeddedSingleLineEdit edit;
    private Object instance;
    private String name;
    private String prefix;
    private String text;
    private int pos;

    public SimpleLinePopup(Object instance,
			    String name,
			    String prefix,
			    String text)
    {
	this.instance = instance;
	this.name = name != null?name:"";
	this.prefix = prefix != null?prefix:"";
	this.text = text != null?text:"";
	this.pos = prefix.length() + text.length();
	this.edit = new EmbeddedSingleLineEdit(this, this, prefix.length(), 0);
    }

    public int getLineCount()
    {
	return 1;
    }

    public String getLine(int index)
    {
	return index == 0?(prefix + text):"";
    }

    public int getHotPointX()
    {
	return pos;
    }

    public int getHotPointY()
    {
	return 0;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (closing.onKeyboardEvent(event))
	    return true;
	if (edit.isPosCovered(pos, 0) && edit.onKeyboardEvent(event))
	    return true;
	if (!event.isCommand() || event.isModified())
	    return false;
	final String line = prefix + text;
	switch (event.getCommand())
	{
	case KeyboardEvent.ARROW_LEFT:
	    if (pos == 0)
	    {
		Speech.say(Langs.staticValue(Langs.AREA_BEGIN), Speech.PITCH_HIGH);
		return true;
	    }
	    pos--;
	    if (pos < line.length())
		Speech.sayLetter(line.charAt(pos)); else
		Speech.sayLetter(' ');
	    Luwrain.onAreaNewHotPoint(this);
	    return true;
	case KeyboardEvent.ARROW_RIGHT:
	    if (pos >= line.length())
	    {
		Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH);
		return true;
	    }
	    pos++;
	    if (pos < line.length())
		Speech.sayLetter(line.charAt(pos)); else
		Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH);
	    Luwrain.onAreaNewHotPoint(this);
	    return true;
	case KeyboardEvent.HOME:
	    pos = prefix.length();
	    if (pos >= line.length())
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH); else
		Speech.sayLetter(prefix.charAt(pos));
	    return true;
	case KeyboardEvent.END:
	    pos = line.length();
	    Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH);
	    Luwrain.onAreaNewHotPoint(this);
	    return true;
	case KeyboardEvent.ENTER:
	    closing.doOk();
	    return true;
	}
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return closing.onEnvironmentEvent(event);
    }

    public String getName()
    {
	return name;
    }

    public String getText()
    {
	return text;
    }

    public String getEmbeddedEditLine(int editPosX, int editPosY)
    {
	return text;
    }

    public void setEmbeddedEditLine(int editPosX, int editPosY, String value)
    {
	text = value != null?value:"";
	Luwrain.onAreaNewContent(this);
    }

    public void setHotPointX(int value)
    {
	if (value < 0)
	    return;
	pos = value;
	Luwrain.onAreaNewHotPoint(this);
    }

    public void setHotPointY(int value)
    {
	//Nothing here;
    }

    public boolean onOk()
    {
	return true;
    }

    public boolean onCancel()
    {
	return true;
    }

    protected String getTextBeforeHotPoint()
    {
	if (text == null)
	    return "";
	final int offset = pos - prefix.length();
	if (offset < 0)
	    return "";
	if (offset >= text.length())
	    return text;
	return text.substring(0, offset);
    }

    protected String getTextAfterHotPoint()
    {
	if (text == null)
	    return "";
	final int offset = pos - prefix.length();
	if (offset < 0)
	    return text;
	if (offset >= text.length())
	    return "";
	return text.substring(offset);
    }

    //Speaks nothing;
    protected void setText(String beforeHotPoint, String afterHotPoint)
    {
	if (beforeHotPoint == null || afterHotPoint == null)
	    return;
	text = beforeHotPoint + afterHotPoint;
	pos = prefix.length() + beforeHotPoint.length();
	Luwrain.onAreaNewContent(this);
	Luwrain.onAreaNewHotPoint(this);
    }
}
