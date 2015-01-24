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

package org.luwrain.controls;

import org.luwrain.core.*;

public class DefaultControlEnvironment implements ControlEnvironment
{
    private Luwrain luwrain;

    public DefaultControlEnvironment(Luwrain luwrain)
    {
	this.luwrain = luwrain;
    }

    public void say(String text)
    {
	Speech.say(text);
    }

    public void sayLetter(char letter)
    {
	Speech.sayLetter(letter);
    }

    public void hint(String text)
    {
	Speech.say(text, Speech.PITCH_HIGH);
    }

    public void hintStaticString(int id)
    {
	hint(langStaticString(id));
    }

    public void onAreaNewName(Area area)
    {
	luwrain.onAreaNewName(area);
    }

    public void onAreaNewContent(Area area)
    {
	luwrain.onAreaNewContent(area);
    }

    public void onAreaNewHotPoint(Area area)
    {
	luwrain.onAreaNewHotPoint(area);
    }

    public int getAreaVisibleHeight(Area area)
    {
	return luwrain.getAreaVisibleHeight(area);
    }

    public String langStaticString(int id)
    {
	return Langs.staticValue(id);
    }

    public void setClipboard(String[] value)
    {
	luwrain.setClipboard(value);
    }

    public String[] getClipboard()
    {
	return luwrain.getClipboard();
    }

    public void popup(Popup popupObj)
    {
	luwrain.popup(popupObj);
    }
}
