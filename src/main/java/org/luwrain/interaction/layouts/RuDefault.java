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

package org.luwrain.interaction.layouts;

import org.luwrain.interaction.*;

public final class RuDefault implements KeyboardLayout
{
    final String[] clusters = {
	"`ё",
	"qй",
	"wц",
	"eу",
	"rк",
	"tе",
	"yн",
	"uг",
	"iш",
	"oщ",
	"pз",
	"[х",
	"]ъ",
	"aф",
	"sы",
	"dв",
	"fа",
	"gп",
	"hр",
	"jо",
	"kл",
	"lд",
	";ж",
	"'э",
	"zя",
	"xч",
	"cс",
	"vм",
	"bи",
	"nт",
	"mь",
	",б",
	".ю"
    };

    @Override public char getAsciiOfButton(char ch)
    {
	for(String s: clusters)
	{
	    for(int i = 0;i < s.length();i++)
		if (s.charAt(i) == ch)
		    return s.charAt(0);
	}
	return '\0';
    }

    @Override public boolean onSameButton(char c1, char c2)
    {
	final char lc1 = Character.toLowerCase(c1);
	final char lc2 = Character.toLowerCase(c2);
	for(String s: clusters)
	{
	    int i;
	    for(i = 0;i < s.length();++i)
		if (s.charAt(i) == lc1)
		    break;
	    if (i >= s.length())
		continue;
	    for(i = 0;i < s.length();++i)
		if (s.charAt(i) == lc2)
		    return true;
	}
	return lc1 == lc2;
    }
}
