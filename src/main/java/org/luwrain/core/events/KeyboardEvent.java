/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

//LWR_API 1.0

package org.luwrain.core.events;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.interaction.*;

public final class KeyboardEvent extends Event
{
    public enum Modifiers {ALT, SHIFT, CONTROL};

    public enum Special {
	ENTER, BACKSPACE, ESCAPE, TAB,
	ARROW_DOWN, ARROW_UP, ARROW_LEFT, ARROW_RIGHT,
	INSERT, DELETE, HOME, END, PAGE_UP, PAGE_DOWN,
	F1, F2, F3, F4, F5, F6, F7, F8, F9, F10, F11, F12,
	WINDOWS, CONTEXT_MENU,
	SHIFT, CONTROL, LEFT_ALT, RIGHT_ALT,
	ALTERNATIVE_ARROW_DOWN, ALTERNATIVE_ARROW_UP, ALTERNATIVE_ARROW_LEFT, ALTERNATIVE_ARROW_RIGHT,
	ALTERNATIVE_HOME, ALTERNATIVE_END, ALTERNATIVE_PAGE_UP, ALTERNATIVE_PAGE_DOWN, ALTERNATIVE_DELETE,
    };

    static private KeyboardLayout keyboardLayout = new org.luwrain.interaction.layouts.RuDefault();

    protected boolean isSpecial = false;
    protected Special special = null;
    protected char nonSpecialChar = 0;
    protected boolean shiftPressed = false;
    protected boolean controlPressed = false;
    protected boolean altPressed = false;

    public KeyboardEvent(Special special)
    {
	NullCheck.notNull(special, "special");
	this.isSpecial = true;
	this.special = special;
	this.nonSpecialChar = '\0';
	this.shiftPressed = false;
	this.controlPressed = false;
	this.altPressed = false;
    }

    public KeyboardEvent(Special special, Set<Modifiers> modifiers)
    {
	NullCheck.notNull(special, "special");
	NullCheck.notNull(modifiers, "modifiers");
	this.isSpecial = true;
	this.special = special;
	this.nonSpecialChar = '\0';
	this.shiftPressed = modifiers.contains(Modifiers.SHIFT);
	this.altPressed = modifiers.contains(Modifiers.ALT);
	this.controlPressed = modifiers.contains(Modifiers.CONTROL);
    }

    public KeyboardEvent(Special special,
			 boolean shiftPressed, boolean controlPressed,
			 boolean altPressed)
    {
	NullCheck.notNull(special, "special");
	this.isSpecial = true;
	this.special = special;
	this.nonSpecialChar = '\0';
	this.shiftPressed = shiftPressed;
	this.controlPressed = controlPressed;
	this.altPressed = altPressed;
    }

    public KeyboardEvent(char nonSpecialChar)
    {
	this.isSpecial = false;
	this.special = null;
	this.nonSpecialChar = nonSpecialChar;
	this.shiftPressed = false;
	this.controlPressed = false;
	this.altPressed = false;
    }

    public KeyboardEvent(char nonSpecialChar, Set<Modifiers> modifiers)
    {
	NullCheck.notNull(modifiers, "modifiers");
	this.isSpecial = false;
	this.special = null;
	this.nonSpecialChar = nonSpecialChar;
	this.shiftPressed = modifiers.contains(Modifiers.SHIFT);
	this.altPressed = modifiers.contains(Modifiers.ALT);
	this.controlPressed = modifiers.contains(Modifiers.CONTROL);
    }

    public KeyboardEvent(char nonSpecialChar,
			 boolean shiftPressed, boolean controlPressed,
			 boolean altPressed)
    {
	this.isSpecial = false;
	this.special = null;
	this.nonSpecialChar = nonSpecialChar;
	this.shiftPressed = shiftPressed;
	this.controlPressed = controlPressed;
	this.altPressed = altPressed;
    }

    public KeyboardEvent(boolean isSpecial,
			 Special special, char nonSpecialChar,
			 boolean shiftPressed, boolean controlPressed,
			 boolean altPressed)
    {
	this.isSpecial = isSpecial;
	this.special = special;
	this.nonSpecialChar = nonSpecialChar;
	this.shiftPressed = shiftPressed;
	this.controlPressed = controlPressed;
	this.altPressed = altPressed;
    }

