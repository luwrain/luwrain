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

package org.luwrain.core.registry;

//Root directory may not contain values;
public class Path
{
    private boolean absolute = true;
    private String[] dirItems = new String[0];
    private String valueName = "";

    public Path(boolean absolute,
		String[] dirItems,
		String valueName)
    {
	this.absolute = absolute;
	this.dirItems = dirItems;
	this.valueName = valueName;
    }

    public Path(boolean absolute, String[] dirItems)
    {
	this.absolute = absolute;
	this.dirItems = dirItems;
    }

    public boolean isAbsolute()
    {
	return absolute;
    }

    public String[] getDirItems()
    {
	if (dirItems == null)
	    return new String[0];
	int i;
	for(i = 0;i < dirItems.length;++i)
	    if (dirItems[i] == null)
		break;
	if (i >= dirItems.length)
	    return dirItems;
	String[] newItems = new String[dirItems.length];
	for(i = 0;i < dirItems.length;++i)
	    newItems [i] = dirItems[i] != null?dirItems[i]:"";
	return newItems;
    }

    public String getValueName()
    {
	return valueName != null?valueName:"";
    }

    public Path getDirPart()
    {
	return new Path(absolute, dirItems);
    }

    public int getDirCount()
    {
	return dirItems != null?dirItems.length:0;
    }

    public Path getParentOfDir()
    {
	if (getDirCount() == 0)
	    return null;
	String[] newItems = new String[dirItems.length - 1];
	for(int i = 0;i < dirItems.length - 1;++i)
	    newItems[i] = dirItems[i];
	return new Path(absolute, newItems);
    }

    public String getLastDirItem()
    {
	if (dirItems == null || dirItems.length == 0)
	    return "";
	return dirItems[dirItems.length - 1] != null?dirItems[dirItems.length - 1].trim():"";
    }

    public boolean isDir()
    {
	return valueName == null || valueName.trim().isEmpty();
    }

    public boolean isRootDir()
    {
	return absolute && isDir() && (dirItems == null || dirItems.length <= 0);
    }

    public boolean isValidDir()
    {
	if (!absolute && (dirItems == null || dirItems.length <= 0))
	    return false;
	if (dirItems != null)
	for(int i = 0;i < dirItems.length;++i)
	    if (dirItems[i] == null || dirItems[i].trim().isEmpty())
		return false;
	return valueName == null || valueName.trim().isEmpty();
    }

    public boolean isValidAbsoluteDir()
    {
	return absolute && isValidDir();
    }

    public boolean isValidValue()
    {
	if (absolute && (dirItems == null || dirItems.length <= 0))
	    return false;
	if (dirItems != null)
	for(int i = 0;i < dirItems.length;++i)
	    if (dirItems[i] == null || dirItems[i].trim().isEmpty())
		return false;
	return valueName != null && !valueName.trim().isEmpty();
    }

    public boolean isValidAbsoluteValue()
    {
	return absolute && isValidValue();
    }

    public boolean isValid()
    {
	return isDir()?isValidDir():isValidValue();
    }

    public boolean isValidAbsolute()
    {
	if (!absolute)
	    return false;
	return isDir()?isValidAbsoluteDir():isValidAbsoluteValue();
    }
}
