/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.core;

public class KeyboardEvent extends Event
{
    public static final int ENTER = 10;
    public static final int ESCAPE = 27;
    public static final int TAB = 9;

    public static final int ARROW_DOWN = 258;
    public static final int ARROW_UP = 259;
    public static final int ARROW_LEFT = 260;
    public static final int ARROW_RIGHT = 261;

    private int code;

    public KeyboardEvent(int code)
    {
	super(KEYBOARD_EVENT);
	this.code = code;
    }

    public int getRawCode()
    {
	return code;
    }

    public boolean isCommand()
    {
	return true;
    }

    public int getCommand()
    {
	return code;
    }
}
