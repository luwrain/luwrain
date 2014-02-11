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

package org.luwrain.core.events;

//TODO:Rename Command -> Action;

import org.luwrain.core.*;

public class KeyboardEvent extends Event
{
    public static final int ENTER = 0;
    public static final int BACKSPACE = 1;
    public static final int ESCAPE = 2;
    public static final int TAB = 3;

    public static final int ARROW_DOWN = 10;
    public static final int ARROW_UP = 11;
    public static final int ARROW_LEFT = 12;
    public static final int ARROW_RIGHT = 13;

    public static final int INSERT = 20;
    public static final int DELETE = 21;
    public static final int HOME = 22;
    public static final int END = 23;
    public static final int PAGE_UP = 24;
    public static final int PAGE_DOWN = 25;

    public static final int F1 = 30;
    public static final int F2 = 31;
    public static final int F3 = 32;
    public static final int F4 = 33;
    public static final int F5 = 34;
    public static final int F6 = 35;
    public static final int F7 = 36;
    public static final int F8 = 37;
    public static final int F9 = 38;
    public static final int F10 = 39;
    public static final int F11 = 40;
    public static final int F12 = 41;

    public static final int WINDOWS = 50;
    public static final int CONTEXT_MENU = 51;

    public static final int SHIFT = 60;
    public static final int CONTROL = 61;
    public static final int LEFT_ALT = 62;
    public static final int RIGHT_ALT = 63;

    private boolean cmd = false;
    private int cmdCode = 0;
    private char nonCmdChar = 0;

    private boolean shiftPressed = false;
    private boolean controlPressed = false;
    private boolean leftAltPressed = false;
    private boolean rightAltPressed = false;

    public KeyboardEvent(boolean cmd,
			 int cmdCode,
			 char nonCmdChar,
			 boolean shiftPressed,
			 boolean controlPressed,
			 boolean leftAltPressed,
			 boolean rightAltPressed)
    {
	super(KEYBOARD_EVENT);
	this.cmd = cmd;
	this.cmdCode = cmdCode;
	this.nonCmdChar = nonCmdChar;
	this.shiftPressed = shiftPressed;
	this.controlPressed = controlPressed;
	this.leftAltPressed = leftAltPressed;
	this.rightAltPressed = rightAltPressed;
    }

    public KeyboardEvent(boolean cmd,
			 int cmdCode,
			 char nonCmdChar)
    {
	super(KEYBOARD_EVENT);
	this.cmd = cmd;
	this.cmdCode = cmdCode;
	this.nonCmdChar = nonCmdChar;
	shiftPressed = false;
	controlPressed = false;
	leftAltPressed = false;
	rightAltPressed = false;
    }

    public boolean equals(KeyboardEvent event)
    {
	return (cmd == event.cmd &&
		(!cmd || cmdCode == event.cmdCode) &&
		(cmd || 		nonCmdChar == event.nonCmdChar) &&
		shiftPressed == event.shiftPressed &&
		controlPressed == event.controlPressed &&
		leftAltPressed == event.leftAltPressed &&
		rightAltPressed == event.rightAltPressed);
    }

    public boolean isCommand()
    {
	return cmd;
    }

    public char getCharacter()
    {
	return nonCmdChar;
    }

    public int getCommand()
    {
	return cmdCode;
    }

    public boolean isModified()
    {
	return shiftPressed || controlPressed || leftAltPressed || rightAltPressed;
    }

    public boolean withShift()
    {
	return shiftPressed;
    }

    public boolean withShiftOnly()
    {
	return shiftPressed && !controlPressed && !leftAltPressed && !rightAltPressed;
    }

    public boolean withControl()
    {
	return controlPressed;
    }

    public boolean withControlOnly()
    {
	return controlPressed && !shiftPressed && !leftAltPressed && !rightAltPressed;
    }

    public boolean withAlt()
    {
	return leftAltPressed || rightAltPressed;
    }

    public boolean withAltOnly()
    {
	return (leftAltPressed || rightAltPressed) && !shiftPressed && !controlPressed;
    }

    public boolean withLeftAlt()
    {
	return leftAltPressed;
    }

    public boolean withLeftAltOnly()
    {
	return leftAltPressed && !rightAltPressed && !shiftPressed && !controlPressed;
    }

    public boolean withRightAlt()
    {
	return rightAltPressed;
    }

    public boolean withRightAltOnly()
    {
	return rightAltPressed && !leftAltPressed && !shiftPressed && !controlPressed;
    }

    public String toString()
    {
	String res = cmd?("[CMD] " + cmdCode):("\'" + nonCmdChar + "\'");
	if (shiftPressed)
	    res += " SHIFT";
	if (controlPressed)
	    res += " CTRL";
	if (leftAltPressed)
	    res += " LEFT-ALT";
	if (rightAltPressed)
	    res += " RIGHT-ALT";
	return res;
    }
}
