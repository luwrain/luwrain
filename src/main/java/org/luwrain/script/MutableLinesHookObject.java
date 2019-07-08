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

package org.luwrain.script;

import java.util.*;
import java.util.function.*;

import org.luwrain.core.*;

public class MutableLinesHookObject extends EmptyHookObject
{
    protected final MutableLines lines;

    public MutableLinesHookObject(MutableLines lines)
    {
	NullCheck.notNull(lines, "lines");
	this.lines = lines;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notEmpty(name, "name");
	switch(name)
	{
	case "add":
	    return (Consumer)this::add;
	case "remove":
	    return (Consumer)this::remove;
	case "insert":
	    return (BiConsumer)this::insert;
	default:
	    return super.getMember(name);
	}
    }

    @Override public Object getSlot(int index)
    {
	if (index < 0 || index >= lines.getLineCount())
	    return null;
	return lines.getLine(index);
    }

    @Override public boolean hasSlot(int index)
    {
	return index >= 0 && index < lines.getLineCount();
    }

    @Override public void setSlot(int index, Object obj)
    {
	final String line = ScriptUtils.getStringValue(obj);
	if (line == null)
	    return;
	if (index >= 0 && index < lines.getLineCount())
	    lines.setLine(index, line);
    }

    @Override public boolean isArray()
    {
	return true;
    }

    protected void add(Object arg)
    {
	final String line = ScriptUtils.getStringValue(arg);
	if (line != null)
	    lines.addLine(line);
    }

protected void remove(Object arg)
    {
	final Integer index = ScriptUtils.getIntegerValue(arg);
	if (index != null && index.intValue() >= 0)
	    lines.removeLine(index.intValue());
    }

    protected void insert(Object arg1, Object arg2)
    {
    }


    
}
