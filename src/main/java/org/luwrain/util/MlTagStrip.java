/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.util;

import java.util.*;

import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import org.luwrain.core.*;

public class MlTagStrip
{

    private final StringBuilder builder = new StringBuilder();
    private final Document jsoupDoc;

    MlTagStrip(String text)
    {
	NullCheck.notNull(text, "text");
	jsoupDoc = Jsoup.parse(text);
    }

    private void run()
    {
	onNode(jsoupDoc.body());
    }

    private void onNode(Node node)
    {
	NullCheck.notNull(node, "node");
	final List<Node> nodes = node.childNodes();
	if (nodes == null)
	    return;
	for(Node n: nodes)
	{
	    if (n instanceof TextNode)
	    {
		final TextNode textNode = (TextNode)n;
		final String text = textNode.text();
		if (text != null)
		    builder.append(text);
		continue;
	    }
	    onNode(n);
	}
    }

    static public String run(String text)
    {
	NullCheck.notNull(text, "text");
	final MlTagStrip strip = new MlTagStrip(text);
	strip.run();
	return new String(strip.builder);
    }
}
