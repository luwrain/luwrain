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

package org.luwrain.script;

import org.apache.logging.log4j.*;

import org.luwrain.core.*;
import org.luwrain.script.hooks.ChainOfResponsibilityHook;
import org.luwrain.script.hooks.PermissionHook;
import org.luwrain.script.hooks.NotificationHook;
import org.luwrain.script.hooks.ProviderHook;
import org.luwrain.script.hooks.TransformerHook;
import org.luwrain.script.hooks.CollectorHook;

import static org.luwrain.core.NullCheck.*;

public final class Hooks
{
    static private final Logger log = LogManager.getLogger();

    static public final String
	ANNOUNCEMENT = "luwrain.announcement",
		AREA_CLEAR = "luwrain.area.clear",
	AREA_REGION_POINT_SET = "luwrain.area.region.point.set",
	CLIPBOARD_COPY_ALL = "luwrain.clipboard.copy.all",
	EDIT_INPUT = "luwrain.edit.input",
	STARTUP = "luwrain.startup",
URL_OPEN = "luwrain.url.open";

            static public Object[] collector(HookContainer container, String hookName, Object[] args)
    {
	notNull(container, "container");
	notEmpty(hookName, "hookName");
	return new CollectorHook(container).run(hookName, args);
    }

                static public Object[] collectorForArrays(HookContainer container, String hookName, Object[] args)
    {
	notNull(container, "container");
	notEmpty(hookName, "hookName");
	return new CollectorHook(container).runForArrays(hookName, args);
    }

    //Throws RuntimeException
    static public boolean chainOfResponsibility(HookContainer container, String hookName, Object[] args) throws HookException
    {
	notNull(container, "container");
	notEmpty(hookName, "hookName");
	return new ChainOfResponsibilityHook(container).run(hookName, args);
    }

        static public boolean chainOfResponsibilityNoExc(HookContainer container, String hookName, Object[] args)
    {
	notNull(container, "container");
	notEmpty(hookName, "hookName");
	try {
	    return new ChainOfResponsibilityHook(container).run(hookName, args);
	}
	catch(Throwable ex)
	{
	    log.error("The " + hookName + " hook thrown an exception", ex);
	    return false;
	}
    }


    //Throws RuntimeException, returns true if all hooks returned true
        static public boolean permission(HookContainer container, String hookName, Object[] args)
    {
	NullCheck.notNull(container, "container");
	NullCheck.notEmpty(hookName, "hookName");
	return new PermissionHook(container).run(hookName, args);
    }


    //Throws RuntimeException, returns the first not-null value
        static public Object provider(HookContainer container, String hookName, Object[] args)
    {
	NullCheck.notNull(container, "container");
	NullCheck.notEmpty(hookName, "hookName");
	return new ProviderHook(container).run(hookName, args);
    }


            static public boolean notification(HookContainer container, String hookName, Object[] args)
    {
	NullCheck.notNull(container, "container");
	NullCheck.notEmpty(hookName, "hookName");
	return new NotificationHook(container).run(hookName, args);
    }

            static public Object transformer(HookContainer container, String hookName, Object arg)
    {
	NullCheck.notNull(container, "container");
	NullCheck.notEmpty(hookName, "hookName");
	return new TransformerHook(container).run(hookName, arg);
    }


}
