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

package org.luwrain.core;

import java.util.*;

import org.luwrain.controls.ControlContext;

public final class UniRefUtils
{
    static public final String ALIAS = "alias";

        static public String makeUniRef(String component, String addr)
    {
	NullCheck.notEmpty(component, "component");
	NullCheck.notNull(addr, "addr");
	return component + ":" + addr;
    }

    static public UniRefInfo make(String str)
    {
	NullCheck.notNull(str, "str");
	final String text = str.trim();
	if (text.isEmpty())
	    return new UniRefInfo(UniRefInfo.makeValue(UniRefProcs.TYPE_EMPTY, ""), UniRefProcs.TYPE_EMPTY, "", "");
	return new UniRefInfo(UniRefInfo.makeValue(UniRefProcs.TYPE_STATIC, ""), UniRefProcs.TYPE_STATIC, text, text);
    }

    static public UniRefInfo make(java.io.File file)
    {
	NullCheck.notNull(file, "file");
	final String path = file.getAbsolutePath();
	return new UniRefInfo(UniRefInfo.makeValue(UniRefProcs.TYPE_FILE, path), UniRefProcs.TYPE_FILE, path, path);
    }

    static public UniRefInfo make(java.net.URL url)
    {
	NullCheck.notNull(url, "url");
	final String addr = url.toString();
	return new UniRefInfo(UniRefInfo.makeValue(UniRefProcs.TYPE_URL, addr), UniRefProcs.TYPE_URL, addr, addr);
    }

    static public UniRefInfo make(Luwrain luwrain, Object obj)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(obj, "obj");
	if (obj instanceof UniRefInfo)
	    return (UniRefInfo)obj;
	if (obj instanceof java.io.File)
	    return make((java.io.File)obj);
	if (obj instanceof java.net.URL)
	    return make((java.net.URL)obj);
	if (!(obj instanceof String))
	    return null;
	final String value = (String)obj;
	final UniRefInfo uniRefInfo = luwrain.getUniRefInfo(value);
	if (uniRefInfo != null)
	    return uniRefInfo;
	return make(value);
    }

    static public UniRefInfo[] make(Luwrain luwrain, Object[] objs)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNullItems(objs, "objs");
	final List<UniRefInfo> res = new LinkedList();
	for(Object o: objs)
	{
	    final UniRefInfo info = make(luwrain, o);
	    if (info != null)
		res.add(info);
	}
	return res.toArray(new UniRefInfo[res.size()]);
    }

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
