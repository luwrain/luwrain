/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.core;

public class SimpleArea extends NavigateArea
{
    private String name = "";
    private String[] content = null;

    public SimpleArea()
    {
    }

    public SimpleArea(String name)
    {
	this.name = name;
    }

    public SimpleArea(String name, String[] content)
    {
	this.name = name;
	this.content = content;
    }

    public int getLineCount()
    {
	if (content == null)
	    return 1;
	if (content.length < 1)
	    return 1;
	return content.length;
    }

    public String getLine(int index)
    {
	if (content == null && index == 0)
	    return new String();
	if (content.length < 1 && index == 0)
	    return new String();
	if (content == null)
	    return null;
	if (index >= content.length)
	    return null;
	if (content[index] == null)
	    return new String();
	return content[index];
    }

    public void setContent(String[] content)
    {
	this.content = content;
    }

    public String getName()
    {
	if (name == null)
	    return "";
	return name;
    }

public void setName(String name)
{
    this.name = name;
}
}
