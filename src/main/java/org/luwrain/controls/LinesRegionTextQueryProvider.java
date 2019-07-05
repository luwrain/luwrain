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

public class LinesRegionTextQueryProvider implements RegionTextQueryTranslator.Provider
{
    protected final Lines lines;

    public LinesRegionTextQueryProvider(Lines lines)
    {
	NullCheck.notNull(lines, "lines");
	this.lines = lines;
    }

    @Override public String onRegionTextQuery(int fromX, int fromY, int toX, int toY)
    {
	final int count = lines.getLineCount();
	if (count < 1)
	    return null;
	if (fromY >= count || toY > count || fromY > toY)
	    return null;
	if (fromY == toY)
	{
	    final String line = lines.getLine(fromY);
	    if (line == null)
		return null;
	    final int fromPos = Math.min(fromX, line.length());
	    final int toPos = Math.min(toX, line.length());
	    if (fromPos >= toPos)
		return null;
	    return line.substring(fromPos, toPos);
	}
	final StringBuilder b = new StringBuilder();
	final String firstLine = lines.getLine(fromY);
	if (firstLine == null)
	    return null;
	b.append (firstLine.substring(Math.min(fromX, firstLine.length())));
	for(int i = fromY + 1;i < toY;++i)
	{
	    final String line = lines.getLine(i);
	    if (line == null)
		return null;
	    b.append("\n" + line);
	}
	final String lastLine = lines.getLine(toY);
	if (lastLine == null)
	    return null;
	b.append("\n" + lastLine.substring(0, Math.min(toX, lastLine.length())));
	return new String(b);
    }
}
