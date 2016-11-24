/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

final public class RegionContent
{
private Object[] rawObjects = new Object[0];
    private String[] strings = new String[0];
    private String comment = "";

    public RegionContent()
    {
    }

    public RegionContent(String[] strings)
    {
	NullCheck.notNullItems(strings, "strings");
	this.strings = strings;
    }

    public RegionContent(String[] strings, String comment)
    {
	NullCheck.notNullItems(strings, "strings");
	NullCheck.notNull(comment, "comment");
	this.strings = strings;
	this.comment = comment;
    }

    public RegionContent(String[] strings, Object[] rawObjects)
    {
	NullCheck.notNullItems(strings, "strings");
	NullCheck.notNullItems(rawObjects, "rawObjects");
	if (strings.length != rawObjects.length)
	    throw new IllegalArgumentException("strings and rawObjects must have equal number of items");
	this.strings = strings;
	this.rawObjects = rawObjects;
    }

    public RegionContent(String[] strings, Object[] rawObjects,
		    String comment)
    {
	NullCheck.notNullItems(strings, "strings");
	NullCheck.notNullItems(rawObjects, "rawObjects");
	NullCheck.notNull(comment, "comment");
	if (strings.length != rawObjects.length)
	    throw new IllegalArgumentException("strings and rawObjects must have equal number of items");
	this.strings = strings;
	this.rawObjects = rawObjects;
	this.comment = comment;
    }

    public boolean isEmpty()
    {
	return strings == null || strings.length < 1;
    }

    public int getLineCount()
    {
	NullCheck.notNullItems(strings, "strings");
	NullCheck.notNullItems(rawObjects, "rawObjects");
	if (rawObjects.length > 0)
	    return rawObjects.length;
	int firstLine = -1;
	int lastLine = -1;
	for(int i = 0;i < strings.length;++i)
	    if (!strings[i].isEmpty())
	{
	    if (firstLine < 0)
		firstLine = i;
	    lastLine = i;
	}
	return lastLine - firstLine + 1;
    }

    /**
     * Returns the entire content of the object as a single line. All string
     * items are concatenated with spaces between them.
     *
     * @return String content as a single line
     */
    public String toSingleLine()
    {
	if (strings == null || strings.length < 1)
	    return "";
	final StringBuilder b = new StringBuilder();
	b.append(strings[0]);
	for(int i = 1;i < strings.length;++i)
	{
	    b.append(" ");
	    b.append(strings[i]);
	}
	return new String(b);
    }

    public String[] strings() {return strings;}
    public Object[] rawObjects() {return rawObjects;}
    public String comment() {return comment;}
}
