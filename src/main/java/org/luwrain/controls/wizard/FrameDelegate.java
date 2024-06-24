/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls.wizard;

import java.util.*;

import groovy.lang.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.controls.WizardArea.*;

import static org.luwrain.core.NullCheck.*;

public class FrameDelegate
{
    final WizardGroovyController controller;
    final Frame frame;

    public FrameDelegate(WizardGroovyController controller, Frame frame)
    {
	notNull(controller, "controller");
	notNull(frame, "frame");
	this.controller = controller;
	this.frame = frame;
    }

    public void text(String value)
    {
	frame.addText(value);
    }

    public void input(String id, String title, String value)
    {
	notEmpty(id, "id");
	notNull(title, "title");
	frame.addInput(title, value != null?value:"");
    }

        public void input(String id, String title)
    {
	input(id, title, null);
    }

    public void button(String title, Closure closure)
    {
	notNull(title, "title");
	notNull(closure, "closure");
	final var d = new ButtonDelegate();
	closure.setDelegate(d);
	frame.addClickable(title, values -> {
		closure.call(values);
		return true;
	    });
    }

    public final class ButtonDelegate
    {
	public void show(String id)
	{
	    notEmpty(id, "id");
	    final Frame f = controller.frames.get(id);
	    if (f == null)
		throw new IllegalArgumentException("No such frame: " + id);
	    controller.area.show(f);
	}
	public void error(String message)
	{
	    notNull(message, "message");
	    controller.luwrain.message(message, Luwrain.MessageType.ERROR);
	}
    }
}
