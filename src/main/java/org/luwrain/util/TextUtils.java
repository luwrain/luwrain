/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

public final class TextUtils
{
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
}
