/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;


class AreaText
{
    private Area area;

    AreaText(Area area)
    {
	NullCheck.notNull(area, "area");
	this.area = area;
    }

    String currentWord()
    {
	final int x = area.getHotPointX();
	final int y = area.getHotPointY();
	if (y >= area.getLineCount())
	    return "";
	final String line = area.getLine(y);
	if (line == null || x >= line.length())
	    return "";
	if (!wordChar(line.charAt(x)))
	    return "";
	int i = x;
	String res = "";
	while (i >= 0 && wordChar(line.charAt(i)))
	{
	    res = line.charAt(i) + res;
	    --i;
	}
	i = x + 1;
	while (i < line.length() && wordChar(line.charAt(i)))
	{
	    res += line.charAt(i);
	    ++i;
	}
	return res;
    }

    private boolean wordChar(char c)
    {
	return Character.isLetter(c) || Character.isDigit(c) || c == '_' || c == '-';
    }
}
