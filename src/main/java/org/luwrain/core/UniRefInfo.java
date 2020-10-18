/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.core;

public final class UniRefInfo implements Comparable
{
    private final boolean available;
    private final String value;
    private final String type;
    private final String addr;
    private final String title;

    public UniRefInfo(String value)
    {
	NullCheck.notNull(value, "value");
	this.available = false;
	this.value = value;
	this.type = "";
	this.addr = "";
	this.title = "";
    }

    public UniRefInfo(String value, String type, String addr, String title)
    {
	NullCheck.notNull(value, "value");
	NullCheck.notNull(type, "type");
	NullCheck.notNull(addr, "addr");
	NullCheck.notNull(title, "title");
	this.available = true;
	this.value = value;
	this.type = type;
	this.addr = addr;
	this.title = title;
    }

    //Will be removed
    public UniRefInfo(String value, String prefix, String title)
    {
	this(value, "", "", title);
    }

    public boolean isAvailable()
    {
	return available;
    }

    public String getValue()
    {
	return value;
    }

    public String getType()
    {
	return type;
    }

    public String getAddr()
    {
	return addr;
    }

    public String getTitle()
    {
	return title;
    }

    @Override public String toString()
    {
	if (!available)
	    return value;
	return title;
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof UniRefInfo))
	    return false;
	final UniRefInfo uniRef = (UniRefInfo)o;
	return value.equals(uniRef.getValue());
    }

    @Override public int compareTo(Object o)
    {
	if (o == null || !(o instanceof UniRefInfo))
	    return 0;
	final UniRefInfo uniRef = (UniRefInfo)o;
	return value.compareTo(uniRef.getValue());
    }

    static public String makeValue(String type, String addr)
    {
	NullCheck.notEmpty(type, "type");
	NullCheck.notNull(addr, "addr");
	if (type.indexOf(":") >= 0)
	    throw new IllegalArgumentException("type (" + type + ") can't contain the ':' character");
	final StringBuilder b = new StringBuilder();
	b.append(type).append(":").append(addr);
	return new String(b);
    }
}
