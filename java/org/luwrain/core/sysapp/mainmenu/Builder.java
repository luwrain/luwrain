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

package org.luwrain.core.sysapp.mainmenu;

import org.luwrain.core.sysapp.StringConstructor;

public class Builder
{
    private StringConstructor stringConstructor;
    private StandardBuilderPart standardPart;

    public Builder(StringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
	this.standardPart = new StandardBuilderPart(stringConstructor, new String[0]);
    }

    public Item[] buildItems(String[] actionsList)
    {
	if (actionsList != null)
	    standardPart.setContent(actionsList);
	//FIXME:Optional parts;
	return standardPart.buildItems();
    }
}
