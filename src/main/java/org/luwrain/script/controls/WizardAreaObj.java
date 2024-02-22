/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.script.controls;

import java.util.*;

import org.graalvm.polyglot.*;


import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.script.core.*;
//import org.luwrain.util.*;
//import org.luwrain.controls.MultilineEdit.ModificationResult;

import static org.luwrain.core.NullCheck.*;
import static org.luwrain.script.ScriptUtils.*;

public class WizardAreaObj extends WizardArea
{
    final org.luwrain.script.core.Module module;
    final Value content;

    public WizardAreaObj(ControlContext context, org.luwrain.script.core.Module module, Value content)
    {
	super(context);
	notNull(module, "module");
	notNull(content, "content");
		if (!content.hasArrayElements())
	    throw new IllegalArgumentException("The wizard content object doesn't contain any elements");
	this.module = module;
	this.content = content;
    }

    Frame createFrame()
    {
	final Frame f = newFrame();
	final List<Object> items = getArrayItems(content);
	return f;
    }

    
}
