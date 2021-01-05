/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.linux;

import java.util.*;

import org.luwrain.core.*;

public final class MountParams
{
    public enum Flags {RW, RO};

    private final String devName;
    private final String point;
    private final Set<Flags> flags;

    public MountParams(String devName, String point, Set<Flags> flags)
    {
	NullCheck.notEmpty(devName, "devName");
	NullCheck.notEmpty(point, "point");
	NullCheck.notNull(flags, "flags");
	this.devName = devName;
	this.point = point;
	this.flags = flags;
    }

    public MountParams(String devName, String point)
    {
	this(devName, point, EnumSet.noneOf(Flags.class));
    }

    public String getDevName()
    {
	return devName;
    }

    public String getPoint()
    {
	return point;
    }

    public Set<Flags> getFlags()
    {
	return flags;
    }
};
