/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

public class UniRefInfo implements Comparable
{
    private boolean available = false;
    private String prefix;
    private String title;
    private String value;

    public UniRefInfo(String value)
    {
	available = false;
	this.value = value;
	NullCheck.notNull(value, "value");
	prefix = "";
	title = "";
    }

    public UniRefInfo(String value,
		      String prefix, String title)
    {
	available = true;
	this.value = value;
	this.prefix = prefix;
	this.title = title;
	NullCheck.notNull(value, "value");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(title, "title");
    }

    public boolean available()
    {
	return available;
    }

    public String value()
    {
	return value;
    }

    public String prefix()
    {
	return prefix;
    }

    public String title()
    {
	return title;
    }

    @Override public String toString()
    {
	if (!available)
	    return value;
	if (prefix == null || prefix.trim().isEmpty())
	    return title;
	return prefix + " " + title;
    }

    @Override public boolean equals(Object o)
    {
	if (o == null || !(o instanceof UniRefInfo))
	    return false;
	final UniRefInfo uniRef = (UniRefInfo)o;
	return value.equals(uniRef.value());
    }

    @Override public int compareTo(Object o)
    {
	if (o == null || !(o instanceof UniRefInfo))
	    return 0;
	final UniRefInfo uniRef = (UniRefInfo)o;
	return value.compareTo(uniRef.value());
    }
}
