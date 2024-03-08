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
import static org.luwrain.core.NullCheck.*;

public final class JSoupDocObj extends JSoupNodeObj
{
    final Document doc;

	public JSoupDocObj(Document doc)
    {
	super(doc);
	notNull(doc, "doc");
	this.doc = doc;
    }

    @HostAccess.Export public ProxyExecutable getBody = this::getBodyImpl;
    private Object getBodyImpl(Value[] args)
    {
	if (doc.body() == null)
	    return null;
	return new JSoupNodeObj(doc.body());
    }
}
