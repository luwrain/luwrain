/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.*;

final class Directory
{
    final String name;
    private final List<Directory> subdirs = new Vector();
    private Map<String, Value> values = new HashMap();

    Directory(String name)
    {
	NullCheck.notEmpty(name, "name");
	this.name = name;
    }

    String getName()
    {
	return name;
    }

    Directory createSubdir(String newName)
    {
	NullCheck.notEmpty(newName, "newName");
	Directory d = findSubdir(name);
	if (d != null)
	    return d;
	d = new Directory(newName);
	subdirs.add(d);
	return d;
    }

    boolean deleteSubdir(String dirName)
    {
	NullCheck.notEmpty(dirName, "dirName");
	for(int i = 0;i < subdirs.size();i++)
	    if (subdirs.get(i).name.equals(dirName))
	    {
		subdirs.remove(i);
		return true;
	    }
	return false;
    }

    boolean hasSubdir(String dirName)
    {
	return findSubdir(dirName) != null;
    }

    //null means no subdirectory
    Directory findSubdir(String dirName)
    {
	NullCheck.notEmpty(dirName, "dirName");
	for(Directory s: subdirs)
	    if (dirName.equals(s.getName()))
		return s;
	return null;
    }

    boolean deleteValue(String valueName)
    {
	NullCheck.notEmpty(valueName, "valueName");
	if (!values.containsKey(valueName))
	    return false;
	values.remove(valueName);
	return true;
    }

    boolean getBoolean(String valueName)
    {
	NullCheck.notEmpty(valueName, "valueName");
	if (!values.containsKey(valueName))
	    return false;
	final Value value = values.get(valueName);
	return value.type == Registry.BOOLEAN?value.boolValue:false;
    }

    int getInteger(String valueName)
    {
	NullCheck.notEmpty(valueName, "valueName");
	if (!values.containsKey(valueName))
	    return 0;
	final Value value = values.get(valueName);
	return value.type == Registry.INTEGER?value.intValue:0;
    }

    String getString(String valueName)
    {
	NullCheck.notEmpty(valueName, "valueName");
	if (!values.containsKey(valueName))
	    return "";
	final Value value = values.get(valueName);
	return value.type == Registry.STRING?value.strValue:"";
    }

    boolean setBoolean(String valueName, boolean value)
    {
	NullCheck.notEmpty(valueName, "valueName");
	if (!values.containsKey(valueName))
	{
	    values.put(valueName, new Value(value));
	    return true;
	}
	final Value v = values.get(valueName);
	v.type = Registry.BOOLEAN;
	v.boolValue = value;
	return true;
    }

    boolean setInteger(String valueName, int value)
    {
	NullCheck.notEmpty(valueName, "valueName");
	if (!values.containsKey(valueName))
	{
	    values.put(valueName, new Value(value));
	    return true;
	}
	final Value v = values.get(valueName);
	v.type = Registry.INTEGER;
	v.intValue = value;
	return true;
    }

    boolean setString(String valueName, String value)
    {
	NullCheck.notEmpty(valueName, "valueName");
	NullCheck.notNull(value, "value");
	if (!values.containsKey(valueName))
	{
	    values.put(valueName, new Value(value));
	    return true;
	}
	final Value v = values.get(valueName);
	v.type = Registry.STRING;
	v.strValue = value;
	return true;
    }

    String[] subdirs()
    {
	final List<String> v = new LinkedList();
	for (Directory d: subdirs)
	    v.add(d.getName());
	return v.toArray(new String[v.size()]);
    }

    String[] values()
    {
	final List<String> v = new LinkedList();
	for(Map.Entry<String, Value> i: values.entrySet())
	    v.add(i.getKey());
final String[] res = v.toArray(new String[v.size()]);
Arrays.sort(res);
return res;
    }

    boolean hasValue(String valueName)
    {
	NullCheck.notEmpty(valueName, "valueName");
	return values.containsKey(valueName);
    }

    int getTypeOf(String valueName)
    {
	NullCheck.notEmpty(valueName, "valueName");
	if (!values.containsKey(valueName))
	    return Registry.INVALID;
	return values.get(valueName).type;
    }
}
