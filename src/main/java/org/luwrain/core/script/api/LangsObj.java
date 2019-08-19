/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.script.api;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.i18n.*;
import org.luwrain.script.*;

final class LangsObj extends EmptyHookObject
{
    private final I18n i18n;
    private final Map<String, LangObj> langs = new HashMap();

    LangsObj(I18n i18n)
    {
	NullCheck.notNull(i18n, "i18n");
	this.i18n = i18n;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	if (langs.containsKey(name))
	    return langs.get(name);
	final Lang lang = i18n.getLang(name);
	if (lang == null)
	    return super.getMember(name);
	final LangObj langObj = new LangObj(lang);
	langs.put(name, langObj);
	return langObj;
    }
}
