/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core.events;

import org.luwrain.core.*;

public class EnvironmentEvent extends Event
{
    static public final int OK = 0;
    static public final int CANCEL = 1;
    static public final int CLOSE = 2;
    static public final int SAVE = 3;
    static public final int REFRESH = 4;
    static public final int INTRODUCE = 5;
    static public final int HELP = 6;
    static public final int THREAD_SYNC = 7;
    static public final int OPEN = 8;

    public static final int REGION_POINT = 10;
    public static final int COPY = 11;
    public static final int CUT = 12;
    public static final int INSERT = 14;
    public static final int DELETE = 15;

    private int code;

    public EnvironmentEvent(int code)
    {
	super(ENVIRONMENT_EVENT);
	this.code = code;
    }

    public int getCode()
    {
	return code;
    }
}
