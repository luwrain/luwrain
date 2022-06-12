/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.script.core;

import java.util.*;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;

public class MutableLinesArray implements ProxyArray
{
    protected final MutableLines lines;

    public MutableLinesArray(MutableLines lines)
    {
	NullCheck.notNull(lines, "lines");
	this.lines = lines;
    }

    @Override public Object get(long index)
    {
	if (index < 0 || index >= lines.getLineCount())
	    throw new ArrayIndexOutOfBoundsException((int)index);
	return lines.getLine((int)index);
    }

    @Override public long getSize()
    {
	return lines.getLineCount();
    }

    @Override public void set(long index, Value value)
    {
	if (value == null || value.isNull())
	    return;
	final String v;
	if (value.isString())
	    v = value.asString(); else
	    v = value.toString();
	if (v == null)
	    return;
		if (index < 0 || index >= lines.getLineCount())
	    throw new ArrayIndexOutOfBoundsException((int)index);
		lines.setLine((int)index, v);

	
	
	 }

    @Override public boolean remove(long index)
    {
	if (index < 0 || index >= lines.getLineCount())
	    return false;
	lines.removeLine((int)index);
	return true;
	    }
}
