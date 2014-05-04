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

package org.luwrain.app.news;

import org.luwrain.pim.StoredNewsGroup;

class NewsGroupWrapper
{
    private StoredNewsGroup group;
    private int newArticleCount;

    public NewsGroupWrapper(StoredNewsGroup group, int newArticleCount)
    {
	this.group = group;
	this.newArticleCount = newArticleCount;
    }

    public StoredNewsGroup getStoredGroup()
    {
	return group;
    }

    public String toString()
    {
	if (group == null)
	    return "";
	if (newArticleCount == 0)
	    return group.getName();
	return group.getName() + " (" + newArticleCount + ")";
    }
}
