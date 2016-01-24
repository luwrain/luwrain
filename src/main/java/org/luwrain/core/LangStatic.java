/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

public interface LangStatic
{
    static public final int SPACE = 1;
    static public final int TAB = 2;
    static public final int YES = 3;
    static public final int NO = 4;
    static public final int BEGIN_OF_LINE = 5;
    static public final int END_OF_LINE = 6;
    static public final int EMPTY_LINE = 7;
    static public final int NO_LINES_ABOVE = 8;
    static public final int NO_LINES_BELOW = 10;
    static public final int BEGIN_OF_TEXT = 11;
    static public final int END_OF_TEXT = 12;
    static public final int BEGIN_OF_TREE = 13;
    static public final int END_OF_TREE = 14;
    static public final int NO_ITEMS_ABOVE = 15;
    static public final int NO_ITEMS_BELOW = 16;
    static public final int FONT_SIZE = 17;

    static public final int NO_CONTENT = 50;
    static public final int COMMANDER_NO_CONTENT = 51;
    static public final int LIST_NO_CONTENT = 52;
    static public final int TABLE_NO_CONTENT = 53;
    static public final int TREE_NO_CONTENT = 54;

    static public final int COMMANDER_SELECTED = 100;
    static public final int COMMANDER_DIRECTORY = 101;
    static public final int COMMANDER_SYMLINK = 102;
    static public final int COMMANDER_PIPE = 103;
    static public final int COMMANDER_SOCKET = 104;
    static public final int COMMANDER_BLOCK_DEVICE = 105;
    static public final int COMMANDER_CHAR_DEVICE = 106;
    static public final int COMMANDER_SPECIAL = 107;
    static public final int COMMANDER_UNKNOWN = 108;
    static public final int COMMANDER_PARENT_DIRECTORY = 109;
    static public final int COMMANDER_USER_HOME = 110;

    static public final int TREE_EMPTY_ITEM = 150;
    static public final int TREE_EXPANDED = 151;
    static public final int TREE_COLLAPSED = 152;
    static public final int TREE_LEVEL = 153;

    static public final int TABLE_NO_ROWS_ABOVE = 200;
    static public final int TABLE_NO_ROWS_BELOW = 201;
    static public final int TABLE_END_OF_COL = 202;
    static public final int TABLE_BEGIN_OF_ROW = 203;
    static public final int TABLE_END_OF_ROW = 204;

    static public final int POPUP_IMPORTANT_LOCATIONS_NAME = 250;
    static public final int DOCUMENT_NO_CONTENT = 300;
}
