/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.popups;

public class EditListPopupItem implements Comparable
{
    private String value;
    private String introduction;

    public EditListPopupItem()
    {
	value = "";
	introduction = "";
    }

    public EditListPopupItem(String value, String introduction)
    {
	this.value = value;
	this.introduction = introduction;
	if (value == null)
	    throw new NullPointerException("value may not be null");
	if (introduction == null)
	    throw new NullPointerException("introduction may not be null");
    }

    public EditListPopupItem(String value)
    {
	this.value = value;
	this.introduction = value;
	if (value == null)
	    throw new NullPointerException("value may not be null");
    }

    public String value()
    {
	return value;
    }

    public String introduction()
    {
	return introduction;
    }

    @Override public String toString()
    {
	return value;
    }

    @Override public int compareTo(Object o)
    {
	return value.compareTo(o.toString());
    }
}
