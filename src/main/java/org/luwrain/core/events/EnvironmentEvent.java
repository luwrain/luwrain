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

package org.luwrain.core.events;

import org.luwrain.core.*;

public class EnvironmentEvent extends Event
{
    public enum Code {OK,
		      ANNOUNCE_LINE,
		      CANCEL,
		      CLOSE,
		      SAVE,
		      REFRESH,
		      INTRODUCE,
		      HELP,
		      THREAD_SYNC,
		      MESSAGE,
		      ACTION,
		      OPEN,
		      MOVE_HOT_POINT,
		      READING_POINT,
		      REGION_POINT,
		      INSERT,
		      DELETE,
		      PROPERTIES,
    };

    private Code code;

    public EnvironmentEvent(Code code)
    {
	//	super(ENVIRONMENT_EVENT);
	this.code = code;
	NullCheck.notNull(code, "code");
    }

    public Code getCode()
    {
	return code;
    }

    static public boolean resetRegionPoint(Area area)
    {
	NullCheck.notNull(area, "area");
	return false;
    }
}
