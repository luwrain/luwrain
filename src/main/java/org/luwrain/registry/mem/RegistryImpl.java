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

package org.luwrain.registry.mem;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.registry.Path;
import org.luwrain.registry.PathParser;

public final class RegistryImpl implements Registry
{
    static final String LOG_COMPONENT = "fsdir";

    private final Directory root = new Directory("root");

    @Override public synchronized boolean addDirectory(String path)
    {
	final Path p = parseAsDir(path);
	return addDirectory(p);
    }

    private boolean addDirectory(Path p)
    {
	NullCheck.notNull(p, "p");
	if (p.isRoot())
	    throw new IllegalArgumentException("the root directory may not be requested for creating");
	final String[] items = p.dirItems();
	Directory d = root;
	int pos = 0;
	while (pos < items.length)
	{
	    Directory dd = d.findSubdir(items[pos]);
	    if (dd == null)
		break;
	    d = dd;
	    ++pos;
	}
	if (pos >= items.length)//The directory already exists
	    return false;
	while(pos < items.length)
	{
	    d = d.createSubdir(items[pos]);
	    ++pos;
	}
	return true;
    }

    @Override public synchronized boolean deleteDirectory(String path)
    {
	NullCheck.notEmpty(path, "path");
	final Path p = parseAsDir(path);
	if (p.isRoot())
	    throw new IllegalArgumentException("Root directory may not be deleted");
	Directory d = findDirectory(p.dirItems());
	if (d == null)
	    return false;
	final String name = d.name;
	final Path parent = p.getParentOfDir();
	d = findDirectory(parent.dirItems());//Should never return null
	d.deleteSubdir(name);
	return true;
    }

    @Override public synchronized boolean deleteValue(String path)
    {
	final Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	Directory d = findDirectory(p.dirItems());
	if (d == null)
	    return false;
	return d.deleteValue(p.valueName());
    }

    @Override public synchronized boolean getBoolean(String path)
    {
	final Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	final Directory d = findDirectory(p.dirItems());
	if (d == null)
	    return false;
	return d.getBoolean(p.valueName());
    }

    @Override public synchronized String[] getDirectories(String path)
    {
	final Path p = parseAsDir(path);
	final Directory d = findDirectory(p.dirItems());
	if (d == null)
	    return null;
	return d.subdirs();
    }

    @Override public synchronized int getInteger(String path)
    {
	final Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	final Directory d = findDirectory(p.dirItems());
	if (d == null)
	    return 0;
	return d.getInteger(p.valueName());
    }

    @Override public synchronized String getString(String path)
    {
	final Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	final Directory d = findDirectory(p.dirItems());
	if (d == null)
	    return "";
	final String res = d.getString(p.valueName());
	return res;
    }

    @Override public synchronized String getStringDesignationOfType(int type)
    {
	switch (type)
	{
	case BOOLEAN:
	    return "boolean";
	case INTEGER:
	    return "integer";
	case STRING:
	    return "string";
	default:
	    return "invalid";
	}
    }

    @Override public synchronized int getTypeOf(String path)
    {
	try {
	    final Path p = parse(path);
	    if (p.isDirectory())
		return INVALID;
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return INVALID;
	    return d.getTypeOf(p.valueName());
	}
	catch (Exception e)
	{
	    return INVALID;
	}
    }

    @Override public synchronized String[] getValues(String path)
    {
	final Path p = parseAsDir(path);
	if (p.isRoot())
	    throw new IllegalArgumentException("root directory may not have values");
	final Directory d = findDirectory(p.dirItems());
	if (d == null)
	    return null;
	return d.values();
    }

    @Override public synchronized boolean hasDirectory(String path)
    {
	final Path p = parseAsDir(path);
	return findDirectory(p.dirItems()) != null;
    }

    @Override public synchronized boolean hasValue(String path)
    {
	final Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	final Directory d = findDirectory(p.dirItems());
	if (d == null)
	    return false;
	return d.hasValue(p.valueName());
    }

    @Override public synchronized boolean setBoolean(String path, boolean value)
    {
	final Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	Directory d = findDirectory(p.dirItems());
	if (d == null)
	{
	    if (!addDirectory(new Path(true, p.dirItems(), "")))
		return false;
	    d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	}
	return d.setBoolean(p.valueName(), value);
    }

    @Override public synchronized boolean setInteger(String path, int value)
    {
	final Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	Directory d = findDirectory(p.dirItems());
	if (d == null)
	{
	    if (!addDirectory(new Path(true, p.dirItems(), "")))
		return false;
	    d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	}
	return d.setInteger(p.valueName(), value);
    }

    @Override public synchronized boolean setString(String path, String value)
    {
	NullCheck.notNull(value, "value");
	final Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	Directory d = findDirectory(p.dirItems());
	if (d == null)
	{
	    if (!addDirectory(new Path(true, p.dirItems(), "")))
		return false;
	    d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	}
	return d.setString(p.valueName(), value);
    }

    public void load(File file) throws IOException
    {
	NullCheck.notNull(file, "file");
	final Loader loader = new Loader(this);
	final InputStream is = new FileInputStream(file);
	try {
	    loader.load(is);
	}
	finally {
	    is.close();
	}
    }

    //Returns the root if path is empty, null is returned if there is no such path without throwing an exception;
    private Directory findDirectory(String[] path)
    {
	NullCheck.notNull(path, "path");
	Directory d = root;
	for(int pos = 0;pos < path.length;++pos)
	{
	    if (path[pos] == null)
		throw new NullPointerException("path[" + pos + "] may not be null");
	    if (path[pos].isEmpty())
		throw new NullPointerException("path[" + pos + "] may not be empty");
	    d = d.findSubdir(path[pos]);
	    if (d == null)
		return null;
	}
	return d;
    }

    private Path parse(String path)
    {
	NullCheck.notNull(path, "path");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path may not be empty");
	final Path p = PathParser.parse(path);
	if (p == null)
	    throw new IllegalArgumentException("meaningless path");
	return p;
    }

    private Path parseAsDir(String path)
    {
	NullCheck.notNull(path, "path");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path may not be empty");
	final Path p = PathParser.parseAsDirectory(path);
	if (p == null)
	    throw new IllegalArgumentException("meaningless path");
	return p;
    }
}
