/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core.script;

import java.util.*;
import javax.script.*;
import jdk.nashorn.api.scripting.AbstractJSObject;
import jdk.nashorn.api.scripting.ScriptObjectMirror;
import java.util.function.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class Wrappers
{
    static final class Output extends AbstractJSObject
    {
	private final Context.Output output;
        Output(Context.Output output)
	{
	    NullCheck.notNull(output, "out");
	    this.output = output;
	}
	@Override public Object getMember(String name)
	{
	    NullCheck.notNull(name, "name");
	    switch(name)
	    {
	    case "print":
		return (Consumer)this::print;
	    default:
		return null;
	    }
	}
	private void print(Object b)
	{
	    if (b != null && b.toString() != null)
		output.onOutputLine(b.toString());
	}
    }
}
