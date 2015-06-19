/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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


package org.luwrain.core.events;

public class EqualKeys
{
    final static String[] clusters = {
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

    static public boolean equalKeys(char c1, char c2)
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
