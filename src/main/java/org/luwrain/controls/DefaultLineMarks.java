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

package org.luwrain.core;

public class DefaultLineMarks implements LineMarks
{
    final Mark[] marks;
    DefaultLineMarks(Mark[] marks)
    {
	NullCheck.notNullItems(marks, "marks");
	this.marks = marks.clone();
    }
    @Override public Mark[] getMarks()
    {
	return this.marks.clone();
    }

    public final class MarkImpl implements Mark
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
