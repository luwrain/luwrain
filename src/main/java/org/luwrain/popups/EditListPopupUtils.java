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

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.popups.EditListPopup.Item;

public final class EditListPopupUtils
{
    static public class DefaultItem implements EditListPopup.Item
    {
	protected final String value;
	protected final String announcement;
	public DefaultItem()
	{
	    this.value = "";
	    this.announcement = "";
	}
	public DefaultItem(String value, String announcement)
	{
	    NullCheck.notNull(value, "value");
	    NullCheck.notNull(announcement, "announcement");
	    this.value = value;
	    this.announcement = announcement;
	}
	public DefaultItem(String value)
	{
	    this(value, value);
	}
	@Override public String getValue()
	{
	    return value;
	}
	@Override public String getAnnouncement()
	{
	    return announcement;
	}
	@Override public String toString()
	{
	    return value;
	}
	@Override public int compareTo(Object o)
	{
	    return value.compareTo(o.toString());
	}
    }

    static public class DefaultAppearance implements EditListPopup.Appearance
    {
	protected final Luwrain luwrain;
	protected Luwrain.SpeakableTextType speakableTextType;
	public DefaultAppearance(Luwrain luwrain, Luwrain.SpeakableTextType speakableTextType)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(speakableTextType, "speakableTextType");
	    this.luwrain = luwrain;
	    this.speakableTextType = speakableTextType;
	}
	public DefaultAppearance(Luwrain luwrain)
	{
	    this(luwrain, Luwrain.SpeakableTextType.NATURAL);
	}
	@Override public void announceItem(EditListPopup.Item item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    final String value = item.getValue();
	    if (!value.isEmpty())
	    luwrain.setEventResponse(DefaultEventResponse.listItem(luwrain.getSpeakableText(value, speakableTextType))); else
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	}
	@Override public String getSpeakableText(String prefix, String text)
	{
	    NullCheck.notNull(prefix, "prefix");
	    NullCheck.notNull(text, "text");
	    return prefix + luwrain.getSpeakableText(text, speakableTextType);
	}
    }

    static public abstract class DynamicModel implements EditListPopup.Model
    {
	//Items must be ordered and all of them should be greater than an empty item;
	protected abstract Item[] getItems(String context);
	protected abstract Item getEmptyItem(String context);
	@Override public String getCompletion(String beginning)
	{
	    if (beginning == null)
		return "";
	    final Item[] fullItems = getItems(beginning);
	    if (fullItems == null || fullItems.length < 1)
		return "";
	    final String[] items = new String[fullItems.length];
	    for(int i = 0;i < fullItems.length;++i)
		items[i] = fullItems[i].getValue();
	    final List<String> m = new ArrayList<String>();
	    for(String s: items)
		//	    if (beginning.isEmpty() || s.indexOf(beginning) == 0)
		if (beginning.isEmpty() || s.startsWith(beginning))
		    m.add(s);
	    if (m.isEmpty())
		return "";
	    String[] matching = m.toArray(new String[m.size()]);
	    String res = "";
	    while(true)
	    {
		if (beginning.length() + res.length() >= matching[0].length())
		    break;
		final char c = matching[0].charAt(beginning.length() + res.length());
		int k;
		for(k = 1;k < matching.length;++k)
		    if (beginning.length() + res.length() >= matching[k].length() ||
			matching[k].charAt(beginning.length() + res.length()) != c)
			break;
		if (k >= matching.length)
		    res += c; else
		    break;
	    }
	    return res;
	}
	@Override public String[] getAlternatives(String beginning)
	{
	    final Item[] fullItems = getItems(beginning);
	    if (fullItems == null || fullItems.length < 1)
		return new String[0];
	    final String[] items = new String[fullItems.length];
	    for(int i = 0;i < fullItems.length;++i)
		items[i] = fullItems[i].getValue();
	    if (beginning == null || beginning.isEmpty())
		return items;
	    final List<String> matching = new ArrayList();
	    for(String s: items)
		if (s.startsWith(beginning))
		    matching.add(s);
	    return matching.toArray(new String[matching.size()]);
	}
	@Override public Item getListPopupPreviousItem(String text)
	{
	    if (text == null || text.isEmpty())
		return null;
	    final Item emptyItem = getEmptyItem(text);
	    if (emptyItem == null)
		return null;
	    if (text.compareTo(emptyItem.getValue()) <= 0)
		return null;
	    final Item[] items = getItems(text);
	    if (items == null || items.length <= 1)
		return null;
	    if (text.compareTo(items[0].getValue()) <= 0)
		return emptyItem;
	    for(int i = 1;i < items.length;++i)
		if (text.compareTo(items[i].getValue()) <= 0)
		    return items[i - 1];
	    return items[items.length - 1];
	}
	@Override public Item getListPopupNextItem(String text)
	{
	    final Item[] items = getItems(text);
	    if (text == null || text.isEmpty())
		return (items != null && items.length > 0)?items[0]:null;
	    if (items == null || items.length <= 1)
		return null;
	    if (text.compareTo(items[items.length - 1].getValue()) >= 0)
		return null;
	    for(int i = items.length - 2;i >= 0;--i)
		if (text.compareTo(items[i].getValue()) >= 0)
		    return items[i + 1];
	    return items[0];
	}
    }

    static public class FixedModel extends DynamicModel
    {
	protected final EditListPopup.Item[] fixedItems;
	public FixedModel(String[] items)
	{
	    NullCheck.notNullItems(items, "items");
	    final List<Item> v = new ArrayList();
	    for(String s: items)
		if (!s.isEmpty())
		    v.add(new DefaultItem(s));
	    this.fixedItems = v.toArray(new Item[v.size()]);
	    Arrays.sort(this.fixedItems);
	}
	@Override protected Item[] getItems(String context)
	{
	    //Returning every time the same items regardless the context;
	    return fixedItems.clone();
	}
	@Override protected Item getEmptyItem(String context)
	{
	    return new DefaultItem();
	}
    }
}
