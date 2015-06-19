/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

import java.util.*;

class ConsoleMlReaderListener implements MlReaderListener
{
    @Override public void onMlTagOpen(String tagName, Map<String, String> attrs)
    {
	System.out.println("Tag: " + tagName);
	if (attrs != null && !attrs.isEmpty())
	{
	    for(Map.Entry<String, String> e: attrs.entrySet())
		System.out.println("Attr: " + e.getKey() + "=" + e.getValue());
	}
	System.out.println();
    }

    @Override public void onMlText(String text, LinkedList<String> tagsStack)
    {
	if (tagsStack.contains("script"))
	{
	    System.out.println("# Script removed");
	    return;
	}
	System.out.println(text);
    }

    @Override public void onMlTagClose(String tagName)
    {
	System.out.println("Closed: " + tagName);
    }
}
