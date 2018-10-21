
package org.luwrain.registry.mem;

import java.util.*;

import org.luwrain.core.*;

final class Directory
{
    final String name;
    private Vector<Directory> subdirs;
    private TreeMap<String, Value> values;

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

    public String getString(String valueName)
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
	return v.toArray(new String[v.size()]);
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
