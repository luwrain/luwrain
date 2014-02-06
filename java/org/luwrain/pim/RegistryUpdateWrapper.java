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

package org.luwrain.pim;

import org.luwrain.core.registry.Registry;

class RegistryUpdateWrapper
{
    public static void setString(Registry registry,
				 String path,
				 String value)  throws RegistryUpdateException
    {
	if (registry == null || path == null || path.trim().isEmpty() || value == null)
	    return;
	if (!registry.setString(path, value))
	    throw new RegistryUpdateException("Registry refuses to set value \'" + value + "\' to " + path);
    }

    public static void setInteger(Registry registry,
				 String path,
				 int value) throws RegistryUpdateException
    {
	if (registry == null || path == null || path.trim().isEmpty())
	    return;
	if (!registry.setInteger(path, value))
	    throw new RegistryUpdateException("Registry refuses to set value \'" + value + "\' to " + path);
    }

    public static void setBoolean(Registry registry,
				 String path,
				 boolean value) throws RegistryUpdateException
    {
	if (registry == null || path == null || path.trim().isEmpty())
	    return;
	if (!registry.setBoolean(path, value))
	    throw new RegistryUpdateException("Registry refuses to set value \'" + value + "\' to " + path);
    }

	    public static void deleteValue(Registry registry, String path) throws RegistryUpdateException
	    {
		if (registry == null || path == null || path.trim().isEmpty())
		    return;
		if (!registry.deleteValue(path))
		    throw new RegistryUpdateException("Registry refuses to delete value " + path);
	    }





}
