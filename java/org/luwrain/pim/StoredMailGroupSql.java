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

import java.sql.SQLException;
import java.sql.Connection;

public class StoredMailGroupSql implements StoredMailGroup
{
    private Connection con = null;

    public long id = 0;
    public long parentGroupId = 0;
    public String name = new String();
    public String groupType = new String();
    public int orderIndex = 0;
    public int expireAfterDays = 0;
    public String extInfo = new String();

    public     StoredMailGroupSql(Connection con)
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

    public String getGroupType()
    {
	return groupType;
    }

    public void setGroupType(String value) throws SQLException
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

    public String getExtInfo()
    {
	return extInfo;
    }

    public void setExtInfo(String value) throws SQLException
    {
	//FIXME:
    }
}
