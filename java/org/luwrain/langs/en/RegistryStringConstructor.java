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

package org.luwrain.langs.en;

import java.util.*;

public class RegistryStringConstructor implements org.luwrain.app.registry.StringConstructor
{
    public String dirsAreaName()
    {
	return "Directories tree";
    }

    public String valuesAreaName()
    {
	return "List of values";
    }

    public String rootItemTitle()
    {
	return "Luwrain registry";
    }

    public String introduceStringValue(String name, String value)
    {
	return "String parameter " + name + " equals " + value;
    }

    public String introduceIntegerValue(String name, String value)
    {
	return "Integer parameter " + name + " equals " + value;
    }

    public String introduceBooleanValue(String name, boolean value)
    {
	return "String parameter " + name + " equals " + (value?"true":"false");
    }

    public String yes()
    {
	return "Yes";
    }

    public String no()
    {
	return "No";
    }

    public String newDirectoryTitle()
    {
	return "New registry directory";
    }

    public String newDirectoryPrefix(String parentName)
    {
	return "Name of the subdirectory for \"" + parentName + "\":";
    }

    public String directoryNameMayNotBeEmpty()
    {
	return "Registry directory name may not be empty";
    }

    public String directoryInsertionRejected(String parentName, String dirName)
    {
	return "Insertion of the registry directory with name \"" + dirName + "\" rejected";
    }
}
