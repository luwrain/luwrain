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

//LWR_API 1.0

package org.luwrain.script.ml;

import java.util.*;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;
import org.jsoup.*;
import org.jsoup.nodes.*;

import org.luwrain.core.*;

import org.luwrain.core.*;

import static org.luwrain.script.ScriptUtils.*;
import static org.luwrain.core.NullCheck.*;

public class JSoupNodeObj
{
    final Node node;

	JSoupNodeObj(Node node)
    {
	notNull(node, "node");
	this.node = node;
    }

    @HostAccess.Export public ProxyExecutable getChildNodes = this::getChildNodesImpl;
    private Object getChildNodesImpl(Value[] args)
    {
	final var childNodes = node.childNodes();
	if (childNodes == null)
	    return null;
	final var res = new ArrayList<JSoupNodeObj>();
	for(var i: childNodes)
	    res.add(new JSoupNodeObj(i));
	return ProxyArray.fromArray((Object[])res.toArray(new JSoupNodeObj[res.size()]));
    }

    @HostAccess.Export public ProxyExecutable getTagName = this::getTagNameImpl;
    private Object getTagNameImpl(Value[] args)
    {
	if (node instanceof Element el)
	return el.tagName();
	throw new IllegalArgumentException("This node is not an element, unable to call getTagName()");
    }

    @HostAccess.Export public ProxyExecutable getText = this::getTextImpl;
    private Object getTextImpl(Value[] args)
    {
	return node.toString();
    }

        @HostAccess.Export public ProxyExecutable getType = this::getTypeImpl;
    private Object getTypeImpl(Value[] args)
    {
	return node.getClass().getSimpleName();
    }

            @HostAccess.Export public ProxyExecutable find = this::findImpl;
    private Object findImpl(Value[] args)
    {
	if (!notNullAndLen(args, 1) || !args[0].canExecute())
	    throw new IllegalArgumentException("Node.find() takes exactly one function argument");
	final var nodes = node.childNodes();
	if (nodes == null)
	    return null;
	for(final var n: nodes)
	{
	    final var obj = new JSoupNodeObj(n);
	    final Value res = args[0].execute(obj);
	    if (res != null && res.isBoolean() && res.asBoolean())
		return obj;
	}
	return null;
    }


	
}
