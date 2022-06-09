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

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;

public class UndoLines implements MutableLines 
{
    protected final MutableLines lines;
    protected final List<Command> commands = new ArrayList<>();

    public UndoLines(MutableLines lines)
    {
	NullCheck.notNull(lines, "lines");
	this.lines = lines;
    }

    @Override public int getLineCount()
    {
	return lines.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return lines.getLine(index);
    }

    @Override public void update(Updating updating)
    {
	//FIXME:
    }

    @Override public String[] getLines()
    {
	return lines.getLines();
    }

    @Override public void setLines(String[] lines)
    {
    }

    @Override public void addLine(String line)
    {
	NullCheck.notNull(line, "line");
	final Command cmd = new AddLine(lines, line);
	cmd.redo(lines);
	saveCommand(cmd);
    }

    @Override public void insertLine(int index, String line)
    {
	//FIXME:
    }

    @Override public void removeLine(int index)
    {
	//New command will take care about index bounds
	final Command cmd = new RemoveLine(lines, index);
	cmd.redo(lines);
	saveCommand(cmd);
    }

    @Override public void setLine(int index, String line)
    {
    }

    @Override public void clear()
    {
    }

    @Override public LineMarks getLineMarks(int index)
    {
	return null;
    }

    @Override public void setLineMarks(int index, LineMarks lineMarks)
    {
    }

    protected void saveCommand(Command command)
    {
	NullCheck.notNull(command, "command");
	commands.add(command);
    }

    static protected abstract class Command
    {
	public abstract void redo(MutableLines lines);
	public abstract void undo(MutableLines lines);
    }

    static protected final class AddLine extends Command
    {
	private final int addedLineIndex;
	private final String line;
	public AddLine(MutableLines lines, String line)
	{
	    NullCheck.notNull(lines, "lines");
	    NullCheck.notNull(line, "line");
	    this.addedLineIndex = lines.getLineCount();
	    this.line = line;
	}
	@Override public void redo(MutableLines lines)
	{
	    NullCheck.notNull(lines, "lines");
	    lines.addLine(line);
	}
	@Override public void undo(MutableLines lines)
	{
	    NullCheck.notNull(lines, "lines");
	    lines.removeLine(addedLineIndex);
	}
    }

        static protected final class RemoveLine extends Command
    {
	private final int removingLineIndex;
	private final String line;
	public RemoveLine(MutableLines lines, int index)
	{
	    NullCheck.notNull(lines, "lines");
	    if (index < 0 || index >= lines.getLineCount())
		throw new IllegalArgumentException("index (" + String.valueOf(index) + ") must be non-negative and less than " + String.valueOf(lines.getLineCount()));
	    this.removingLineIndex = lines.getLineCount();
	    this.line = lines.getLine(index);
	}
	@Override public void redo(MutableLines lines)
	{
	    NullCheck.notNull(lines, "lines");
	    lines.removeLine(removingLineIndex);
	}
	@Override public void undo(MutableLines lines)
	{
	    NullCheck.notNull(lines, "lines");
	    lines.insertLine(removingLineIndex, line);
	}
    }

}
