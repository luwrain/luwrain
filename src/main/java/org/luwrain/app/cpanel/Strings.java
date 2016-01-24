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

package org.luwrain.app.cpanel;

public interface Strings
{
    static public final int APPS = 1;
    static public final int HARDWARE = 2;
    static public final int SYS_DEVICES = 3;
    static public final int STORAGE_DEVICES = 4;
    static public final int SPEECH = 5;
    static public final int SOUNDS = 6;
    static public final int KEYBOARD = 7;
    static public final int UI = 8;
    static public final int EXTENSIONS = 9;
    static public final int NETWORK = 10;
    static public final int WORKERS = 11;

    String appName();
    String sectionsAreaName();
    String sectName(int id);
}
