/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.extensions;

import org.luwrain.base.*;
import org.luwrain.core.*;

public final class LoadedExtension
{
    public final Extension ext;
    public final Luwrain luwrain;
    public final String id;
    public final ExtensionObject[] extObjects;

    public Command[] commands;
    //    public Shortcut[] shortcuts;
    public UniRefProc[] uniRefProcs;
    public org.luwrain.cpanel.Factory[] controlPanelFactories;

    LoadedExtension(Extension ext, Luwrain luwrain,
		    ExtensionObject[] extObjects)
    {
	NullCheck.notNull(ext, "ext");
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNullItems(extObjects, "extObjects");
	this.ext = ext;
	this.luwrain = luwrain;
	this.id = java.util.UUID.randomUUID().toString();
	this.extObjects = extObjects;
    }
}
