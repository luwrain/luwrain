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

public class DefaultLineMarks implements LineMarks
{
    final Mark[] marks;
    DefaultLineMarks(Mark[] marks)
    {
	NullCheck.notNullItems(marks, "marks");
	this.marks = marks;
    }
    @Override public Mark[] getMarks()
    {
	return this.marks.clone();
    }

    static public final class Builder
    {
	private final List<LineMarks.Mark> res = new ArrayList<>();
	public Builder(LineMarks marks)
	{
	    if (marks != null)
	    {
		final LineMarks.Mark[] newMarks = marks.getMarks();
		if (newMarks != null)
		    res.addAll(Arrays.asList(newMarks));
	    }
	}
	public Builder add(LineMarks.Mark mark)
	{
	    NullCheck.notNull(mark, "mark");
	    res.add(mark);
	    return this;
	}
	public Builder addAll(List<LineMarks.Mark> marks)
	{
	    NullCheck.notNull(marks, "marks");
	    res.addAll(marks);
	    return this;
	}
	public Builder addAll(LineMarks.Mark[] marks)
	{
	    NullCheck.notNullItems(marks, "marks");
	    res.addAll(Arrays.asList(marks));
	    return this;
	}
	public DefaultLineMarks build()
	{
	    return new DefaultLineMarks(res.toArray(new LineMarks.Mark[res.size()]));
	}
    }

    static public final class MarkImpl implements LineMarks.Mark
    {
	final Type type;
	final int posFrom, posTo;
	final MarkObject obj;
	public MarkImpl(Type type, int posFrom, int posTo, MarkObject obj)
	{
	    NullCheck.notNull(type, "type");
	    this.type = type;
	    this.posFrom = posFrom;
	    this.posTo = posTo;
	    this.obj = obj;
	}
	@Override public Type getType() { return type; }
	@Override public int getPosFrom() { return posFrom; }
	@Override public int getPosTo() { return posTo; }
	@Override public MarkObject getMarkObject() { return obj; }
    }
}
