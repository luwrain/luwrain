/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.tests;

import java.io.File;
import org.luwrain.core.*;
import org.luwrain.controls.*;

class TestingControlEnvironment implements ControlEnvironment
{
    //    private Luwrain luwrain;
    public String spoken = "";
    public String[] clipboard = new String[0];
    public int lastHint;

    @Override public void say(String text)
    {
	spoken = text;
    }

    @Override public void sayStaticStr(int code)
    {
	say(staticStr(code));
    }

    public void sayLetter(char letter)
    {
	spoken = "";
	spoken += letter;
    }

    @Override public void hint(String text)
    {
	spoken = text;
    }

    @Override public void hint(String text, int code)
    {
	spoken = text;
	lastHint = code;
    }

    @Override public void hint(int code)
    {
	lastHint = code;
    }

    public void hintStaticString(int id)
    {
	hint(staticStr(id));
    }

    public void onAreaNewName(Area area)
    {
    }

    public void onAreaNewContent(Area area)
    {
    }

    public void onAreaNewHotPoint(Area area)
    {
    }

    public int getAreaVisibleHeight(Area area)
    {
	return 25;
    }

    public void setClipboard(String[] value)
    {
	clipboard = value;
    }

    public String[] getClipboard()
    {
	return clipboard;
    }

    public void popup(Popup popupObj)
    {
    }

    @Override public LaunchContext launchContext()
    {
	return new LaunchContext("", "", "");
    }

    @Override public String staticStr(int code)
    {
	return "#static " + code + "#";
    }

    @Override public org.luwrain.core.Strings environmentStrings()
    {
	return null;
    }

    @Override public void playSound(int id)
    {
    }

@Override public UniRefInfo getUniRefInfo(String uniRef)
    {
	return null;
    }
}
