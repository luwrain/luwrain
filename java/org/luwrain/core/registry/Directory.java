/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

public class Directory
{
    private String name = "";
    private Directory[] subdirs;
    private Value[] values; 

    public Directory getSubdir(String subdirName)
    {
	if (subdirs == null)
	    return null;
	for(int i = 0;i < subdirs.length;++i)
	    if (subdirs[i] != null && subdirs[i].equals(subdirName))
		return subdirs[i];
	return null;
    }

    public Value getValue(String valueName)
    {
	if (values == null)
	    return null;
	for(int i = 0;i < values.length;++i)
	    if (values[i] != null && values[i].name.equals(valueName))
		return values[i];
	return null;
    }
}
