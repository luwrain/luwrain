/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

public class TextUtils
{
    public static String getLastWord(String text, int upToPos)
    {
	String word = new String();
	boolean broken = false;
	for(int i = 0;i < text.length() && i < upToPos;i++)
	{
	    char c = text.charAt(i);
	if (Character.getType(c) == Character.UPPERCASE_LETTER ||
	    Character.getType(c) == Character.LOWERCASE_LETTER ||
	    Character.getType(c) == Character.DECIMAL_DIGIT_NUMBER)
	{
	    if (broken)
		word = new String();
	    broken = false;
	    word += c;
	    continue;
	}
	broken = true;
	}
	return word;
    }
}

