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

package org.luwrain.core.shell.desktop;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

final class Appearance implements ListArea.Appearance
{
    private final Luwrain luwrain;

    Appearance(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public void announceItem(Object item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");
	if (item instanceof UniRefInfo)
	{
	    final UniRefInfo info = (UniRefInfo)item;
	    UniRefUtils.defaultAnnouncement(new DefaultControlContext(luwrain), info.getValue(), Sounds.DESKTOP_ITEM, Suggestions.CLICKABLE_LIST_ITEM);
	    return;
	}
	luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.DESKTOP_ITEM, luwrain.getSpeakableText(item.toString(), Luwrain.SpeakableTextType.NATURAL), null));
    }

    @Override public String getScreenAppearance(Object item, Set<Flags> flags)
    {
	if (item == null)
	    return "";
	if (item instanceof String)
	    return (String)item;
	if (item instanceof UniRefInfo)
	{
	    final UniRefInfo i = (UniRefInfo)item;
	    return i.toString();
	}
	return "";
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 0;
    }

    @Override public int getObservableRightBound(Object item)
    {
	return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
    }
}
