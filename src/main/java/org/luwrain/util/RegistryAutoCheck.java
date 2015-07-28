/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.util;

import org.luwrain.core.Registry;
import org.luwrain.core.Log;

public class RegistryAutoCheck
{
    private String loggingFacility = "";
    private Registry registry;
    private boolean writeLog = false;

    public RegistryAutoCheck(Registry registry)
    {
	this.registry = registry;
	if (registry == null)
	    throw new NullPointerException("Registry object may not be null");
	loggingFacility = "";
	writeLog = false;
    }

    public RegistryAutoCheck(Registry registry, String loggingFacility)
    {
	this.registry = registry;
	this.loggingFacility = loggingFacility != null?loggingFacility:"";
	if (registry == null)
	    throw new NullPointerException("Registry object may not be null");
	writeLog = !loggingFacility.isEmpty();
    }

    public int intAny(String path, int defaultValue)
    {
	if (path == null)
	    throw new NullPointerException("Path may not be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path:may not be empty");
	final int type = registry.getTypeOf(path);
	if (type == Registry.INTEGER)
	    return registry.getInteger(path);
	typeMismatchLogMessage(path, Registry.INTEGER, type);
	return defaultValue;
    }

    public int intPositive(String path, int defaultValue)
    {
	if (path == null)
	    throw new NullPointerException("Path may not be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path:may not be empty");
	final int type = registry.getTypeOf(path);
	if (type != Registry.INTEGER)
	{
	typeMismatchLogMessage(path, Registry.INTEGER, type);
	return defaultValue;
	}
	final int value = registry.getInteger(path);
	if (value < 0)
	{
	    if (writeLog)
		Log.warning(loggingFacility, "expecting the registry value " + path + " to be non-negative but its value is " + value + ", using the default value " + defaultValue);
	    return defaultValue;
	}
	    return value;
    }

    public int intPositiveNotZero(String path, int defaultValue)
    {
	if (path == null)
	    throw new NullPointerException("Path may not be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path:may not be empty");
	final int type = registry.getTypeOf(path);
	if (type != Registry.INTEGER)
	{
	typeMismatchLogMessage(path, Registry.INTEGER, type);
	return defaultValue;
	}
	final int value = registry.getInteger(path);
	if (value <= 0)
	{
	    if (writeLog)
		Log.warning(loggingFacility, "expecting the registry value " + path + " to be strictly positive but its value is " + value + ", using the default value " + defaultValue);
	    return defaultValue;
	}
	    return value;
    }

    public int intRange(String path,
			int min,
			int max,
			int defaultValue)
    {
	if (path == null)
	    throw new NullPointerException("Path may not be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path:may not be empty");
	if (max < min)
	    throw new IllegalArgumentException("min(" + min + ") is greater than max (" + max + ")");
	final int type = registry.getTypeOf(path);
	if (type != Registry.INTEGER)
	{
	typeMismatchLogMessage(path, Registry.INTEGER, type);
	return defaultValue;
	}
	final int value = registry.getInteger(path);
	if (value < min || value > max)
	{
	    if (writeLog)
		Log.warning(loggingFacility, "expecting the registry value " + path + " to be between " + min + " and " + max + " but its value is " + value + ", using the default value " + defaultValue);
	    return defaultValue;
	}
	    return value;
    }

    public String stringNotEmpty(String path, String defaultValue)
    {
	if (path == null)
	    throw new NullPointerException("Path may not be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path:may not be empty");
	if (defaultValue == null)
	    throw new NullPointerException("defaultValue  may not be null");
	final int type = registry.getTypeOf(path);
	if (type != Registry.STRING)
	{
	    typeMismatchLogMessage(path, Registry.STRING, type);
	    return defaultValue;
	}
	final String value = registry.getString(path);
	if (value == null || value.isEmpty())
	{
	    if (writeLog)
		Log.warning(loggingFacility, "expecting the registry value " + path + " be a non-empty string but it is empty, using default value \'" + defaultValue + "\'");
	    return defaultValue;
	}
	return value;
    }

    public String stringAny(String path, String defaultValue)
    {
	if (path == null)
	    throw new NullPointerException("Path may not be null");
	if (path.isEmpty())
	    throw new IllegalArgumentException("path:may not be empty");
	if (defaultValue == null)
	    throw new NullPointerException("defaultValue  may not be null");
	final int type = registry.getTypeOf(path);
	if (type != Registry.STRING)
	{
	    typeMismatchLogMessage(path, Registry.STRING, type);
	    return defaultValue;
	}
	final String value = registry.getString(path);
	return value != null?value:"";
    }

    private void typeMismatchLogMessage(String path,
					int expecting,
					int present)
    {
	if (!writeLog)
	    return;
Log.warning(loggingFacility, "expecting the registry value " + path + " be of type \'" + registry.getStringDesignationOfType(expecting) + "\' but it is \'" + registry.getStringDesignationOfType(present) + "\'");
    }
}
