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

package org.luwrain.util;

import java.util.*;

import org.luwrain.core.*;

/**
 * Splits a text preserving lenth of lines not exceeding some given
 * number. This class takes a set of lines and makes new dividing trying
 * to make the length of each line close to some value as much as it is
 * possible. The dividing is performed only on space characters (
 * {@code Character.isSpaceChar()}), so in common sense there is no guarantee
 * that all lines will have the length not greater of the given value,
 * because very long words go without splitting. If the position of the
 * hot point is provided, it is moved to the corresponding place in the
 * new lines.
 */
public class TextAligning
{
    final int maxLineLen;

    /** The original X position of the hot point; must be -1, if there is no hot point*/
    public int origHotPointX = -1;

    /** The original Y position of the hot point; must be -1, if there is no hot point*/
    public int origHotPointY = -1;

    /** The text to split*/
    public String[] origLines = new String[0];

    /** New X position of the hot point*/
    public int hotPointX = -1;

        /** New Y position of the hot point*/
    public int hotPointY = -1;

    /** The result with new text dividing*/
    public final LinkedList<String> res = new LinkedList();

    public TextAligning(int maxLineLen)
    {
	if (maxLineLen < 0)
	    throw new IllegalArgumentException("maxLineLen (" + maxLineLen + ") may not be negative");
	this.maxLineLen = maxLineLen;
    }

    public void align()
    {
	res.clear();
	hotPointX = -1;
	hotPointY = -1;
	boolean wereSpaces = false;
	boolean wereSpacesWithHotPoint = false;
	for(int lineIndex = 0;lineIndex < origLines.length;++lineIndex)
	{
	    final String line = origLines[lineIndex];
	    if (line.isEmpty())
		continue;
	    int pos = 0;
	    while (pos < line.length())
	    {
		final int wordBeginPos = pos;
		while (pos < line.length() && !Character.isSpaceChar(line.charAt(pos)))
		    ++pos;
		if (pos > wordBeginPos)
		{
		    //Handling the word
		    final int hotPointPos;
		    if (origHotPointY == lineIndex && origHotPointX >= wordBeginPos && origHotPointX < pos)
			hotPointPos = origHotPointX - wordBeginPos; else
			hotPointPos = -1;
		    onWord(line.substring(wordBeginPos, pos), hotPointPos, wereSpaces, wereSpacesWithHotPoint);
		    wereSpaces = false;
		    wereSpacesWithHotPoint = false;
		}
		final int spaceBeginPos = pos;
		while(pos < line.length() && Character.isSpaceChar(line.charAt(pos)))
		    ++pos;
		if (pos > spaceBeginPos)
		{
		    //Handling the space
		    wereSpaces = true;
		    if (origHotPointY == lineIndex && origHotPointX >= spaceBeginPos && origHotPointX < pos)
			wereSpacesWithHotPoint = true;
		}
	    }
	    wereSpaces = true;
	} //for(lines)
	if (wereSpaces && wereSpacesWithHotPoint)
	{
	    if (res.isEmpty() || getLastLineSpaceLeft() == 0)
	    {
		res.add(" ");
		hotPointX = 0;
		hotPointY = res.size() - 1;
	    } else
	    {
		addLastLine(" ");
		hotPointX = getLastLineLen() - 1;
		hotPointY = res.size() - 1;
	    }
	}
    }

    protected void onWord(String word, int hotPointPos, boolean wereSpaces, boolean wereSpacesWithHotPoint)
    {
	NullCheck.notEmpty(word, "word");
	if (hotPointPos >= word.length())
	    throw new IllegalArgumentException("hotPointPos (" + hotPointPos + ") may not be greater than " + word.length());
	if (wereSpacesWithHotPoint && !wereSpaces)
	    throw new IllegalArgumentException("wereSpacesWithHotPoint can be set only with wereSpaces");
	if (res.isEmpty())
	{
	    if (wereSpacesWithHotPoint)
	    {
		res.add(" " + word);
		hotPointX = 0;
		hotPointY = 0;
	    } else
	    {
		res.add(word);
		if (hotPointPos >= 0)
		{
		    hotPointX = hotPointPos;
		    hotPointY = 0;
		}
	    }
	    return;
	}
	//res array is not empty
	if (wereSpacesWithHotPoint)
	{
	    if (getLastLineSpaceLeft() > 0)
	    {
		addLastLine(" ");
		hotPointX = getLastLineLen() - 1;
		hotPointY = res.size() - 1;
	    } else
	    {
		res.add(" ");
		hotPointX = 0;
		hotPointY = res.size() - 1;
	    }
	    onWord(word, -1, false, false);
	    return;
	}
	//res not empty and wereSpacesWithHotPoint guarantly false
	if (getLastLineSpaceLeft() >= word.length() + (wereSpaces?1:0))
	{
	    //It looks like wereSpaces is never false at this point, but handling it anyway
	    if (wereSpaces)
		addLastLine(" ");
	    final int previousLen = getLastLineLen();
	    addLastLine(word);
	    if (hotPointPos >= 0)
	    {
		hotPointX = previousLen + hotPointPos;
		hotPointY = res.size() - 1;
	    }
	    return;
	}
	//The last case with adding new line, wereSpacesWithHotPoint guarantly false
	res.add(word);
	if (hotPointPos >= 0)
	{
	    hotPointX = hotPointPos;
	    hotPointY = res.size() - 1;
	}
    }

    protected int getLastLineLen()
    {
	if (res.isEmpty())
	    throw new RuntimeException("res may not be empty");
	return res.getLast().length();
    }

    protected int getLastLineSpaceLeft()
    {
	if (res.isEmpty())
	    throw new RuntimeException("res may not be empty");
	return maxLineLen - res.getLast().length();
    }

    protected void addLastLine(String text)
    {
	if (res.isEmpty())
	    throw new RuntimeException("res may not be empty");
	res.set(res.size() - 1, res.getLast() + text);
    }
}
