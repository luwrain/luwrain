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

//LWR_API 1.0

package org.luwrain.core;

import org.luwrain.controls.ControlContext;

public final class UniRefUtils
{
    static public final String URL = "url";
    static public final String ALIAS = "link";

    static public void defaultAnnouncement(ControlContext context, UniRefInfo info, Sounds defaultSound, Suggestions clickableSuggestion)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(info, "info");
	if (!info.isAvailable())
	{
	    context.setEventResponse(DefaultEventResponse.listItem(defaultSound != null?defaultSound:Sounds.LIST_ITEM, getDefaultAnnouncementText(context, info), null));
	    return;
	}
	switch(info.getType())
	{
	case "static":
	    context.setEventResponse(DefaultEventResponse.listItem(defaultSound != null?defaultSound:Sounds.LIST_ITEM, getDefaultAnnouncementText(context, info), null));
	    break;
	case "section":
	    context.setEventResponse(DefaultEventResponse.listItem(Sounds.DOC_SECTION, getDefaultAnnouncementText(context, info), null));
	    break;
	    	case "empty":
		    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    break;
	default:
	    context.setEventResponse(DefaultEventResponse.listItem(defaultSound != null?defaultSound:Sounds.LIST_ITEM, getDefaultAnnouncementText(context, info), clickableSuggestion));
	     return;
	}
    }

    static public String getDefaultAnnouncementText(ControlContext context, UniRefInfo uniRefInfo)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(uniRefInfo, "uniRefInfo");
	if (!uniRefInfo.isAvailable())
	    return context.getSpeakableText(uniRefInfo.getValue(), Luwrain.SpeakableTextType.NATURAL);
	switch(uniRefInfo.getType())
	{
	case "file":
	case "url":
	    return context.getSpeakableText(uniRefInfo.getTitle(), Luwrain.SpeakableTextType.PROGRAMMING);
	default:
	    return context.getSpeakableText(uniRefInfo.getTitle(), Luwrain.SpeakableTextType.NATURAL);
	}
    }

    static public String makeAlias(String title, String uniRef)
    {
	NullCheck.notEmpty(title, "title");
	NullCheck.notNull(uniRef, "uniRef");
	return ALIAS + ":" + title.replaceAll(":", "\\\\:") + ":" + uniRef;
    }

    static public String makeUniRef(String component, String addr)
    {
	NullCheck.notEmpty(component, "component");
	NullCheck.notNull(addr, "addr");
	return component + ":" + addr;
    }

    static boolean isAlias(String uniref)
    {
	NullCheck.notNull(uniref, "uniref");
	if (uniref.isEmpty())
	    return false;
	return uniref.startsWith(ALIAS + ":");
    }

    static private int findAliasDelim(String aliasBody)
    {
	NullCheck.notNull(aliasBody, "aliasBody");
	int delim = 0;
	while(delim < aliasBody.length() &&
	      (aliasBody.charAt(delim) != ':' || (delim > 0 && aliasBody.charAt(delim - 1) == '\\')))
	    ++delim;
	return delim < aliasBody.length()?delim:-1;
    }
}
