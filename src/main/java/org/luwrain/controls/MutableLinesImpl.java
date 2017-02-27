/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls;

import java.util.Vector;

import org.luwrain.core.*;

public class MutableLinesImpl implements MutableLines
{
    protected final Vector<String> lines = new Vector<String>();

    public MutableLinesImpl()
    {
    }

    public MutableLinesImpl(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	this.lines.setSize(lines.length);
	for(int i = 0;i < lines.length;++i)
	    this.lines.set(i, lines[i]);
    }

    public MutableLinesImpl(String lines)
    {
	NullCheck.notNull(lines, "lines");
	if (lines.isEmpty())
	    return;
	final String[] l = lines.split("\n", -1);
	this.lines.setSize(l.length);
	for(int i = 0;i < l.length;++i)
	    this.lines.set(i, l[i]);
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

    public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	this.lines.setSize(lines.length);
	for(int i = 0;i < lines.length;++i)
	    this.lines.set(i, lines[i]);
    }

    public String[] getLines()
    {
	return lines.toArray(new String[lines.size()]);
    }

    @Override public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index >= 0 && index < lines.size())
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
	if (index < lines.size())
	{
	    lines.insertElementAt(line, index);
	    return;
	}
	if (lines.size() == index)
	lines.add(line);
    }

    @Override public void removeLine(int index)
    {
	if (index < 0 || index >= lines.size())
	    return;
	lines.remove(index);
    }

    public void clear()
    {
	lines.clear();
    }

    public String getWholeText()
    {
	if (lines.size() == 0)
	    return "";
	if (lines.size() == 1)
	    return lines.get(0);
	final StringBuilder res = new StringBuilder();
	res.append(lines.get(0));
	for(int i = 1;i < lines.size();++i)
	    res.append("\n" + lines.get(i));
	return new String(res);
    }
}
