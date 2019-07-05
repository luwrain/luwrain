/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.script.api;

import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class RegistryObj extends AbstractJSObject
{
    private final Registry registry;
    private final String path;

    RegistryObj(Registry registry, String path)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(path, "path");
	this.registry = registry;
	this.path = path;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty())
	    return super.getMember(name);
	final String fullPath = Registry.join(path, buildName(name));
	if (registry.hasDirectory(fullPath))
	    return new RegistryObj(registry, fullPath);
	switch(registry.getTypeOf(fullPath))
	{
	case Registry.INVALID:
	    return null;
	case Registry.STRING:
	    return registry.getString(fullPath);
	case Registry.INTEGER:
	    return new Integer(registry.getInteger(fullPath));
	case Registry.BOOLEAN:
	    return new Boolean(registry.getBoolean(fullPath));
	default:
	    return null;
	}
    }

    private String buildName(String name)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty())
	    return "";
						      final StringBuilder b = new StringBuilder();
						      b.append(Character.toLowerCase(name.charAt(0)));
					      for(int i = 1;i < name.length();++i)
					      {
						  final char c = name.charAt(i);
						  if (Character.isUpperCase(c) && Character.isLowerCase(name.charAt(i - 1)))
						      b.append("-").append(Character.toLowerCase(c)); else
						      b.append(Character.toLowerCase(c));
					      }
					      return new String(b);
    }
}
