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

package org.luwrain.script;

import org.luwrain.core.*;
import org.luwrain.script2.hooks.ChainOfResponsibilityHook;
import org.luwrain.script.hooks.PermissionHook;
import org.luwrain.script.hooks.NotificationHook;
import org.luwrain.script.hooks.ProviderHook;
import org.luwrain.script2.hooks.CollectorHook;


public final class Hooks
{
    static public final String
	EDIT_INPUT = "luwrain.edit.input";

    //Throws RuntimeException
    static public boolean chainOfResponsibility(HookContainer container, String hookName, Object[] args)
    {
	NullCheck.notNull(container, "container");
	NullCheck.notEmpty(hookName, "hookName");
	return new ChainOfResponsibilityHook(container).run(hookName, args);
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

}
