/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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
    private final String prefix;
    private final String title;
    private final String value;

    public UniRefInfo(String value)
    {
	NullCheck.notNull(value, "value");
	this.available = false;
	this.value = value;
	this.prefix = "";
	this.title = "";
    }

    public UniRefInfo(String value, String prefix, String title)
    {
	NullCheck.notNull(value, "value");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(title, "title");
	this.available = true;
	this.value = value;
	this.prefix = prefix;
	this.title = title;
    }

    public boolean isAvailable()
    {
	return available;
    }

    public String getValue()
    {
	return value;
    }

    public String getPrefix()
    {
	return prefix;
    }

    public String getTitle()
    {
	return title;
    }

    @Override public String toString()
    {
	if (!available)
	    return value;
	if (prefix.isEmpty())
	    return title;
	return prefix + " " + title;
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
}
