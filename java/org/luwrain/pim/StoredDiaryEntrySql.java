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

import java.util.*;
import java.sql.Connection;

class StoredDiaryEntrySql implements StoredDiaryEntry, Comparable<StoredDiaryEntrySql>
{
    private Connection con;

    public long id;
    public String title = "";
    public String comment = "";
    public Date dateTime = new Date();
    public int duration = 0;
    public int type = 0;
    public int status = 0;
    public int importance = 0;
    public String attributes = "";
    public String attributesType = "";

    public StoredDiaryEntrySql(Connection con)
    {
	this.con = con;
    }

    public StoredDiaryEntrySql(Connection con, int id)
    {
	this.con = con;
	this.id = id;
    }

    @Override public String getTitle()
    {
	return title != null?title:"";
    }

    @Override public void setTitle(String value) throws Exception
    {
	//FIXME:
    }

    @Override public String getComment()
    {
	return comment != null?comment:"";
    }

    @Override public void setComment(String value) throws Exception
    {
	//FIXME:
    }

    @Override public Date getDateTime()
    {
	return dateTime; 
    }

    @Override public void setDateTime(Date value) throws Exception
    {
	//FIXME:
    }

    @Override public int getDuration()
    {
	return duration;
    }

    @Override public void setDuration(int value) throws Exception
    {
	//FIXME:
    }

    @Override public int getType()
    {
	return type;
    }

    @Override public void setType(int type) throws Exception
    {
	//FIXME:
    }

    @Override public int getStatus()
    {
	return status;
    }

    @Override public void setStatus(int value) throws Exception
    {
	//FIXME:
    }

    @Override public int getImportance()
    {
	return importance;
    }

    @Override public void setImportance(int value) throws Exception
    {
	//FIXME:
    }

    @Override public String getAttributes()
    {
	return attributes != null?attributes:"";
    }

    @Override public void setAttributes(String value) throws Exception
    {
	//FIXME:
    }

    @Override public String getAttributesType()
    {
	return attributesType != null?attributesType:"";
    }

    @Override public void setAttributesType(String value) throws Exception
    {
	//FIXME:
    }

    public String toString()
    {
	return title != null?title:"";
    } 

    public boolean equals(Object o)
    {
	if (o == null || !(o instanceof StoredDiaryEntrySql))
	    return false;
	StoredDiaryEntrySql e = (StoredDiaryEntrySql)o;
	return id == e.id;
    }

    public int compareTo(StoredDiaryEntrySql entry)
    {
	if (dateTime == null || entry.dateTime == null)
	    return 0;
	return dateTime.compareTo(entry.dateTime);
    }
}
