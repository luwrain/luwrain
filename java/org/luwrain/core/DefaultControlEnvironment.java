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

package org.luwrain.core;

public class DefaultControlEnvironment implements ControlEnvironment
{
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
	Luwrain.onAreaNewName(area);
    }

    public void onAreaNewContent(Area area)
    {
	Luwrain.onAreaNewContent(area);
    }

    public void onAreaNewHotPoint(Area area)
    {
	Luwrain.onAreaNewHotPoint(area);
    }

    public int getAreaVisibleHeight(Area area)
    {
	return Luwrain.getAreaVisibleHeight(area);
    }

    public String langStaticString(int id)
    {
	return Langs.staticValue(id);
    }

    public void setClipboard(String[] value)
    {
	Luwrain.setClipboard(value);
    }

    public String[] getClipboard()
    {
	return Luwrain.getClipboard();
    }

    public void popup(Popup popupObj)
    {
	Luwrain.popup(popupObj);
    }
}
