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

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;

public class MutableLinesImpl extends ArrayList<String> implements MutableLines 
{
    //    protected final ArrayList<String> lines = new ArrayList<String>();

    public MutableLinesImpl()
    {
    }

    public MutableLinesImpl(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	addAll(Arrays.asList(lines));
    }

    @Override public void update(Updating updating)
    {
	NullCheck.notNull(updating, "updating");
	updating.update(this);
    }

    @Override public int getLineCount()
    {
	return size();
    }

    @Override public String getLine(int index)
    {
	if (index < 0 || index >= size())
	    return "";
	return get(index);
    }

    @Override public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	clear();
	addAll(Arrays.asList(lines));
    }

    @Override public String[] getLines()
    {
	return toArray(new String[size()]);
    }

    @Override public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < 0)
	    throw new IllegalArgumentException("index (" + String.valueOf(index) + " can't be negative");
	if (index >= size())
	    throw new IllegalArgumentException("index (" + String.valueOf(index) + ") can't be greater or equal to line count (" + String.valueOf(size()) + ")");
	set(index, line);
    }

    @Override public void addLine(String line)
    {
	NullCheck.notNull(line, "line");
	add(line);
    }

    //index is the position of newly inserted line
    @Override public void insertLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < 0 || index > size())
	    throw new IllegalArgumentException("Illegal index value (" + index + ")");
	if (index < size())
	    add(index, line); else
	    add(line);
    }

    @Override public void removeLine(int index)
    {
	if (index < 0 || index >= size())
	    throw new IllegalArgumentException("Invalid index (" + index + ")");
	remove(index);
    }

    @Override public LineMarks getLineMarks(int index)
    {
	return null;
    }

    @Override public void setLineMarks(int index, LineMarks lineMarks)
    {
	NullCheck.notNull(lineMarks, "lineMarks");
    }

    public String getWholeText(String lineSep)
    {
	final String s = lineSep != null?lineSep:System.lineSeparator();
	if (size() == 0)
	    return "";
	if (size() == 1)
	    return get(0);
	final StringBuilder res = new StringBuilder();
	res.append(get(0));
	for(int i = 1;i < size();++i)
	    res.append(s).append(get(i));
	return new String(res);
    }

    public String getWholeText()
    {
	return getWholeText(null);
    }
}
