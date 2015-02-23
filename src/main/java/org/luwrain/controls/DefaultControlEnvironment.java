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

import java.io.File;

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
	luwrain.say(text);
    }

    @Override public void sayStaticStr(int code)
    {
	say(staticStr(code));
    }

    public void sayLetter(char letter)
    {
	luwrain.sayLetter(letter);
    }

    @Override public void hint(String text)
    {
	luwrain.hint(text);
    }

    @Override public void hint(String text, int code)
    {
	luwrain.hint(text, code);
    }

    @Override public void hint(int code)
    {
	luwrain.hint(code);
    }

    public void hintStaticString(int id)
    {
	hint(staticStr(id));
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

    @Override public LaunchContext launchContext()
    {
	return luwrain.launchContext();
    }

    @Override public String staticStr(int code)
    {
	return luwrain.i18n().staticStr(code);
    }

    @Override public File getFsRoot(File relativeTo)
    {
	if (relativeTo == null)
	    throw new NullPointerException("relativeTo may not be null");
	return luwrain.os().getRoot(relativeTo);
    }
}
