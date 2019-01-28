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

package org.luwrain.core.speech;

import org.luwrain.core.*;

public final class SpeakingHook implements Luwrain.HookRunner
{
    private String text;

    public SpeakingHook(String text)
    {
	NullCheck.notNull(text, "text");
	this.text = text;
    }

    
    @Override public Luwrain.HookResult runHook(Luwrain.Hook hook)
    {
	NullCheck.notNull(hook, "hook");
final Object res = hook.run(new Object[]{text});
if (res == null)
	return Luwrain.HookResult.CONTINUE;
final String value = res.toString();
if (value != null)
    this.text = value;
	return Luwrain.HookResult.CONTINUE;
    }

    public String getText()
    {
	return text;
    }
}
