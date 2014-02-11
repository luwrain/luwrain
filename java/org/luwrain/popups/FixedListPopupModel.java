/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.popups;

import java.util.*;

public class FixedListPopupModel implements ListPopupModel
{
    private String[] items;

    public FixedListPopupModel(String[] items)
    {
	if (items != null)
	{
	    this.items = new String[items.length];
	    for(int i = 0;i < items.length;++i)
		this.items[i] = items[i];
	    Arrays.sort(this.items);
	} else
	    this.items = new String[0];
    }

    public String getCompletion(String beginning)
    {
	if (beginning == null)
	    return "";
	ArrayList<String> m = new ArrayList<String>();
	for(String s: items)
	    if (beginning.isEmpty() || s.indexOf(beginning) == 0)
		m.add(s);
	if (m.size() == 0)
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

    public String[] getAlternatives(String beginning)
    {
	if (beginning == null || beginning.isEmpty())
	    return items;
	ArrayList<String> matching = new ArrayList<String>();
	for(String s: items)
	    if (s.indexOf(beginning) == 0)
		matching.add(s);
	return matching.toArray(new String[matching.size()]);
    }

    public String getListPopupPreviousItem(String text)
    {
	if (text == null || text.isEmpty())
	    return null;
	if (items == null || items.length <= 1)
	    return "";
	if (text.compareTo(items[0]) <= 0)
	    return "";
	for(int i = 1;i < items.length;++i)
	    if (text.compareTo(items[i]) <= 0)
		return items[i - 1];
	return items[items.length - 1];
    }

    public String getListPopupNextItem(String text)
    {
	if (text == null || text.isEmpty())
	    return (items != null && items.length > 0)?items[0]:null;
	if (items == null || items.length <= 1)
	    return null;
	if (text.compareTo(items[items.length - 1]) >= 0)
	    return null;
	for(int i = items.length - 2;i >= 0;--i)
	    if (text.compareTo(items[i]) >= 0)
		return items[i + 1];
	return items[0];
    }
}
