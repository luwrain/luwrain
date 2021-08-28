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

package org.luwrain.script.core;

import java.util.*;
import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;

import org.luwrain.core.*;

final class I18nObj implements ProxyObject
{
    static private String[] KEYS = new String[]{
	"lang",
	"langs",
    };
    static private final Set<String> KEYS_SET = new HashSet(Arrays.asList(KEYS));
    static private final ProxyArray KEYS_ARRAY = ProxyArray.fromArray((Object[])KEYS);

    private final Luwrain luwrain;
    private LangObj activeLangObj = null;
    private LangsObj langsObj = null;

    I18nObj(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	refresh();
    }

    void refresh()
    {
	if (luwrain.i18n().getActiveLang() != null)
	    this.activeLangObj = new LangObj(luwrain.i18n().getActiveLang()); else
	    this.activeLangObj = null;
	this.langsObj = new LangsObj(luwrain.i18n());
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "lang":
	    return activeLangObj;
	case "langs":
	    return langsObj;
	default:
	    return null;
	}
    }

    @Override public boolean hasMember(String name) { return KEYS_SET.contains(name); }
    @Override public Object getMemberKeys() { return KEYS_ARRAY; }
    @Override public void putMember(String name, Value value) { throw new RuntimeException("The i18n object doesn't support updating of its variables"); }
}
