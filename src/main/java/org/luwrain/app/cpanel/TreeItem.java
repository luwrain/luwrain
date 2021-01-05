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

package org.luwrain.app.cpanel;

import java.util.*;

import org.luwrain.core.NullCheck;
import org.luwrain.cpanel.*;

class TreeItem 
{
    Element el;
    Factory factory;
    boolean onDemandFilled = false;
    Section sect = null;
    final LinkedList<Element> children = new LinkedList<Element>();

    TreeItem(Element el, Factory factory)
    {
	NullCheck.notNull(el, "el");
	NullCheck.notNull(factory, "factory");
	this.el = el;
	this.factory = factory;
    }
}
