/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

public class MutableLinesImpl implements MutableLines
{
    protected final ArrayList<String> lines = new ArrayList<String>();

    public MutableLinesImpl()
    {
    }

    public MutableLinesImpl(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	this.lines.addAll(Arrays.asList(lines));
    }

    @Override public void beginLinesTrans()
    {
    }

    @Override public void endLinesTrans()
    {
    }

    @Override public int getLineCount()
    {
	return lines.size();
    }

    @Override public String getLine(int index)
    {
	if (index < 0 || index >= lines.size())
	    return "";
	return lines.get(index);
    }

    @Override public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	this.lines.clear();
	this.lines.addAll(Arrays.asList(lines));
    }

    @Override public String[] getLines()
    {
	return lines.toArray(new String[lines.size()]);
    }

    @Override public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < 0)
	    throw new IllegalArgumentException("index (" + String.valueOf(index) + " can't be negative");
	if (index >= lines.size())
	    throw new IllegalArgumentException("index (" + String.valueOf(index) + ") can't be greater or equal to line count (" + String.valueOf(lines.size()) + ")");
	lines.set(index, line);
    }

    @Override public void addLine(String line)
    {
	NullCheck.notNull(line, "line");
	lines.add(line);
    }

    //index is the position of newly inserted line
    @Override public void insertLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < 0 || index > lines.size())
	    throw new IllegalArgumentException("Illegal index value (" + index + ")");
	if (index < lines.size())
	    lines.add(index, line); else
	lines.add(line);
    }

    @Override public void removeLine(int index)
    {
	if (index < 0 || index >= lines.size())
	    throw new IllegalArgumentException("Invalid index (" + index + ")");
	lines.remove(index);
    }

    @Override public void clear()
    {
	lines.clear();
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
	if (lines.size() == 0)
	    return "";
	if (lines.size() == 1)
	    return lines.get(0);
	final StringBuilder res = new StringBuilder();
	res.append(lines.get(0));
	for(int i = 1;i < lines.size();++i)
	    res.append(s).append(lines.get(i));
	return new String(res);
    }

    public String getWholeText()
    {
	return getWholeText(null);
    }

    public List<String> asList()
    {
	return lines;
    }
}
