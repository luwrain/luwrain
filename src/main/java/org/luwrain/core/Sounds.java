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

public interface Sounds
{
    public static final int EVENT_NOT_PROCESSED = 0;
    public static final int NO_APPLICATIONS = 1;
    public static final int STARTUP = 2;
    public static final int SHUTDOWN = 3;
    public static final int MAIN_MENU = 4;
    public static final int MAIN_MENU_ITEM = 5;
    public static final int MAIN_MENU_EMPTY_LINE = 6;
    public static final int GENERAL_ERROR = 7;

    public static final int GENERAL_OK = 8;
    public static final int INTRO_REGULAR = 9;
    public static final int INTRO_POPUP = 10;
    public static final int INTRO_APP = 11;
    public static final int NO_ITEMS_BELOW = 12;
    public static final int NO_ITEMS_ABOVE = 13;
    public static final int NO_LINES_BELOW = 14;
    public static final int NO_LINES_ABOVE = 15;
    public static final int COMMANDER_NEW_LOCATION = 16;
    public static final int NEW_LIST_ITEM = 17;
    public static final int GENERAL_TIME = 18;
}
