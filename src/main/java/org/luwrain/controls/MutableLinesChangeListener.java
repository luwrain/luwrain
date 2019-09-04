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

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;

abstract public class MutableLinesChangeListener implements MutableLines
{
    protected final MutableLines lines;

    public MutableLinesChangeListener(MutableLines lines)
    {
	NullCheck.notNull(lines, "lines");
	this.lines = lines;
    }

    abstract public void onMutableLinesChange();
	
    @Override public void beginLinesTrans()
    {
    }

    @Override public void endLinesTrans()
    {
    }

    @Override public int getLineCount()
    {
	return lines.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return lines.getLine(index);
    }

    @Override public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	this.lines.setLines(lines);
	onMutableLinesChange();
	    }

    @Override public String[] getLines()
    {
	return lines.getLines();
    }

    @Override public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	lines.setLine(index, line);
	onMutableLinesChange();
	    }

    @Override public void addLine(String line)
    {
	NullCheck.notNull(line, "line");
	lines.addLine(line);
	onMutableLinesChange();
	    }

    //index is the position of newly inserted line
    @Override public void insertLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	lines.insertLine(index, line);
	onMutableLinesChange();
	    }

    @Override public void removeLine(int index)
    {
	lines.removeLine(index);
	onMutableLinesChange();
	    }

    @Override public void clear()
    {
	lines.clear();
	onMutableLinesChange();
    }
}
