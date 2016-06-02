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

import java.util.*;

import org.luwrain.core.NullCheck;

public class ListUtils
{
    static public class DefaultHotPointMoves implements ListArea.HotPointMoves 
    {
	protected boolean hasEmptyLineTop = false;

	@Override public int numberOfEmptyLinesTop()
	{
	    return hasEmptyLineTop?1:0;
	}

	    @Override public int numberOfEmptyLinesBottom()
	    {
		return 1;
	    }

	    @Override public int oneLineUp(int index, int modelItemCount)
	    {
		return index > 0?index - 1:0;
	    }

	    @Override public int oneLineDown(int index, int modelItemCount)
	    {
		final int count = modelItemCount + (hasEmptyLineTop?1:0) + 1;
		return (index + 1 < count)?index + 1:index;
	    }

	@Override public void setFlags(Set<ListArea.Flags> flags)
	{
	    NullCheck.notNull(flags, "flags");
	    hasEmptyLineTop = flags.contains(ListArea.Flags.EMPTY_LINE_TOP);
	}
	}
    }
