/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

import org.luwrain.core.*;

public class EnvironmentEvent extends Event
{
    public static final int OK = 0;
    public static final int CANCEL = 1;
    public static final int CLOSE = 2;
    public static final int SAVE = 3;
    public static final int REFRESH = 4;
    public static final int DESCRIBE = 5;
    public static final int HELP = 6;
    public static final int INTRODUCE = 7;
    public static final int THREAD_SYNC = 8;
    //TODO:DESCRIBE_AT_POINT;


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
