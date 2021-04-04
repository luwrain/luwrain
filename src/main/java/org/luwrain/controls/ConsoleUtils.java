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

public final class ConsoleUtils
{
        static public class ArrayModel implements ConsoleArea.Model
    {
	public interface Source
	{
	    Object[] getItems();
	}
	protected final Source source;
	public ArrayModel(Source source)
	{
	    NullCheck.notNull(source, "source");
	    this.source = source;
	}
	@Override public int getItemCount()
	{
	    final Object[] o = source.getItems();
	    return o != null?o.length:0;
	}
	@Override public Object getItem(int index)
	{
	    final Object[] o = source.getItems();
	    if (o == null)
		return "#No items#";
	    if (index < 0 || index >= o.length)
		return "#Illegal index: " + String.valueOf(index) + "#";
	    return o[index];
	}
	}

            static public class ListModel implements ConsoleArea.Model
    {
	protected final List source;
	public ListModel(List source)
	{
	    NullCheck.notNull(source, "source");
	    this.source = source;
	}
	@Override public int getItemCount()
	{
	    return source.size();
	}
	@Override public Object getItem(int index)
	{
	    if (index < 0 || index >= source.size())
		return "#Illegal index: " + String.valueOf(index) + "#";
	    return source.get(index);
	}
    }

}
