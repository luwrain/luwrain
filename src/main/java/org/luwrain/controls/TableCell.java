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

package org.luwrain.controls;

class TableCell
{
    public int pos = 0;//Not real in the string, real is pos+shift;
    public int shift = 0;
    public int width = 0;//Without trailing position designating end of string
    public String line;

    public TableCell(int pos,
		     int shift,
		     int width,
		     String line)
    {
	this.pos = pos;
	this.shift = shift;
	this.width = width;
	this.line = line;
	if (line == null)
	    throw new NullPointerException("line may not be null");
    }

    public boolean moveNext()
    {
	normalize();
	if (pos + shift >= line.length())
	    return false;
	if (pos + shift == line.length() - 1)
	{
	    ++pos;
	    return true;
	}
	if (pos == width - 1)
	    ++shift; else
	    ++pos;
	return true;
    }

    public boolean movePrev()
    {
	normalize();
	if (pos + shift <= 0)
	    return false;
	if (pos > 0)
	    --pos; else
	    --shift;
	return true;
    }

    private void normalize()
    {
	//If the line is shorter than the cell
	if (line.length() <= width)
	{
	    pos = pos + shift;
	    shift = 0;
	    if (pos < 0)
		pos = 0;
	    if (pos >line.length())
		pos = line.length();
	    return;
	}
	//Checking there is no space between end of line and right side of the cell
	if (line.length() < width + shift)
	{
	    final int absPos = pos + shift;
	    shift = line.length() - width;
	    pos = absPos - shift;
	}
	if (pos + shift > line.length())
	    pos = line.length() - shift;
	if (pos + shift == line.length())
	{
	    pos = width;
	    shift = line.length() - width;
	    return;
	}
	if (pos + shift < 0)
	    pos = 0 - shift;
	//Changing shift to catch pos into cell;
	if (pos < 0)
	{
	    shift = pos;
	    pos = 0;
	}
	if (pos > width)
	{
	    shift = pos - width + 1;
	    pos = width - 1;
	}
    }
}
