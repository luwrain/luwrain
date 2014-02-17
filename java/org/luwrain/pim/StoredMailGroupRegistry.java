/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.pim;

import org.luwrain.core.registry.Registry;

class StoredMailGroupRegistry implements StoredMailGroup
{
    private Registry registry;

    public long id;
    public long parentId = 0;
    public String name = "";
    public int orderIndex = 0;
    public int expireAfterDays = 0;

    public     StoredMailGroupRegistry(Registry registry)
    {
	this.registry = registry;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name) throws Exception
    {
	//FIXME:
    }

    public int getOrderIndex()
    {
	return orderIndex;
    }

    public void setOrderIndex(int index) throws Exception
    {
	//FIXME:
    }

    public int getExpireAfterDays()
    {
	return expireAfterDays;
    }

    public void setExpireAfterDays(int count) throws Exception
    {
	//FIXME:
    }

    public String toString()
    {
	return name != null?name:"";
    }

    public boolean equals(Object o)
    {
	StoredMailGroupRegistry g;
	try {
	    g = (StoredMailGroupRegistry)o;
	}
	catch(ClassCastException e)
	{
	    return false;
	}
	return id == g.id;
    }
}
