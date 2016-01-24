/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.registry.fsdir;

import java.io.*;
import java.util.*;

import org.luwrain.core.Registry;
import org.luwrain.core.Log;
import org.luwrain.core.NullCheck;

class Directory
{
    static final String STRINGS_VALUES_FILE = "strings.txt";
    static final String INTEGERS_VALUES_FILE = "integers.txt";
    static final String BOOLEANS_VALUES_FILE = "booleans.txt";

    private String name = "";
    private File dir;
    private Vector<Directory> subdirs;
    private TreeMap<String, Value> values;

    Directory(String name, File dir)
    {
	this.name = name;
	this.dir = dir;
	NullCheck.notNull(name, "name");
	NullCheck.notNull(dir, "dir");
	if (name.isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	if (!dir.isAbsolute())
	    throw new IllegalArgumentException("dir should denote an absolute path");
    }

    String name()
    {
	return name;
    }

    Directory createSubdir(String newName) throws IOException
    {
	NullCheck.notNull(newName, "newName");
	if (newName.isEmpty())
	    throw new IllegalArgumentException("newName may not be empty");
	loadSubdirs();
	Directory d = findSubdir(name);
	if (d != null)
	    return d;
	final File f = new File(dir, newName);
	if (!f.mkdir())
	    return null;
	d = new Directory(newName, f);
	if (!d.createValuesFiles())
	    return null;
	subdirs.add(d);
	return d;
    }

    private boolean createValuesFiles() throws IOException 
    {
	if (!(new File(dir, STRINGS_VALUES_FILE).createNewFile()))
	    return false;
	if (!(new File(dir, INTEGERS_VALUES_FILE).createNewFile()))
	    return false;
	if (!(new File(dir, BOOLEANS_VALUES_FILE).createNewFile()))
	    return false;
	return true;
    }

    boolean hasSubdir(String dirName) throws IOException
    {
	return findSubdir(dirName) != null;
    }

    //null means no subdirectory
    Directory findSubdir(String dirName) throws IOException
    {
	NullCheck.notNull(dirName, "dirName");
	if (dirName.isEmpty())
	    throw new IllegalArgumentException("dirName may not be empty");
	loadSubdirs();
	for(Directory s: subdirs)
	    if (dirName.equals(s.name()))
		return s;
	return null;
    }

    void delete() throws IOException
    {
	loadSubdirs();
	for(Directory d:subdirs)
	    d.delete();
	new File(dir, STRINGS_VALUES_FILE).delete();
	new File(dir, INTEGERS_VALUES_FILE).delete();
	new File(dir, BOOLEANS_VALUES_FILE).delete();
	dir.delete();
	values = null;
	subdirs = null;
    }

    boolean deleteValue(String valueName) throws IOException
    {
	NullCheck.notNull(valueName, "valueName");
	if (valueName.isEmpty())
	    throw new IllegalArgumentException("valueName may not be empty");
	loadValues();
	if (!values.containsKey(valueName))
	    return false;
	values.remove(valueName);
	saveValues();
	return true;
    }

    boolean getBoolean(String valueName) throws IOException
    {
	NullCheck.notNull(valueName, "valueName");
	if (valueName.isEmpty())
	    throw new IllegalArgumentException("valueName may not be empty");
	loadValues();
	if (!values.containsKey(valueName))
	    return false;
	final Value value = values.get(valueName);
	return value.type == Registry.BOOLEAN?value.boolValue:false;
    }

    int getInteger(String valueName) throws IOException
    {
	NullCheck.notNull(valueName, "valueName");
	if (valueName.isEmpty())
	    throw new IllegalArgumentException("valueName may not be empty");
	loadValues();
	if (!values.containsKey(valueName))
	    return 0;
	final Value value = values.get(valueName);
	return value.type == Registry.INTEGER?value.intValue:0;
    }

    public String getString(String valueName) throws IOException
    {
	NullCheck.notNull(valueName, "valueName");
	if (valueName.isEmpty())
	    throw new IllegalArgumentException("valueName may not be empty");
	loadValues();
	if (!values.containsKey(valueName))
	    return "";
	final Value value = values.get(valueName);
	return value.type == Registry.STRING?value.strValue:"";
    }

    boolean setBoolean(String valueName, boolean value) throws IOException
    {
	NullCheck.notNull(valueName, "valueName");
	if (valueName.isEmpty())
	    throw new IllegalArgumentException("valueName may not be empty");
	loadValues();
	if (!values.containsKey(valueName))
	{
	    values.put(valueName, new Value(value));
	    saveValues();
	    return true;
	}
	final Value v = values.get(valueName);
	v.type = Registry.BOOLEAN;
	v.boolValue = value;
	saveValues();
	return true;
    }

    boolean setInteger(String valueName, int value) throws IOException
    {
	NullCheck.notNull(valueName, "valueName");
	if (valueName.isEmpty())
	    throw new IllegalArgumentException("valueName may not be empty");
	loadValues();
	if (!values.containsKey(valueName))
	{
	    values.put(valueName, new Value(value));
	    saveValues();
	    return true;
	}
	final Value v = values.get(valueName);
	v.type = Registry.INTEGER;
	v.intValue = value;
	saveValues();
	return true;
    }

    boolean setString(String valueName, String value) throws IOException
    {
	NullCheck.notNull(valueName, "valueName");
	NullCheck.notNull(value, "value");
	if (valueName.isEmpty())
	    throw new IllegalArgumentException("valueName may not be empty");
	loadValues();
	if (!values.containsKey(valueName))
	{
	    values.put(valueName, new Value(value));
	    saveValues();
	    return true;
	}
	final Value v = values.get(valueName);
	v.type = Registry.STRING;
	v.strValue = value;
	saveValues();
	return true;
    }

    String[] subdirs() throws IOException
    {
	loadSubdirs();
	final LinkedList<String> v = new LinkedList<String>();
	for (Directory d: subdirs)
	    v.add(d.name());
	return v.toArray(new String[v.size()]);
    }

    String[] values() throws IOException
    {
	loadValues();
	final LinkedList<String> v = new LinkedList<String>();
	for(Map.Entry<String, Value> i: values.entrySet())
	    v.add(i.getKey());
	return v.toArray(new String[v.size()]);
    }

    boolean hasValue(String valueName) throws IOException
    {
	NullCheck.notNull(valueName, "valueName");
	if (valueName.isEmpty())
	    throw new IllegalArgumentException("valueName may not be empty");
	loadValues();
	return values.containsKey(valueName);
    }

    int getTypeOf(String valueName) throws IOException
    {
	NullCheck.notNull(valueName, "valueName");
	if (valueName.isEmpty())
	    throw new IllegalArgumentException("valueName may not be empty");
	loadValues();
	if (!values.containsKey(valueName))
	    return Registry.INVALID;
return values.get(valueName).type;
    }

    void refreshDeleted() throws IOException
    {
	subdirs = null;
	loadSubdirs();
    }

    private void loadValues() throws IOException
    {
	if (values != null)
	    return;
	values = new TreeMap<String, Value>();
	Map<String, String> raw;

	//strings
	raw = ValueReader.readValuesFromFile(new File(dir, STRINGS_VALUES_FILE).getAbsolutePath());
	for(Map.Entry<String, String> e: raw.entrySet())
	{
	    final String k = e.getKey();
	    final String v = e.getValue();
	    if (values. containsKey(k))
	    {
		Log.warning("registry", "doublicating of key \'" + k + "\' in values of " + dir.getAbsolutePath());
		continue;
	    }
	    values.put(k, new Value(v));
	}

	//booleans
	raw = ValueReader.readValuesFromFile(new File(dir, BOOLEANS_VALUES_FILE).getAbsolutePath());
	for(Map.Entry<String, String> e: raw.entrySet())
	{
	    final String k = e.getKey();
	    final String v = e.getValue();
	    if (values. containsKey(k))
	    {
		Log.warning("registry", "doublicating of key \'" + k + "\' in values of " + dir.getAbsolutePath());
		continue;
	    }
	    boolean res;
	    if (v.equals("true") || v.equals("True") || v.equals("TRUE"))
		res = true; else
	    if (v.equals("false") || v.equals("False") || v.equals("FALSE"))
		res = false; else
	    {
		Log.warning("registry", "key \'" + k + "\' in " + dir.getAbsolutePath() + "\' has an invalid boolean value \'" + v + "\'");
		continue;
	    }
	    values.put(e.getKey(), new Value(res));
	}

	//integers
	raw = ValueReader.readValuesFromFile(new File(dir, INTEGERS_VALUES_FILE).getAbsolutePath());
	for(Map.Entry<String, String> e: raw.entrySet())
	{
	    final String k = e.getKey();
	    final String v = e.getValue();
	    //	    Log.debug("fsdir", "k=" + k + ",v=" + v);
	    if (values. containsKey(k))
	    {
		Log.warning("registry", "doublicating of key \'" + k + "\' in values of " + dir.getAbsolutePath());
		continue;
	    }
	    int res;
	    try {
		res = Integer.parseInt(v);
	    }
	    catch (NumberFormatException ee)
	    {
		Log.warning("fsdir", "key \'" + k + "\' in " + dir.getAbsolutePath() + "\' has an invalid integer value \'" + v + "\'");
		continue;
	    }
	    values.put(e.getKey(), new Value(res));
	}
    }

    private void loadSubdirs() throws IOException
    {
	if (subdirs != null)
	    return;
	subdirs = new Vector<Directory>();
	final File[] content = dir.listFiles();
	for(File f: content)
	    if (f.isDirectory())
		subdirs.add(new Directory(f.getName(), f));
    }

    private void saveValues() throws IOException
    {
	if (values == null)
	    return;
	final TreeMap<String, String> stringValues = new TreeMap<String, String>();
	final TreeMap<String, String> integerValues = new TreeMap<String, String>();
	final TreeMap<String, String> booleanValues = new TreeMap<String, String>();
	for(Map.Entry<String, Value> e: values.entrySet())
	{
	    final String name = e.getKey();
	    final Value v = e.getValue();
	    switch(v.type)
	    {
	    case Registry.STRING:
		stringValues.put(name, v.strValue);
		break;
	    case Registry.INTEGER:
		integerValues.put(name, "" + v.intValue);
		break;
	    case Registry.BOOLEAN:
		booleanValues.put(name, v.boolValue?"true":"false");
		break;
	    }
	}
	ValueWriter.saveValuesToFile(stringValues, new File(dir, STRINGS_VALUES_FILE).getAbsolutePath());
		ValueWriter.saveValuesToFile(integerValues, new File(dir, INTEGERS_VALUES_FILE).getAbsolutePath());
	ValueWriter.saveValuesToFile(booleanValues, new File(dir, BOOLEANS_VALUES_FILE).getAbsolutePath());
    }
}