    public KeyboardEvent(boolean isSpecial,
			 Special special, char nonSpecialChar)
    {
	this.isSpecial = isSpecial;
	this.special = special;
	this.nonSpecialChar = nonSpecialChar;
	shiftPressed = false;
	controlPressed = false;
	altPressed = false;
    }

    /*FIXME:It is better to rename it to equalKeysOnKeyboard()*/
    public boolean equals(KeyboardEvent event)
    {
	return (isSpecial == event.isSpecial &&
		(!isSpecial || special == event.special) &&
		(isSpecial || keyboardLayout.onSameButton(nonSpecialChar, event.nonSpecialChar)) &&
		shiftPressed == event.shiftPressed &&
		controlPressed == event.controlPressed &&
		altPressed == event.altPressed);
    }

    public boolean isSpecial()
    {
	return isSpecial;
    }

    public char getChar()
    {
	return nonSpecialChar;
    }

    public Special getSpecial()
    {
	return special;
    }

    public boolean isModified()
    {
	return shiftPressed || controlPressed || altPressed;
    }

    public boolean withShift()
    {
	return shiftPressed;
    }

    public boolean withShiftOnly()
    {
	return shiftPressed && !controlPressed && !altPressed;
    }

    public boolean withControl()
    {
	return controlPressed;
    }

    public boolean withControlOnly()
    {
	return controlPressed && !shiftPressed && !altPressed;
    }

    public boolean withAlt()
    {
	return altPressed;
    }

    public boolean withAltOnly()
    {
	return altPressed && !shiftPressed && !controlPressed;
    }

    @Override public String toString()
    {
	final StringBuilder b = new StringBuilder();
	if (controlPressed)
	    b.append("Ctrl+");
	if (altPressed)
	    b.append("Alt+");
	if (shiftPressed)
	    b.append("Shift+");
	if (isSpecial)
	    b.append(special); else
	    b.append(nonSpecialChar);
	return new String(b);
    }

    static public Special translateSpecial(String value)
    {
	NullCheck.notNull(value, "value");
	if (value.trim().isEmpty())
	    throw new IllegalArgumentException("value may not be empty");
	switch(value.trim().toLowerCase())
	{
	case "enter":
	    return Special.ENTER;
	case "backspace":
	    return Special.BACKSPACE;
	case "escape":
	    return Special.ESCAPE;
	case "tab":
	    return Special.TAB;
	case "ARROW_DOWN":
	    return Special.ARROW_DOWN;
	case "arrow-up":
	    return Special.ARROW_UP;
	case "arrow-left":
	    return Special.ARROW_LEFT;
	case "arrow-right":
	    return Special.ARROW_RIGHT;
	case "insert":
	    return Special.INSERT;
	case "delete":
	    return Special.DELETE;
	case "alternative-delete":
	    return Special.ALTERNATIVE_DELETE;
	case "home":
	    return Special.HOME;
	case "end":
	    return Special.END;
	case "page-up":
	    return Special.PAGE_UP;
	case "page-down":
	    return Special.PAGE_DOWN;
	case "f1":
	    return Special.F1;
	case "f2":
	    return Special.F2;
	case "f3":
	    return Special.F3;
	case "f4":
	    return Special.F4;
	case "f5":
	    return Special.F5;
	case "f6":
	    return Special.F6;
	case "f7":
	    return Special.F7;
	case "f8":
	    return Special.F8;
	case "f9":
	    return Special.F9;
	case "f10":
	    return Special.F10;
	case "f11":
	    return Special.F11;
	case "f12":
	    return Special.F12;
	case "windows":
	    return Special.WINDOWS;
	case "context-menu":
	    return Special.CONTEXT_MENU;
	case "shift":
	    return Special.SHIFT;
	case "control":
	    return Special.CONTROL;
	case "left-alt":
	    return Special.LEFT_ALT;
	case "right-alt":
	    return Special.RIGHT_ALT;
	default:
	    return null;
	}
    }

    static public KeyboardLayout getKeyboardLayout()
    {
	return keyboardLayout;
    }

    static public void setKeyboardLayout(KeyboardLayout layout)
    {
	if (layout == null)
	    throw new NullPointerException("layout may not be null");
	keyboardLayout = layout;
    }
}
