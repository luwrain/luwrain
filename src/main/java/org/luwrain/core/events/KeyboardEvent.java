/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core.events;

//TODO:Rename Command -> Action;

import org.luwrain.core.*;

public class KeyboardEvent extends Event
{
    static public final int ENTER = 0;
    static public final int BACKSPACE = 1;
    static public final int ESCAPE = 2;
    static public final int TAB = 3;

    static public final int ARROW_DOWN = 10;
    static public final int ARROW_UP = 11;
    static public final int ARROW_LEFT = 12;
    static public final int ARROW_RIGHT = 13;

    static public final int INSERT = 20;
    static public final int DELETE = 21;
    static public final int HOME = 22;
    static public final int END = 23;
    static public final int PAGE_UP = 24;
    static public final int PAGE_DOWN = 25;

    static public final int F1 = 30;
    static public final int F2 = 31;
    static public final int F3 = 32;
    static public final int F4 = 33;
    static public final int F5 = 34;
    static public final int F6 = 35;
    static public final int F7 = 36;
    static public final int F8 = 37;
    static public final int F9 = 38;
    static public final int F10 = 39;
    static public final int F11 = 40;
    static public final int F12 = 41;

    static public final int WINDOWS = 50;
    static public final int CONTEXT_MENU = 51;

    static public final int SHIFT = 60;
    static public final int CONTROL = 61;
    static public final int LEFT_ALT = 62;
    static public final int RIGHT_ALT = 63;

    static public final int ALTERNATIVE_ARROW_DOWN = 70;
    static public final int ALTERNATIVE_ARROW_UP = 71;
    static public final int ALTERNATIVE_ARROW_LEFT = 72;
    static public final int ALTERNATIVE_ARROW_RIGHT = 73;
    static public final int ALTERNATIVE_HOME = 74;
    static public final int ALTERNATIVE_END = 75;
    static public final int ALTERNATIVE_PAGE_UP = 76;
    static public final int ALTERNATIVE_PAGE_DOWN = 77;
    static public final int ALTERNATIVE_DELETE = 78;

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
    /**@todo It is better to rename it to equalKeysOnKeyboard()*/
    public boolean equals(KeyboardEvent event)
    {
	return (cmd == event.cmd &&
		(!cmd || cmdCode == event.cmdCode) &&
		(cmd || EqualKeys.equalKeys(nonCmdChar, event.nonCmdChar)) &&
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
