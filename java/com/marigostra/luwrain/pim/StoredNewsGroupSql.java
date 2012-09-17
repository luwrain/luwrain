/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.pim;

import java.sql.*;

public class StoredNewsGroupSql implements StoredNewsGroup
{
    private Connection con = null;
    public long id = 0;
    public String name = new String();
    public boolean hasMediaContent = false;
    public int orderIndex = 0;
    public int expireAfterDays = 30;

    public StoredNewsGroupSql(Connection con)
    {
	this.con = con;
    }

    public String getName()
    {
	return name;
    }

    public void setName(String name) throws SQLException
    {
	//FIXME:
    }

    public boolean hasMediaContent()
    {
	return hasMediaContent;
    }

    public void setHasMediaContent(boolean value) throws SQLException
    {
	//FIXME:
    }

    public int getOrderIndex()
    {
	return orderIndex;
    }

    public void setOrderIndex(int index) throws SQLException
    {
	//FIXME:
    }

    public int getExpireAfterDays()
    {
	return expireAfterDays;
    }

    public void setExpireAfterDays(int count) throws SQLException
    {
	//FIXME:
    }
}
