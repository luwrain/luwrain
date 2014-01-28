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

package org.luwrain.pim;

public class StoredNewsGroupRegistry implements StoredNewsGroup
{
    public int id = 0;
    public String name = new String();
    public String[] urls = new String[0];
    public boolean hasMediaContent = false;
    public int orderIndex = 0;
    public int expireAfterDays = 30;

    public String getName()
    {
	return name;
    }

    public void setName(String name) throws Exception
    {
	//FIXME:
    }

    public String[] getUrls()
    {
	return urls;
    }

    public void setUrls(String[] urls) throws Exception
    {
	//FIXME:
    }

    public boolean hasMediaContent()
    {
	return hasMediaContent;
    }

    public void setHasMediaContent(boolean value) throws Exception
    {
	//FIXME:
    }

    public int getOrderIndex()
    {
	return orderIndex;
    }

    public void setOrderIndex(int index) throws Exception
    {
	//FIXME:
    }

    public int getExpireAfterDays()
    {
	return expireAfterDays;
    }

    public void setExpireAfterDays(int count) throws Exception
    {
	//FIXME:
    }

    public String toString()
    {
	return getName();
    }
}
