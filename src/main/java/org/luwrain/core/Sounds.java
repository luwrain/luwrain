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

public interface Sounds
{
    static public final int EVENT_NOT_PROCESSED = 0;
    static public final int NO_APPLICATIONS = 1;
    static public final int STARTUP = 2;
    static public final int SHUTDOWN = 3;
    static public final int MAIN_MENU = 4;
    static public final int MAIN_MENU_EMPTY_LINE = 5;

    /** Confirms finishing of the operation not continuous in time*/
    static public final int ERROR = 6;

    /** Confirms finishing of the operation continuous in time*/
    static public final int DONE = 7;

    static public final int OK = 8;
    static public final int BLOCKED = 9;
    static public final int INTRO_REGULAR = 10;
    static public final int INTRO_POPUP = 11;
    static public final int INTRO_APP = 12;
    static public final int NO_ITEMS_BELOW = 13;
    static public final int NO_ITEMS_ABOVE = 14;
    static public final int NO_LINES_BELOW = 15;
    static public final int NO_LINES_ABOVE = 16;
    static public final int COMMANDER_LOCATION = 17;
    static public final int LIST_ITEM = 18;
    static public final int GENERAL_TIME = 19;
    static public final int TERM_BELL = 20;
    static public final int DOC_SECTION = 21;
    static public final int NO_CONTENT = 22;
    static public final int SEARCH = 23;
    static public final int DELETED = 24;
    static public final int FATAL = 25;
    static public final int CANCEL = 26;
    static public final int REGION_POINT = 27;
    static public final int PASTE = 28;
}
