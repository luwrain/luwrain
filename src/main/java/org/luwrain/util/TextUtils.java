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

package org.luwrain.util;

import java.util.*;

import org.luwrain.core.*;

public final class TextUtils
{
        //On an empty line provided returns one empty line
    static public String[] splitLinesAnySeparator(String text)
    {
	NullCheck.notNull(text, "text");
	boolean wasBN = false;
	boolean wasBR = false;
	final List<String> res = new LinkedList();
	StringBuilder b = new StringBuilder();
	for(int i = 0;i < text.length();++i)
	{
	    final char c = text.charAt(i);
	    switch(c)
	    {
	    case '\n':
		if (wasBR)
		{
		    //Doing nothing
		    wasBN = true;
		    continue;
		}
		if (wasBN)
		{
		    //The second encountering, it means there was an empty line
		    wasBN = false;
		    wasBR = false;
		    //b must be empty
		    res.add("");
		    continue;
		}
		//wasBR and wasBN are false
		res.add(new String(b));
		b = new StringBuilder();
		wasBN = true;
		break;
	    case '\r':
		if (wasBN)
		{
		    //Doing nothing
		    wasBR = true;
		    continue;
		}
		if (wasBR)
		{
		    //The second encountering, it means there was an empty line
		    wasBN = false;
		    wasBR = false;
		    //b must be empty
		    res.add("");
		    continue;
		}
		//wasBR and wasBN are false
		res.add(new String(b));
		b = new StringBuilder();
		wasBR = true;
		break;
	    default:
		wasBR = false;
		wasBN = false;
		b.append("" + c);
	    }
	}
	res.add(new String(b));
	return res.toArray(new String[res.size()]);
    }

    static public String getLastWord(String text, int upToPos)
    {
	NullCheck.notNull(text, "text");
	String word = new String();
	boolean broken = false;
	for(int i = 0;i < text.length() && i < upToPos;++i)
	{
	    final char c = text.charAt(i);
	if (Character.getType(c) == Character.UPPERCASE_LETTER ||
	    Character.getType(c) == Character.LOWERCASE_LETTER ||
	    Character.getType(c) == Character.DECIMAL_DIGIT_NUMBER)
	{
	    if (broken)
		word = "";
	    broken = false;
	    word += c;
	    continue;
	}
	broken = true;
	}
	return word;
    }

    static public String sameCharString(char c, int count)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < count;++i)
	    b.append(c);
	return new String(b);
    }

    static public String removeIsoControlChars(String value)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < value.length();++i)
	    if (!Character.isISOControl(value.charAt(i)))
		b.append(value.charAt(i));
	return b.toString();
    }

    static public String replaceIsoControlChars(String value, char replaceWith)
    {
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < value.length();++i)
	    if (!Character.isISOControl(value.charAt(i)))
		b.append(value.charAt(i)); else
		b.append(replaceWith);
	return b.toString();
    }

    static public String replaceIsoControlChars(String value)
    {
	return replaceIsoControlChars(value, ' ');
    }

    static public String[] wordWrap(String line, int width)
    {
	if (width < 3)
	    throw new IllegalArgumentException("width (" + String.valueOf(width) + ") can't be less than 3");
	final List<String> res = new ArrayList();
	final String[] words = line.split(" ", -1);
	StringBuilder b = new StringBuilder();
	for(String word: words)
	{
	    if (word.trim().isEmpty())
		continue;
	    if (b.length() + word.length() + 1 > width)
	    {
		res.add(new String(b));
		b = new StringBuilder();
	    }
		if (b.length() > 0)
		    b.append(" ");
		b.append(word);
	}
	if (b.length() > 0)
	    res.add(new String(b));
	return res.toArray(new String[res.size()]);
    }
}
