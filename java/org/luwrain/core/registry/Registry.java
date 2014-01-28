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

import  org.luwrain.core.Log;
import java.sql.SQLException;

public class Registry
{
    public static final int INVALID = 0;
    public static final int INTEGER = 1;
    public static final int STRING = 2;
    public static final int BOOLEAN = 3;

    private Directory root;
    private VariableStorage storage;

    public boolean init(String[] confFiles, java.sql.Connection jdbcCon)
    {
	if (confFiles == null || jdbcCon == null)
	{
	    Log.error("registry", "invalid parameters for initialization procedure");
	    return false;
	}
	for(int i = 0;i < confFiles.length;++i)
	    if (confFiles[i] == null || confFiles[i].isEmpty())
	    {
		Log.error("registry", "an empty configuration file name  for initialization procedure");
		return false;
	    }
	//FIXME:init static;
	this.storage = new VariableStorage(jdbcCon);
	return true;
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

    public boolean getBool(String pathStr)
    {
	Value value = findValue(pathStr);
	return (value != null && value.type == BOOLEAN)?value.boolValue:false;
    }

    public boolean setString(String pathStr, String value)
    {
	//FIXME:
	return false;
    }

    public boolean setInteger(String pathStr, int value)
    {
	//FIXME:
	return false;
    }

    public boolean setBoolean(String pathStr, boolean value)
    {
	//FIXME:
	return false;
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
	if (pathStr == null || pathStr.trim().isEmpty())
	    return null;
	Path path = PathParser.parse(pathStr);
	return findValue(path);
    }
}
