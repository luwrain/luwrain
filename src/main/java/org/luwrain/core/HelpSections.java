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

package org.luwrain.core;

final class HelpSections
{
    private final Registry registry;

    HelpSections(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	this.registry = registry;
    }

    String getSectionUrl(String sectName)
    {
	NullCheck.notEmpty(sectName, "sectName");
	final String path = Registry.join(Settings.HELP_SECTIONS_PATH, sectName);
	if (registry.getTypeOf(path) != Registry.STRING)
	    return "";
	return registry.getString(path);
    }
}
