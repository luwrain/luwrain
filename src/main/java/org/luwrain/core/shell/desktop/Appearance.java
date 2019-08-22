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
	    announceUniRefInfo(info, flags.contains(Flags.BRIEF));
	    return;
	}
	luwrain.setEventResponse(DefaultEventResponse.text(luwrain.getSpeakableText(item.toString(), Luwrain.SpeakableTextType.NATURAL)));
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

    private void announceUniRefInfo(UniRefInfo uniRefInfo, boolean brief)
    {
	NullCheck.notNull(uniRefInfo, "uniRefInfo");
	if (!uniRefInfo.isAvailable())
	{
	    luwrain.setEventResponse(DefaultEventResponse.text(uniRefInfo.getValue()));
	    return;				    
	}
	if (brief)
	{
	    luwrain.setEventResponse(DefaultEventResponse.listItem(uniRefInfo.getTitle(), null));
	    return;				    
	}
	final String type = uniRefInfo.getValue().substring(0, uniRefInfo.getValue().indexOf(":")).toLowerCase();
	final String text = luwrain.getSpeakableText(uniRefInfo.getTitle(), Luwrain.SpeakableTextType.NATURAL);
	switch(type)
	{
	case "static":
	    luwrain.setEventResponse(DefaultEventResponse.text(text));
	    break;
	case "empty":
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    break;
	case "section":
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.DOC_SECTION, text, null));
	    break;
	default:
	    luwrain.setEventResponse(DefaultEventResponse.listItem(Sounds.DESKTOP_ITEM, text, Suggestions.CLICKABLE_LIST_ITEM));
	}
    }
}
