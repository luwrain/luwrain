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

package org.luwrain.app.registry;

import org.luwrain.core.Registry;

class Value implements Comparable
{
    public int type = Registry.INTEGER;
    public String parentDir;
    public String name;
    public String strValue;
    public int intValue;
    public boolean boolValue;

    @Override public int compareTo(Object o)
    {
	if (o == null || !(o instanceof Value))
	    return 0;
	final Value v = (Value)o;
	return name.compareTo(v.name);
    }
}
