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

package org.luwrain.script.controls;

import java.util.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.script.core.*;
//import org.luwrain.util.*;
//import org.luwrain.controls.MultilineEdit.ModificationResult;

import static org.luwrain.core.NullCheck.*;
import static org.luwrain.script.ScriptUtils.*;

public class WizardAreaObj extends WizardArea
{
    final org.luwrain.script.core.Module module;
    final Value onInput;

    public WizardAreaObj(ControlContext context, org.luwrain.script.core.Module module, Value onInput)
    {
	super(context);
	notNull(module, "module");
	this.module = module;
	this.onInput = onInput;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	notNull(event, "event");
	if (onInput != null)
	{
	    final var res = onInput.execute(new InputEventObj(event));
	    if (res != null && !res.isNull() && res.isBoolean() && res.asBoolean())
		return true;
	}
	return super.onInputEvent(event);
    }

    @HostAccess.Export public ProxyExecutable createFrame = this::createFrameImpl;
    public Object createFrameImpl(Value[] args)
    {
	final var frame = newFrame();
	return new FrameObj(frame);
    }

    public final class FrameObj
    {
	final Frame frame;
	FrameObj(Frame frame)
	{
	    notNull(frame, "frame");
	    this.frame = frame;
	}
	@HostAccess.Export public ProxyExecutable addText = this::addTextImpl;
	public Object addTextImpl(Value[] args)
	{
	    if (!notNullAndLen(args, 1) || !args[0].isString())
		throw new IllegalArgumentException("Frame.addText() takes exactly one string argument");
	    this.frame.addText(args[0].asString());
	    return this;
	}

        @HostAccess.Export public ProxyExecutable addInput = this::addInputImpl;
	public Object addInputImpl(Value[] args)
	{
	    if (args == null || args.length == 0)
		throw new IllegalArgumentException("The frame function addInput() can't be called without arguments");
	    if (args[0] == null || !args[0].isString())
		throw new IllegalArgumentException("The frame function addInput() takes a string as the first argument");
	    if (args.length == 1)
	    {
		this.frame.addInput(args[0].asString(), "");
		return this;
	    }
	    if (args[1] == null || !args[1].isString())
		throw new IllegalArgumentException("The frame function addInput() takes a string as the second argument");
	    this.frame.addInput(args[0].asString(), args[1].asString());
	    return this;
	}

	@HostAccess.Export public ProxyExecutable addClickable = this::addClickableImpl;
	public Object addClickableImpl(Value[] args)
	{
	    if (args == null || args.length != 2)
		throw new IllegalArgumentException("The frame function addClickable() takes two arguments: the text and the handler");
	    if (args[0] == null || !args[0].isString())
		throw new IllegalArgumentException("The frame function addClickable() takes a string as the first argument");
	    if (args[1] == null || !args[1].canExecute())
		throw new IllegalArgumentException("The frame function addClickable() takes a function as the second argument");
	    this.frame.addClickable(args[0].asString(), (values)->{
		    module.execFuncValue(args[1]);
		    return true;
		});
	    return this;
	}
	@HostAccess.Export public ProxyExecutable show = this::showImpl;
	private Object showImpl(Value[] args)
	{
	    WizardAreaObj.this.show(frame);
	    return null;
	}
    }
}
