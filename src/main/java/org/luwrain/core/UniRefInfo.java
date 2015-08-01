		/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

public class UniRefInfo
{
    private String prefix;
    private String title;

    public UniRefInfo(String prefix, String title)
    {
	this.prefix = prefix;
	this.title = title;
	if (prefix == null)
	    throw new NullPointerException("prefix may not be null");
	if (title == null)
	    throw new NullPointerException("title may not be null");
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
	return prefix + " " + title;
    }
}
