/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.concurrent.*;
import javax.script.*;
import jdk.nashorn.api.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

public class MutableLinesWrapper extends EmptyHookObject
{
    private final MutableLines lines;

    public MutableLinesWrapper(MutableLines lines)
    {
	NullCheck.notNull(lines, "lines");
	this.lines = lines;
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
	if (obj == null)
	    return;
	final String value = obj.toString();
	if (value == null)
	    return;
	if (index >= 0 && index < lines.getLineCount())
	    lines.setLine(index, value);
    }

    @Override public boolean isArray()
    {
	return true;
    }


    
}
