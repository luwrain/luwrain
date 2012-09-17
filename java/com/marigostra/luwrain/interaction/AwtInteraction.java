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

package com.marigostra.luwrain.interaction;

import java.awt.*;
import java.awt.event.*;
import com.marigostra.luwrain.core.events.KeyboardEvent;

public class AwtInteraction implements com.marigostra.luwrain.core.Interaction
{
    private Frame frame;
    private boolean acceptingInputEvents = false;
    private boolean leftAltPressed = false;
    private boolean rightAltPressed = false;
    private boolean controlPressed = false;
    private boolean shiftPressed = false;


    private void onKeyPress(KeyEvent event)
    {
	if (!acceptingInputEvents)
	    return;
	int code;
	switch (event.getKeyCode())
	{
	    //Functions keys;
	case KeyEvent.VK_F1:
	    code = KeyboardEvent.F1;
	    break;
	case KeyEvent.VK_F2:
	    code = KeyboardEvent.F2;
	    break;
	case KeyEvent.VK_F3:
	    code = KeyboardEvent.F3;
	    break;
	case KeyEvent.VK_F4:
	    code = KeyboardEvent.F4;
	    break;
	case KeyEvent.VK_F5:
	    code = KeyboardEvent.F5;
	    break;
	case KeyEvent.VK_F6:
	    code = KeyboardEvent.F6;
	    break;
	case KeyEvent.VK_F7:
	    code = KeyboardEvent.F7;
	    break;
	case KeyEvent.VK_F8:
	    code = KeyboardEvent.F8;
	    break;
	case KeyEvent.VK_F9:
	    code = KeyboardEvent.F9;
	    break;
	case KeyEvent.VK_F10:
	    code = KeyboardEvent.F10;
	    break;
	case KeyEvent.VK_F11:
	    code = KeyboardEvent.F11;
	    break;
	case KeyEvent.VK_F12:
	    code = KeyboardEvent.F12;
	    break;
	    //Arrows;
	case KeyEvent.VK_LEFT:
	    code = KeyboardEvent.ARROW_LEFT;
	    break;
	case KeyEvent.VK_RIGHT:
	    code = KeyboardEvent.ARROW_RIGHT;
	    break;
	case KeyEvent.VK_UP:
	    code = KeyboardEvent.ARROW_UP;
	    break;
	case KeyEvent.VK_DOWN:
	    code = KeyboardEvent.ARROW_DOWN;
	    break;
	    //Jump keys;
	case KeyEvent.VK_HOME:
	    code = KeyboardEvent.HOME;
	    break;
	case KeyEvent.VK_END:
	    code = KeyboardEvent.END;
	    break;
	case KeyEvent.VK_INSERT:
	    code = KeyboardEvent.INSERT;
	    break;
	case KeyEvent.VK_PAGE_DOWN:
	    code = KeyboardEvent.PAGE_DOWN;
	    break;
	case KeyEvent.VK_PAGE_UP:
	    code = KeyboardEvent.PAGE_UP;
	    break;
	case KeyEvent.VK_WINDOWS:
	    code = KeyboardEvent.WINDOWS;
	    break;
	case KeyEvent.VK_CONTEXT_MENU:
	    code = KeyboardEvent.CONTEXT_MENU;
	    break;
	    //Modifiers;
	case KeyEvent.VK_ALT:
	    leftAltPressed = true;
	    code = KeyboardEvent.LEFT_ALT;
	    break;
	case KeyEvent.VK_ALT_GRAPH:
	    rightAltPressed = true;
	    code = KeyboardEvent.RIGHT_ALT;
	    break;
	case KeyEvent.VK_CONTROL:
	    controlPressed = true;
	    code = KeyboardEvent.CONTROL;
	    break;
	case KeyEvent.VK_SHIFT:
	    shiftPressed = true;
	    code = KeyboardEvent.SHIFT;
	    break;
	default:
	    return;
	}
	com.marigostra.luwrain.core.Environment.enqueueEvent(new KeyboardEvent(true, code, ' ', shiftPressed, controlPressed, leftAltPressed, rightAltPressed));
    }

    private void onKeyRelease(KeyEvent event)
    {
	if (!acceptingInputEvents)
	    return;
	switch(event.getKeyCode())
	{
	case KeyEvent.VK_ALT:
	    leftAltPressed = false;
	    return;
	case KeyEvent.VK_ALT_GRAPH:
	    rightAltPressed = false;
	    return;
	case KeyEvent.VK_CONTROL:
	    controlPressed = false;
	    return;
	case KeyEvent.VK_SHIFT:
	    shiftPressed = false;
	    return;
	default:
	    return;
	}
    }

    private void onKeyTyping(KeyEvent event)
    {
	if (!acceptingInputEvents)
	    return;
	int code;
	switch (event.getKeyChar())
	{
	case KeyEvent.VK_BACK_SPACE:
	    code = KeyboardEvent.BACKSPACE;
	    break;
	case KeyEvent.VK_ENTER:
	    code = KeyboardEvent.ENTER;
	    break;
	case KeyEvent.VK_ESCAPE:
	    code = KeyboardEvent.ESCAPE;
	    break;
	case KeyEvent.VK_DELETE:
	    code = KeyboardEvent.DELETE;
	    break;
	case KeyEvent.VK_TAB:
	    code = KeyboardEvent.TAB;
	    break;
	default:
	    com.marigostra.luwrain.core.Environment.enqueueEvent(new KeyboardEvent(false, 0, event.getKeyChar(), shiftPressed, controlPressed, leftAltPressed, rightAltPressed));
	    return;
	}
	com.marigostra.luwrain.core.Environment.enqueueEvent(new KeyboardEvent(true, code, ' ', shiftPressed, controlPressed, leftAltPressed, rightAltPressed));
    }

    public void init()
    {
	frame = new Frame("Java AWT Frame");
	frame.setSize(400,400);
	frame.setFocusTraversalKeysEnabled(false);
	frame.addKeyListener(new KeyListener() {
		public void              keyPressed(KeyEvent event)
		{
		    onKeyPress(event);
		}
		public void              keyReleased(KeyEvent event)
		{
		    onKeyRelease(event);
		}
		public void              keyTyped(KeyEvent event)
		{
		    onKeyTyping(event);
		}
	    });
	frame.setVisible(true);                                                    
    }

    public void startInputEventsAcception()
    {
	acceptingInputEvents = true;
    }

    public void stopInputEventsAccepting()
    {
	acceptingInputEvents = false;
    }

    public void close()
    {
    }
}
