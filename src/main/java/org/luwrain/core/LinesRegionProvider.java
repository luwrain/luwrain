/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

import java.util.*;
import org.luwrain.core.*;

public class LinesRegionProvider implements RegionProvider
{
    private Lines lines;

    public LinesRegionProvider(Lines lines)
    {
	this.lines = lines;
	NullCheck.notNull(lines, "lines");
    }

    @Override public HeldData getWholeRegion()
    {
	final LinkedList<String> res = new LinkedList<String>();
	final int count = lines.getLineCount();
	if (count < 1)
	    return null;
	for(int i = 0;i < count;++i)
	{
	    final String line = lines.getLine(i);
	    res.add(line != null?line:"");
	}
	return new HeldData(res.toArray(new String[res.size()]));
    }

    @Override public HeldData getRegion(int fromX, int fromY,
					int toX, int toY)
    {
	final int count = lines.getLineCount();
	if (count < 1)
	    return null;
	if (toY >= count)
	    return null;
	if (fromY == toY)
	{
	    final String line = lines.getLine(fromY);
	    if (line == null || line.isEmpty())
		return null;
	    final int fromPos = fromX < line.length()?fromX:line.length();
	    final int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		return null;
	    return new HeldData(new String[]{line.substring(fromPos, toPos)});
	}
	final LinkedList<String> res = new LinkedList<String>();
	String line = lines.getLine(fromY);
	if (line == null)
	    line = "";
	res.add(line.substring(fromX < line.length()?fromX:line.length()));
	for(int i = fromY + 1;i < toY;++i)
	{
	    line = lines.getLine(i);
	    if (line == null)
		line = "";
	    res.add(line);
	}
	line = lines.getLine(toY);
	if (line == null)
	    line = "";
	res.add(line.substring(0, toX <line.length()?toX:line.length()));
	return new HeldData(res.toArray(new String[res.size()]));
    }

    @Override public boolean deleteWholeRegion()
    {
	return false;
    }

    @Override public boolean deleteRegion(int fromX, int fromY,
					  int toX, int toY)
    {
	return false;
    }




    @Override public boolean insertRegion(int x, int y,
					  HeldData heldData)
    {
	return false;
    }
}
