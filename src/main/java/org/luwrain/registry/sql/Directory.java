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

package org.luwrain.core.registry;

public class Directory
{
    public String name = "";
    public Directory[] subdirs = new Directory[0];
    public Value[] values = new Value[0];

    public Directory(String name)
    {
	this.name = name;
    }

    public Directory getSubdir(String subdirName)
    {
	if (subdirs == null)
	    return null;
	for(int i = 0;i < subdirs.length;++i)
	    if (subdirs[i] != null && subdirs[i].name.equals(subdirName))
		return subdirs[i];
	return null;
    }

    //Returns false is directory with this name already exists;
    public boolean addSubdir(Directory subdir)
    {
	if (subdir == null || subdir.name == null || subdir.name.trim().isEmpty())
	    return false;
	if (subdirs == null)
	    subdirs = new Directory[0];
	Directory[] newSubdirs = new Directory[subdirs.length + 1];
	for(int i = 0;i < subdirs.length;++i)
	{
	    if (subdirs[i].name.equals(subdir.name))
		return false;
	    newSubdirs[i] = subdirs[i];
	}
	newSubdirs[newSubdirs.length - 1] = subdir;
	subdirs = newSubdirs;
	return true;
    }

    public Value getValue(String valueName)
    {
	if (values == null)
	    return null;
	for(int i = 0;i < values.length;++i)
	    if (values[i] != null && values[i].name.equals(valueName))
		return values[i];
	return null;
    }

    public void setValue(Value value)
    {
	if (value == null || value.name == null || value.name.trim().isEmpty())
	    return;
	for(int i = 0;i < values.length;++i)
	    if (values[i].name.equals(value.name))
	    {
		values[i] = value;
		return;
	    }
	Value[] newValues = new Value[values.length + 1];
	for(int i = 0;i < values.length;++i)
	    newValues[i] = values[i];
	newValues[newValues.length - 1] = value;
	values = newValues;
    }
}
