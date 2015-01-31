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

import java.util.*;
import java.io.IOException;
import java.io.File;

import org.luwrain.core.Registry;
import org.luwrain.core.Log;
import org.luwrain.registry.Path;
import org.luwrain.registry.PathParser;

public class RegistryImpl implements Registry
{
    private String base = "";
    private Directory root;

    public RegistryImpl(String base)
    {
	this.base = base;
	if (base == null)
	    throw new NullPointerException("base may not be null");
	if (base.isEmpty())
	    throw new IllegalArgumentException("base may not be empty");
	root = new Directory("root", new File(base));
    }

    @Override public boolean addDirectory(String path)
    {
	Path p = parseAsDir(path);
	if (p.isRoot())
	    throw new IllegalArgumentException("the root directory may not be asked for creation");
	final String[] items = p.dirItems();
	Directory d = root;
	int pos = 0;
	try {
	    while (pos < items.length)
	    {
		Directory dd = d.findSubdir(items[pos]);
		if (dd == null)
		    break;
		d = dd;
		++pos;
	    }
	    if (pos >= items.length)//The directory already exists;
		return true;
	    while(pos < items.length)
	    {
		d = d.createSubdir(items[pos]);
		++pos;
	    }
	}
	catch(IOException e)
	{
	    Log.error("registry", "error while creating registry directory " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	return true;
    }

    @Override public boolean deleteDirectory(String path)
    {
	Path p = parseAsDir(path);
	if (p.isAbsolute())
	    throw new IllegalArgumentException("Root directory may not be deleted");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	    d.delete();
	    Path parent = p.getParentOfDir();
	    d = findDirectory(parent.dirItems());//Should never return null;
	    d.refreshDeleted();
	    return true;
	}
	catch(IOException e)
	{
	    Log.error("registrr", "error while opening registry directory " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    @Override public boolean deleteValue(String path)
    {
	Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	    return d.deleteValue(p.valueName());
	}
	catch(IOException e)
	{
	    Log.error("registry", "error while deleting a value " + p.toString() + " from registry:" + e.getMessage());
	    return false;
	}
    }

    @Override public boolean getBoolean(String path)
    {
	Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	    return d.getBoolean(p.valueName());
	}
	catch (IOException e)
	{
	    Log.error("registry", "error reading boolean value " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    @Override public String[] getDirectories(String path)
    {
	Path p = parseAsDir(path);
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return null;
	    return d.subdirs();
	}
	catch (IOException e)
	{
	    Log.error("registry", "error while reading list of subdirectories of " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return null;
	}
    }

    @Override public int getInteger(String path)
    {
	Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return 0;
	    return d.getInteger(p.valueName());
	}
	catch (IOException e)
	{
	    Log.error("registry", "error reading integer value " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return 0;
	}
    }

    @Override public String getString(String path)
    {
	Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return "";
	    return d.getString(p.valueName());
	}
	catch (IOException e)
	{
	    Log.error("registry", "error reading string value " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return "";
	}
    }

    @Override public String getStringDesignationOfType(int type)
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

    @Override public int getTypeOf(String path)
    {
	try {
	    Path p = parse(path);
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

    @Override public String[] getValues(String path)
    {
	Path p = parseAsDir(path);
	if (!p.isRoot())
	    throw new IllegalArgumentException("root directory may not have values");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return null;
	    return d.values();
	}
	catch (IOException e)
	{
	    Log.error("registry", "error while reading list of values of " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return null;
	}
    }

    @Override public boolean hasDirectory(String path)
    {
	Path p = parseAsDir(path);
	try {
	    return findDirectory(p.dirItems()) != null;
	}
	catch (IOException e)
	{
	    Log.error("registry", "error while checking a directory " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    @Override public boolean hasValue(String path)
    {
	Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	    return d.hasValue(p.valueName());
	}
	catch(IOException e)
	{
	    Log.error("registry", "error while checking a value " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    @Override public boolean setBoolean(String path, boolean value)
    {
	Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	    return d.setBoolean(p.valueName(), value);
	}
	catch (IOException e)
	{
	    Log.error("registry", "error setting boolean value " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    @Override public boolean setInteger(String path, int value)
    {
	Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	    return d.setInteger(p.valueName(), value);
	}
	catch (IOException e)
	{
	    Log.error("registry", "error setting integer value " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    @Override public boolean setString(String path, String value)
    {
	if (value == null)
	    throw new NullPointerException("value may not be null");
	Path p = parse(path);
	if (p.isDirectory())
	    throw new IllegalArgumentException("path addresses a directory, not a value");
	try {
	    Directory d = findDirectory(p.dirItems());
	    if (d == null)
		return false;
	    return d.setString(p.valueName(), value);
	}
	catch (IOException e)
	{
	    Log.error("registry", "error setting string value " + p.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    //Returns the root if path is empty, null is returned if there is no such path without throwing an exception;
    private Directory findDirectory(String[] path) throws IOException
    {
	if (path == null)
	    throw new NullPointerException("path may not be null");
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
	if (path == null)
	    throw new NullPointerException("path may not be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path may not be empty");
	Path p = PathParser.parse(path);
	if (p == null)
	    throw new IllegalArgumentException("meaningless path");
	return p;
    }

    private Path parseAsDir(String path)
    {
	if (path == null)
	    throw new NullPointerException("path may not be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path may not be empty");
	Path p = PathParser.parseAsDirectory(path);
	if (p == null)
	    throw new IllegalArgumentException("meaningless path");
	return p;
    }
}
