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

//LWR_API 1.0

package org.luwrain.script.ml;

import java.util.*;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import org.luwrain.core.*;

final class JsNode implements ProxyObject
{
        static private String[] KEYS = new String[]{
	"childNodes",
    };
    static private final Set<String> KEYS_SET = new HashSet<>(Arrays.asList(KEYS));
    static private final ProxyArray KEYS_ARRAY = ProxyArray.fromArray((Object[])KEYS);

    private final Node node;
    private final ProxyArray childNodes;

	JsNode(Node node)
    {
	NullCheck.notNull(node, "node");
	this.node = node;
	this.childNodes = buildChildNodesArray();
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "childNodes":
	    return this.childNodes;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name) { return KEYS_SET.contains(name); }
    @Override public Object getMemberKeys() { return KEYS_ARRAY; }
    @Override public void putMember(String name, Value value) { throw new UnsupportedOperationException("The JsNode object doesn't support updating of its variables"); }

    private ProxyArray buildChildNodesArray()
    {
	final List<JsNode> res = new ArrayList<>();
	final List<Node> childNodes = node.childNodes();
	if (childNodes != null)
	for(Node c: childNodes)
	    res.add(new JsNode(c));
	return ProxyArray.fromArray((Object[])res.toArray(new JsNode[res.size()]));
    }
}
