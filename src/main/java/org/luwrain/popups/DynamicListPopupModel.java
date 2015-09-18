/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.popups;

import java.util.*;

public abstract class DynamicListPopupModel implements EditListPopupModel
{
    //Items must be ordered and all of them should be greater than an empty item;
    protected abstract EditListPopupItem[] getItems(String context);
    protected abstract EditListPopupItem getEmptyItem(String context);

    @Override public String getCompletion(String beginning)
    {
	if (beginning == null)
	    return "";
	final EditListPopupItem[] fullItems = getItems(beginning);
	if (fullItems == null || fullItems.length < 1)
	    return "";
	final String[] items = new String[fullItems.length];
	for(int i = 0;i < fullItems.length;++i)
	    items[i] = fullItems[i].value();
	Vector<String> m = new Vector<String>();
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
	final EditListPopupItem[] fullItems = getItems(beginning);
	if (fullItems == null || fullItems.length < 1)
	    return new String[0];
	final String[] items = new String[fullItems.length];
	for(int i = 0;i < fullItems.length;++i)
	    items[i] = fullItems[i].value();
	if (beginning == null || beginning.isEmpty())
	    return items;
	Vector<String> matching = new Vector<String>();
	for(String s: items)
	    if (s.startsWith(beginning))
		matching.add(s);
	return matching.toArray(new String[matching.size()]);
    }

    @Override public EditListPopupItem getListPopupPreviousItem(String text)
    {
	if (text == null || text.isEmpty())
	    return null;
	final EditListPopupItem emptyItem = getEmptyItem(text);
	if (emptyItem == null)
	    return null;
	if (text.compareTo(emptyItem.value()) <= 0)
	    return null;
	final EditListPopupItem[] items = getItems(text);
	if (items == null || items.length <= 1)
	    return null;
	if (text.compareTo(items[0].value()) <= 0)
	    return emptyItem;
	for(int i = 1;i < items.length;++i)
	    if (text.compareTo(items[i].value()) <= 0)
		return items[i - 1];
	return items[items.length - 1];
    }

    @Override public EditListPopupItem getListPopupNextItem(String text)
    {
	final EditListPopupItem[] items = getItems(text);
	if (text == null || text.isEmpty())
	    return (items != null && items.length > 0)?items[0]:null;
	if (items == null || items.length <= 1)
	    return null;
	if (text.compareTo(items[items.length - 1].value()) >= 0)
	    return null;
	for(int i = items.length - 2;i >= 0;--i)
	    if (text.compareTo(items[i].value()) >= 0)
		return items[i + 1];
	return items[0];
    }
}
