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

package org.luwrain.app.registry;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class ValuesListAppearance implements ListArea.Appearance<Value>
{
    private Luwrain luwrain;
    private Strings strings;

    public ValuesListAppearance(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
    }

    @Override public void announceItem(Value value, Set<Flags> flags)
    {
	NullCheck.notNull(value, "value");
	NullCheck.notNull(flags, "flags");
	luwrain.speak(value.name);
    }

    @Override public String getScreenAppearance(Value value, Set<Flags> flags)
    {
	NullCheck.notNull(value, "value");
	NullCheck.notNull(flags, "flags");
	return value.name;
    }

    @Override public int getObservableLeftBound(Value value)
    {
		return 0;
	//FIXME:
    }

    @Override public int getObservableRightBound(Value value)
    {
	return getScreenAppearance(value, EnumSet.noneOf(Flags.class)).length();
    }
}
