/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls.edit;

import java.util.*;
import org.luwrain.core.*;
import static org.luwrain.core.NullCheck.*;

public interface MultilineCorrector extends MultilineEdit.Model
{
    public enum ChangeType {
	DELETE_CHAR,
	DELETE_FRAGMENT ,
	INSERT_CHARS,
	INSERT_FRAGMENT,
	MERGE_LINES,
	SPLIT_LINE};

    public interface Model extends MutableLines, HotPointControl
    {
	void change(Change c);
    }

    void change(Change c);

    static public class Change
    {
	protected final ChangeType type;
	protected final int line, pos;
	public Change(ChangeType type, int line, int pos)
	{
	    notNull(type, "type");
	    if (line < 0)
		throw new IllegalArgumentException("line can't be negative (" + String.valueOf(line) + ")");
	    if (pos < 0)
		throw new IllegalArgumentException("pos can't be negative (" + String.valueOf(pos) + ")");
	    this.type = type;
	    this.line = line;
	    this.pos = pos;
	}
	public ChangeType getType() { return type; }
	public int getLine() { return line; }
	public int getPos() { return pos; }
    }

    static public final class DeleteCharChange extends Change
    {
	public DeleteCharChange(int line, int pos) { super(ChangeType.DELETE_CHAR, line, pos); }
    }

    static public final class DeleteFragmentChange extends Change
    {
	protected final int lineTo, posTo;
	public DeleteFragmentChange(int line, int pos, int lineTo, int posTo)
	{
	    super(ChangeType.DELETE_FRAGMENT, line, pos);
	    this.lineTo = lineTo;
	    this.posTo = posTo;
	}
    }

    static public final class InsertFragmentChange extends Change
    {
	protected final List<String> text;
	public InsertFragmentChange(int line, int pos, List<String> text)
	{
	    super(ChangeType.INSERT_FRAGMENT, line, pos);
	    this.text = text;
	}
	public List<String> getText() { return text; }

	static public final class InsertCharsChange extends Change
	{
	    protected final String chars;
	    public InsertCharsChange(int line, int pos, String chars)
	    {
		super(ChangeType.INSERT_CHARS, line, pos);
		notEmpty(chars, "chars");
		this.chars = chars;
	    }
	    public String getChars() { return chars; }
	}
    }

    static public final class MergeLinesChange extends Change
    {
	public MergeLinesChange(int line, int pos) { super(ChangeType.MERGE_LINES, line, pos); }
    }

    static public final class SplitLineChange extends Change
    {
	public SplitLineChange(int line, int pos) { super(ChangeType.SPLIT_LINE, line, pos); }
    }
}
