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

package org.luwrain.core;

public class Hints
{
    public static final int SPACE = 1;
    public static final int TAB = 2;
    public static final int EMPTY_LINE = 3;
    public static final int BEGIN_OF_LINE = 4;
    public static final int END_OF_LINE = 5;
    public static final int BEGIN_OF_TEXT = 6;
    public static final int END_OF_TEXT = 7;
    public static final int NO_LINES_ABOVE = 8;
    public static final int NO_LINES_BELOW = 9;
    public static final int NO_ITEMS_ABOVE = 10;
    public static final int NO_ITEMS_BELOW = 11;
    public static final int NO_CONTENT = 12;

    public static final int TREE_BEGIN = 13;
    public static final int TREE_END = 14;
    public static final int TREE_BRANCH_COLLAPSED = 15;
    public static final int TREE_BRANCH_EXPANDED = 16;

    public static final int TABLE_NO_ROWS = 17;
    public static final int TABLE_NO_ROWS_ABOVE = 18;
    public static final int TABLE_NO_ROWS_BELOW = 19;
    public static final int TABLE_END_OF_COL = 20;
    public static final int TABLE_BEGIN_OF_ROW = 21;
    public static final int TABLE_END_OF_ROW = 22;

    static public int hintToStaticStrMap(int hintCode)
    {
	switch (hintCode)
	{
	case SPACE:
	    return LangStatic.SPACE;
	case TAB:
	    return LangStatic.TAB;
	case EMPTY_LINE:
	    return LangStatic.EMPTY_LINE;
	case BEGIN_OF_LINE:
	    return LangStatic.BEGIN_OF_LINE;
	case END_OF_LINE:
	    return LangStatic.END_OF_LINE;
	case BEGIN_OF_TEXT:
	    return LangStatic.BEGIN_OF_TEXT;
	case END_OF_TEXT:
	    return LangStatic.END_OF_TEXT;
	case NO_LINES_ABOVE:
	    return LangStatic.NO_LINES_ABOVE;
	case NO_LINES_BELOW:
	    return LangStatic.NO_LINES_BELOW;
	case NO_ITEMS_ABOVE:
	    return LangStatic.NO_ITEMS_ABOVE;
	case NO_ITEMS_BELOW:
	    return LangStatic.NO_ITEMS_BELOW;
	case TREE_BEGIN:
	    return LangStatic.BEGIN_OF_TREE;
	case TREE_END:
	    return LangStatic.END_OF_TREE;
	case TABLE_NO_ROWS:
	    return LangStatic.TABLE_NO_ROWS;
	case TABLE_NO_ROWS_ABOVE:
	    return LangStatic.TABLE_NO_ROWS_ABOVE;
	case TABLE_NO_ROWS_BELOW:
	    return LangStatic.TABLE_NO_ROWS_BELOW;
	case TABLE_END_OF_COL:
	    return LangStatic.TABLE_END_OF_COL;
	case TABLE_BEGIN_OF_ROW:
	    return LangStatic.TABLE_BEGIN_OF_ROW;
	case TABLE_END_OF_ROW:
	    return LangStatic.TABLE_END_OF_ROW;
	default:
	    return -1;
	}
    }

    static public int hintToSoundMap(int hintCode)
    {
	switch (hintCode)
	{
	case NO_ITEMS_ABOVE:
	case TREE_BEGIN:
	case TABLE_NO_ROWS_ABOVE:
	    return Sounds.NO_ITEMS_ABOVE;
	case NO_ITEMS_BELOW:
	case TREE_END:
	case TABLE_NO_ROWS_BELOW:
	    return Sounds.NO_ITEMS_BELOW;
	case NO_LINES_ABOVE:
	    return Sounds.NO_LINES_ABOVE;
	case NO_LINES_BELOW:
	    return Sounds.NO_LINES_BELOW;
	default:
	    return -1;
	}
    }
} 
