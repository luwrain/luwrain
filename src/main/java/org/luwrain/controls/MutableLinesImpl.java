/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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
import org.luwrain.core.NullCheck;
import org.luwrain.core.MutableLines;
//import org.luwrain.util.Strings;

public class MutableLinesImpl implements MutableLines
{
    protected final Vector<String> lines = new Vector<String>();

    public MutableLinesImpl()
    {
    }

    public MutableLinesImpl(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	final String[] l = lines;
	for(String ll: l)
	    this.lines.add(ll);
    }

    public MutableLinesImpl(String lines)
    {
	NullCheck.notNull(lines, "lines");
	final String[] l = lines.split("\n", -1);
	for(String ll: l)
	    this.lines.add(ll);
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
	final String[] l = lines;
	this.lines.clear();
	for(String ll: l)
	    this.lines.add(ll);
    }

    public String[] getLines()
    {
	return lines.toArray(new String[lines.size()]);
    }

    @Override public void setLine(int index, String line)
    {
	while(lines.size() <= index)
	    lines.add("");
	lines.set(index, line != null?line:"");
    }

    @Override public void addLine(String line)
    {
	lines.add(line != null?line:"");
    }

    //index is the position of newly inserted line
    @Override public void insertLine(int index, String line)
    {
	if (index < lines.size())
	{
	    lines.insertElementAt(line != null?line:"", index);
	    return;
	}
	while(lines.size() < index)
	    lines.add("");
	lines.add(line != null?line:"");
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
	if (lines.size() < 1)
	    return "";
	if (lines.size() == 1)
	    return lines.get(0);
	final StringBuilder res = new StringBuilder();
	res.append(lines.get(0));
	for(int i = 1;i < lines.size();++i)
	    res.append("\n" + lines.get(i));
	return res.toString();
    }
}
