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

public class ListUtils
{
    static public class DefaultHotPointMoves implements ListArea.HotPointMoves 
    {
	@Override public int numberOfEmptyLinesTop()
	{
	    return 1;
	}

	    @Override public int numberOfEmptyLinesBottom()
	    {
		return 1;
	    }

	    @Override public int oneLineUp(int index, int modelItemCount)
	    {
		System.out.println(index);
		return index > 0?index - 1:0;
	    }

	    @Override public int oneLineDown(int index, int modelItemCount)
	    {
		return (index < modelItemCount + 1)?index + 1:index;
	    }
	}
    }
