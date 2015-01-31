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

package org.luwrain.registry.fsdir;

import java.io.*;
import java.util.*;

public class Directory
{
    private String name = "";
    private File dir;
    private Vector<Directory> subdirs = new Vector<Directory>();

    public Directory(String name, File dir)
    {
	this.name = name;
	this.dir = dir;
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (name.isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	if (dir == null)
	    throw new NullPointerException("dir may not be null");
    }

    public String name()
    {
	return name;
    }

    public Directory createSubdir(String newName) throws IOException
    {
	if (newName == null)
	    throw new NullPointerException("newName may not be null");
	if (newName.isEmpty())
	    throw new IllegalArgumentException("newName may not be empty");
	Directory d = findSubdir(name);
	if (d != null)
	    return d;
	File f = new File(dir, newName);
	d = new Directory(newName, f);
	subdirs.add(d);
	return d;
    }

    public boolean hasSubdir(String dirName) throws IOException
    {
	return findSubdir(dirName) != null;
    }

    //null means no subdirectory
    public Directory findSubdir(String dirName) throws IOException
    {
	return null;
    }

    public void init() throws IOException
    {
    }

    public void delete() throws IOException
    {
    }

    public boolean deleteValue(String valueName) throws IOException
    {
	return false;
    }

    public boolean getBoolean(String valueName) throws IOException
    {
	return false;
    }

    public int getInteger(String valueName) throws IOException
    {
	return 0;
    }

    public String getString(String valueName) throws IOException
    {
	return "";
    }

    public boolean setBoolean(String valueName, boolean value) throws IOException
    {
	return true;
    }

    public boolean setInteger(String valueName, int value) throws IOException
    {
	return true;
    }

    public boolean setString(String valueName, String value) throws IOException
    {
	return true;
    }

    public String[] subdirs() throws IOException
    {
	return null;
    }

    public String[] values() throws IOException
    {
	return null;
    }

    public boolean hasValue(String valueName) throws IOException
    {
	return false;
    }

    public int getTypeOf(String valueName) throws IOException
    {
	return 0;
    }

    public void refreshDeleted() throws IOException
    {
    }
}
