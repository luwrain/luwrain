/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

public class Hints
{
    static public LangStatic hintToStaticStrMap(Hint hint)
    {
	switch (hint)
	{
	case SPACE:
	    return LangStatic.SPACE;
	case TAB:
	    return LangStatic.TAB;
	case EMPTY_LINE:
	    return LangStatic.EMPTY_LINE;
	case NO_CONTENT:
	    return LangStatic.NO_CONTENT;
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
	    return null;
	}
    }

    static public Sounds hintToSoundMap(Hint hint)
    {
	switch (hint)
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
	case BEGIN_OF_LINE:
	case BEGIN_OF_TEXT:
	case END_OF_TEXT:
	case END_OF_LINE:
	    return Sounds.END_OF_LINE;
	case NO_LINES_BELOW:
	    return Sounds.NO_LINES_BELOW;
	case NO_CONTENT:
	    return Sounds.NO_CONTENT;
	case EMPTY_LINE:
	    return Sounds.EMPTY_LINE;
	default:
	    return null;
	}
    }
} 
