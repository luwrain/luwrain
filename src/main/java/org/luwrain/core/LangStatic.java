 /*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

public interface LangStatic
{
    public static final int SPACE = 1;
    public static final int TAB = 2;
    public static final int BEGIN_OF_LINE = 3;
    public static final int END_OF_LINE = 4;
    public static final int EMPTY_LINE = 5;
    public static final int NO_LINES_ABOVE = 6;
    public static final int NO_LINES_BELOW = 7;
    public static final int BEGIN_OF_TEXT = 8;
    public static final int END_OF_TEXT = 9;
    public static final int TREE_AREA_BEGIN = 10;
    public static final int TREE_AREA_END = 11;
    public static final int NO_ITEMS_ABOVE = 12;
    public static final int NO_ITEMS_BELOW = 13;
    public static final int FONT_SIZE = 14;
    public static final int EMPTY_TREE = 15;
    public static final int EMPTY_TREE_ITEM = 16;
    public static final int TREE_EXPANDED = 17;
    public static final int TREE_COLLAPSED = 18;
    public static final int TREE_LEVEL = 19;
    public static final int NO_TABLE_ROWS = 20;
    public static final int NO_TABLE_ROWS_ABOVE = 21;
    public static final int NO_TABLE_ROWS_BELOW = 22;
    public static final int END_OF_TABLE_COL = 23;

    public static final int MESSAGE = 30;
    public static final int MESSAGE_TO = 32;
    public static final int MESSAGE_CC = 33;
    public static final int MESSAGE_SUBJECT = 34;
    public static final int MESSAGE_TEXT = 35;
    public static final int MESSAGE_ATTACHMENT = 36;
    public static final int MESSAGE_ATTACHMENT_POPUP_TITLE = 37;
    public static final int MESSAGE_ATTACHMENT_POPUP_PREFIX = 38;

    public static final int COPIED_LINES = 40;
    public static final int CUT_LINES = 41;
    public static final int LIST_NO_CONTENT = 108;
    public static final int COMMANDER_INACCESSIBLE_DIRECTORY_CONTENT = 109;
    public static final int COMMANDER_SELECTED_DIRECTORY = 110;
    public static final int COMMANDER_SELECTED = 111;
    public static final int COMMANDER_DIRECTORY = 112;
    public static final int COMMANDER_PARENT_DIRECTORY = 113;
    public static final int COMMANDER_USER_HOME = 114;

    public static final int POPUP_IMPORTANT_LOCATIONS_NAME = 120;
}
