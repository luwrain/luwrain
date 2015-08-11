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

package org.luwrain.app.registry;

class Directory
{
    public String name = "";
    public String title = "";
    public Directory parent;
    //    public boolean deleted = false;

    //Used only for root directory;
    public Directory(String title)
    {
	this.name = "";
	this.title = title;
	this.parent = null;
    }

    public Directory(Directory parent, String name)
    {
	this.name = name;
	this.parent = parent;
	this.title = name;
    }

    public String getPath()
    {
	String parentPath = parent != null?parent.getPath():"";
	return parentPath.equals("/")?"/" + name:parentPath + "/" + name;
    }

    /*
    public boolean isValid()
    {
	return !deleted && (parent != null?parent.isValid():true);
    }
    */

    public String toString()
    {
	return title != null?title:"";
    }

    public boolean equals(Object o)
    {
	if (o == null)
	    return false;
	try {
	    Directory dir = (Directory)o;
	    return getPath().equals(dir.getPath());
	}
	catch(ClassCastException e)
	{
	    return false;
	}
    }
}
