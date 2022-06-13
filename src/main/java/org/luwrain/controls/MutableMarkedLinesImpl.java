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

public class MutableMarkedLinesImpl extends ArrayList<MutableMarkedLinesImpl.Line> implements MutableMarkedLines 
{
    public MutableMarkedLinesImpl() {}
    public MutableMarkedLinesImpl(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	for(String s: lines)
	    add(new Line(s));
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
	return get(index).text;
    }

    @Override public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	clear();
	ensureCapacity(lines.length);
	for(String l: lines)
	    add(new Line(l));
    }

    @Override public String[] getLines()
    {
	final ArrayList<String> res = new ArrayList<>();
	res.ensureCapacity(size());
	for(int i = 0;i < size();i++)
	    res.add(get(i).text);
	return res.toArray(new String[res.size()]);
    }

    @Override public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < 0)
	    throw new IllegalArgumentException("index (" + String.valueOf(index) + " can't be negative");
	if (index >= size())
	    throw new IllegalArgumentException("index (" + String.valueOf(index) + ") can't be greater or equal to line count (" + String.valueOf(size()) + ")");
	set(index, new Line(line));
    }

    @Override public void addLine(String line)
    {
	NullCheck.notNull(line, "line");
	add(new Line(line));
    }

    //index is the position of newly inserted line
    @Override public void insertLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < 0 || index > size())
	    throw new IllegalArgumentException("Illegal index value (" + index + ")");
	if (index < size())
	    add(index, new Line(line)); else
	    add(new Line(line));
    }

    @Override public void removeLine(int index)
    {
	if (index < 0 || index >= size())
	    throw new IllegalArgumentException("Invalid index (" + index + ")");
	remove(index);
    }

    @Override public LineMarks getLineMarks(int index)
    {
	return get(index).marks;
    }

    @Override public void setLineMarks(int index, LineMarks marks)
    {
	get(index).marks = marks;
    }

    public String getText(String lineSep)
    {
	final String s = lineSep != null?lineSep:System.lineSeparator();
	if (size() == 0)
	    return "";
	if (size() == 1)
	    return get(0).text;
	final StringBuilder res = new StringBuilder();
	res.append(get(0).text);
	for(int i = 1;i < size();++i)
	    res.append(s).append(get(i).text);
	return new String(res);
    }

    static public final class Line
    {
final String text;
	LineMarks marks = null;
	public Line(String text)
	{
	    NullCheck.notNull(text, "text");
	    this.text = text;
	}
    }
}
