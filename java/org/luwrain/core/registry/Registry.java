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

import java.sql.SQLException;
import java.util.ArrayList;
import  org.luwrain.core.Log;

public class Registry implements XmlReaderOutput
{
    public static final int INVALID = 0;
    public static final int INTEGER = 1;
    public static final int STRING = 2;
    public static final int BOOLEAN = 3;

    private Directory root = new Directory("");
    private VariableStorage storage;

    public boolean initWithConfFiles(String[] confFiles)
    {
	if (confFiles == null)
	    return false;
	for(String s: confFiles)
	{
	    if (s == null || s.isEmpty())
		continue;
	    Log.debug("init", "reading configuration file:" + s);
	    try {
		XmlReader reader = new XmlReader(s, this);
		reader.readFile();
	    }
	    catch(Exception e)
	    {
		Log.error("registry", "error while the config reading:" + e.getMessage());
	    return false;
	    }
	}
	return true;
    }

    public boolean initWithJdbc(java.sql.Connection jdbcCon)
    {
	if (jdbcCon == null)
	    return false;
	this.storage = new VariableStorage(jdbcCon);
	return true;
    }

    public String[] getDirectories(String pathStr)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return new String[0];
	Path path = PathParser.parseAsDirectory(pathStr);
	if (path == null || !path.isValidAbsoluteDir())
	    return new String[0];
	ArrayList<String> res = new ArrayList<String>();
	Directory s = findStaticDirectory(path);
	if(s != null && s.subdirs != null) 
	    for(Directory d: s.subdirs)
		if (d != null && d.name != null && !d.name.isEmpty())
		    res.add(d.name);
	if (storage != null)
	{
	    VariableDirectory[] v;
	    try {
		v = storage.getSubdirs(path);
		if (v != null)
		    for(VariableDirectory d: v)
			if (d != null && d.name != null && !d.name.isEmpty())
			    res.add(d.name);
	    }
	    catch(SQLException e)
	    {
		Log.error("registry", "problem getting subdirs for " + path.toString() + ":" + e.getMessage());
		e.printStackTrace();
	    }
	}
	return res.toArray(new String[res.size()]);
    }

    public String[] getValues(String pathStr)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return new String[0];
	Path path = PathParser.parseAsDirectory(pathStr);
	if (path == null || !path.isValidAbsoluteDir())
	    return new String[0];
	ArrayList<String> res = new ArrayList<String>();
	Directory s = findStaticDirectory(path);
	if(s != null && s.values != null) 
	    for(Value i: s.values)
		if (i != null && i.name != null && !i.name.isEmpty())
		    res.add(i.name);
	if (storage != null)
	{
	    VariableValue[] v;
	    try {
		v = storage.getValues(path);
		if (v != null)
		    for(VariableValue i: v)
			if (i != null && i.name != null && !i.name.isEmpty())
			    res.add(i.name);
	    }
	    catch(SQLException e)
	    {
		Log.error("registry", "problem getting values for " + path.toString() + ":" + e.getMessage());
		e.printStackTrace();
	    }
	}
	return res.toArray(new String[res.size()]);
    }

    public boolean hasDirectory(String pathStr)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return false;
	Path path = PathParser.parseAsDirectory(pathStr);
	if (path == null || !path.isValidAbsoluteDir())
	    return false;
	if (findStaticDirectory(path) != null)
	    return true;
	if (storage == null)
	    return false;
	try {
	return storage.dirExists(path);
	}
	catch(SQLException e)
	{
	    Log.error("registry", "jdbc problem while checking if the directory \'" + path.toString() + "\' exists:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    public boolean hasValue(String pathStr)
    {
	return getTypeOf(pathStr) != INVALID;
    }

    public int getTypeOf(String pathStr)
    {
	Value value = findValue(pathStr);
	return value != null?value.type:INVALID;
    }

    public int getInteger(String pathStr)
    {
	Value value = findValue(pathStr);
	return (value != null && value.type == INTEGER)?value.intValue:0;
    }

    public String getString(String pathStr)
    {
	Value value = findValue(pathStr);
	return (value != null && value.type == STRING)?value.strValue:"";
    }

    public boolean getBoolean(String pathStr)
    {
	Value value = findValue(pathStr);
	return (value != null && value.type == BOOLEAN)?value.boolValue:false;
    }

    public boolean setString(String pathStr, String value)
    {
	if (storage == null)
	    return false;
	if (pathStr == null || pathStr.isEmpty() || value == null)
	    return false;
	Path path = PathParser.parse(pathStr);
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	if (findStaticValue(path) != null)
	    return false;
	try {
	    if (!storage.exists(path))
		if (!storage.addValue(path))
		    return false;
	    VariableValue vv = storage.getValue(path);
	    vv.type = Registry.STRING;
	    vv.strValue = value;
	    return vv.update();
	}
	catch(SQLException e)
	{
	    Log.error("registry", "jdbc problem while setting string value to " + path.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    public boolean setStaticString(String pathStr, String value)
    {
	if (pathStr == null || pathStr.isEmpty() || value == null)
	    return false;
	Path path = PathParser.parse(pathStr);
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	Directory dir = ensureStaticDirectoryExists(path);
	if (dir == null)
	    return false;
	Value v = new Value();
	v.name = path.getValueName();
	v.type = STRING;
	v.strValue = value;
	dir.setValue(v);
	return true;
    }

    public boolean setInteger(String pathStr, int value)
    {
	if (storage == null)
	    return false;
	if (pathStr == null || pathStr.isEmpty())
	    return false;
	Path path = PathParser.parse(pathStr);
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	if (findStaticValue(path) != null)
	    return false;

	try {
	    if (!storage.exists(path))
		if (!storage.addValue(path))
		    return false;
	    VariableValue vv = storage.getValue(path);
	    vv.type = Registry.INTEGER;
	    vv.intValue = value;
	    return vv.update();
	}
	catch(SQLException e)
	{
	    Log.error("registry", "jdbc problem while setting string value to " + path.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    public boolean setStaticInteger(String pathStr, int value)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return false;
	Path path = PathParser.parse(pathStr);
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	Directory dir = ensureStaticDirectoryExists(path);
	if (dir == null)
	    return false;
	Value v = new Value();
	v.name = path.getValueName();
	v.type = INTEGER;
	v.intValue = value;
	dir.setValue(v);
	return true;
    }

    public boolean setBoolean(String pathStr, boolean value)
    {
	if (storage == null)
	    return false;
	if (pathStr == null || pathStr.isEmpty())
	    return false;
	Path path = PathParser.parse(pathStr);
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	if (findStaticValue(path) != null)
	    return false;
	try {
	    if (!storage.exists(path))
		if (!storage.addValue(path))
		    return false;
	    VariableValue vv = storage.getValue(path);
	    vv.type = Registry.BOOLEAN;
	    vv.boolValue = value;
	    return vv.update();
	}
	catch(SQLException e)
	{
	    Log.error("registry", "jdbc problem while setting string value to " + path.toString() + ":" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    public boolean setStaticBoolean(String pathStr, boolean value)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return false;
	Path path = PathParser.parse(pathStr);
	if (path == null || !path.isValidAbsoluteValue())
	    return false;
	Directory dir = ensureStaticDirectoryExists(path);
	if (dir == null)
	    return false;
	Value v = new Value();
	v.name = path.getValueName();
	v.type = BOOLEAN;
	v.boolValue = value;
	dir.setValue(v);
	return true;
    }

    public boolean addDirectory(String pathStr)
    {
	if (storage == null)
	    return false;
	if (pathStr == null || pathStr.isEmpty() ||
	    hasDirectory(pathStr))
	    return false;
	Path path = PathParser.parseAsDirectory(pathStr);
	if (path == null || !path.isValidAbsoluteDir())
	    return false;
	try {
	    return storage.addDirectory(path);
	}
	catch(SQLException e)
	{
	    Log.error("registry", "jdbc problem on inserted new directory \'" + path.toString() + "\':" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
    }

    public boolean deleteValue(String path)
    {
	//FIXME:
	return false;
    }

    public boolean deleteDir(String path)
    {
	//FIXME:
	return false;
    }

    public Directory findDirectoryForXmlReader(String pathStr)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return null;
	Path path = PathParser.parseAsDirectory(pathStr);
	if (path == null)
	return null;
	return ensureStaticDirectoryExists(path);
    }

    public boolean onNewXmlValue(Directory dir,
			      String valueName,
			      int type,
			      String valueStr)
    {
	if (valueName == null || valueName.trim().isEmpty() ||
	    dir == null || valueStr == null)
	    return false;
	Value value = new Value();
	value.type = type;
	value.name = valueName;
	switch(type)
	{
	case STRING:
	    value.strValue = valueStr;
	    break;
	case BOOLEAN:
	    if (valueStr.equals("TRUE") || valueStr.equals("True") || valueStr.equals("true"))
		value.boolValue = true; else 
		if (valueStr.equals("FALSE") || valueStr.equals("False") || valueStr.equals("false"))
		    value.boolValue = false; else
		    return false;
	    break;
	case INTEGER:
	    value.intValue = Integer.parseInt(valueStr);//FIXME:Error handling;
	    break;
	default:
	    return false;
	}
	dir.setValue(value);
	return true;
    }

    private Value findStaticValue(Path path)
    {
	if (path == null || !path.isValidAbsoluteValue())
	    return null;
	Directory dir = root;
	if (dir == null)
	    return null;
	for(String s:path.getDirItems())
	{
	    dir = dir.getSubdir(s);
	    if (dir == null)
		return null;
	}
	return dir.getValue(path.getValueName());
    }

    private VariableValue findVariableValue(Path path)
    {
	if (path == null || !path.isValidAbsoluteValue())
	    return null;
	try {
	    return storage != null?storage.getValue(path):null;
	}
	catch (SQLException e)
	{
	    Log.error("registry", "jdbc:" + e.getMessage());
	    return null;
	}
    }

    private Value findValue(Path path)
    {
	Value value = findStaticValue(path);
	if (value != null)
	    return value;
	return findVariableValue(path);
    }

    private Value findValue(String pathStr)
    {
	if (pathStr == null || pathStr.isEmpty())
	    return null;
	Path path = PathParser.parse(pathStr);
	return findValue(path);
    }

    private Directory ensureStaticDirectoryExists(Path path)
    {
	if (path == null || !path.isAbsolute())
	    return null;
	Directory dir = root;
	if (dir == null)
	    dir = root = new Directory("");
	for(String s: path.getDirItems())
	{
	    if (s.trim().isEmpty())
		continue;
	    Directory d = dir.getSubdir(s);
	    if (d == null)
	    {
		d = new Directory(s);
		dir.addSubdir(d);
	    }
	    dir = d;
	}
	return dir;
    }

    private Directory findStaticDirectory(Path path)
    {
	if (path == null || !path.isValidAbsoluteDir())
	    return null;
	Directory dir = root;
	if (dir == null)
	    return null;
	for(String s: path.getDirItems())
	{
	    if (s.trim().isEmpty())
		continue;
	    Directory d = dir.getSubdir(s);
	    if (d == null)
		return null;
	    dir = d;
	}
	return dir;
    }
}
