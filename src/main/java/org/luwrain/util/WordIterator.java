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

package org.luwrain.util;

public class WordIterator
{
    private String line = "";
    private int pos = 0;
    private String announce = "";

    public WordIterator(String line, int pos)
    {
	if (line == null)
	    throw new NullPointerException("line may not be null");
	this.line = line;
	this.pos = pos;
	if (this.pos < 0)
	    this.pos = 0;
	if (this.pos > line.length())
	    this.pos = line.length();
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
