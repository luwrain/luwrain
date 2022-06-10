/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.util;

import org.luwrain.core.*;

public class WordIterator
{
protected final String line;
protected int pos = 0;
    protected String announce = "";

    public WordIterator(String line, int pos)
    {
	NullCheck.notNull(line, "line");
	if (pos < 0)
	    throw new IllegalArgumentException("pos may not be negative (" + pos + ")");
		if (this.pos > line.length())
		    throw new IllegalArgumentException("pos may not be greater than the length of provided line (" + pos + ")");
	this.line = line;
	this.pos = pos;
	this.announce = "";
    }

    public boolean stepForward()
    {
	if (pos >= line.length())
	    return false;
	pos = getNextBorder(pos);
	if (pos >= line.length())
	{
	    announce = "";
	    return true;
	}
	final int nextPos = getNextBorder(pos);
	announce = line.substring(pos, nextPos);
	return true;
    }

    public boolean stepBackward()
    {
	if (pos <= 0)
	    return false;
	pos = getPrevBorder(pos);
	final int nextPos = getNextBorder(pos);
	announce = line.substring(pos, nextPos);
	return true;
    }

    public int pos()
    {
	return pos;
    }

    public String announce()
    {
	return announce;
    }

    private int getNextBorder(int current)
    {
	if (current < 0 || current > line.length())
	    throw new IllegalArgumentException("current must be inside of the line");
	if (current + 1 >= line.length())
	    return line.length();
	int i = current + 1;
	while (i < line.length() && (!isLetterDigit(line.charAt(i)) || isLetterDigit(line.charAt(i - 1))))
	    ++i;
	return i;
    }

    private int getPrevBorder(int current)
    {
	if (current < 0 || current > line.length())
	    throw new IllegalArgumentException("current must be inside of the line");
	if (current <= 1)
	    return 0;
	int i = current - 1;
	while (i > 1 && (!isLetterDigit(line.charAt(i)) || isLetterDigit(line.charAt(i - 1))))
	    --i;
	if (!isLetterDigit(line.charAt(i - 1)) && isLetterDigit(line.charAt(i)))
	    return i;
	return 0;
    }

    private boolean isLetterDigit(char ch)
    {
	return Character.isDigit(ch) || Character.isLetter(ch);
    }
}
