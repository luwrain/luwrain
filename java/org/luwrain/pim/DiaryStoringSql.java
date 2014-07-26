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
import java.sql .*;

class DiaryStoringSql implements DiaryStoring
{
    private Connection con = null;

public DiaryStoringSql(Connection con)
    {
	this.con = con;
    }

    @Override public StoredDiaryEntry[] loadEntriesForDate(java.util.Date date) throws Exception
    {
	if (date == null)
	    return new StoredDiaryEntry[0];
	PreparedStatement st = con.prepareStatement("SELECT id,	title,comment,date_time,duration,type,status,importance,attributes,attr_type FROM diary_entry WHERE date_time = ?; ORDER BY date_time");
	st.setDate(1, new java.sql.Date(date.getTime()));
	ResultSet rs = st.executeQuery();
	Vector<StoredDiaryEntry> v = new Vector<StoredDiaryEntry>();
	while (rs.next())
	{
	    StoredDiaryEntrySql e = new StoredDiaryEntrySql(con);
	    e.id = rs.getLong(1);
	    e.title = rs.getString(2).trim();
	    e.comment = rs.getString(3).trim();
	    e.dateTime = rs.getDate(4);
	    e.duration = rs.getInt(5);
	    e.type = rs.getInt(6);
	    e.status = rs.getInt(7);
	    e.importance = rs.getInt(8);
	    e.attributes = rs.getString(9);
	    e.attributesType = rs.getString(10).trim();
	    v.add(e);
	}
	return v.toArray(new StoredDiaryEntry[v.size()]);
    }

    @Override public void saveEntry(DiaryEntry entry) throws Exception
    {
	if (entry == null || entry.title == null || entry.title.trim().isEmpty())
	    return;
	PreparedStatement st = con.prepareStatement("INSERT INTO diary_entry (title,comment,date_time,duration,type,status,importance,attributes,attr_type) VALUES (?,?,?,?,?,?,?,?,?);");
	st.setString(1, entry.title.trim());
	st.setString(2, entry.comment != null?entry.comment.trim():"");
	st.setDate(3, new java.sql.Date(entry.dateTime.getTime()));
	st.setInt(4, entry.duration);
	st.setInt(5, entry.type);
	st.setInt(6, entry.status);
	st.setInt(7, 	entry.importance);
	st.setString(8, entry.attributes != null?entry.attributes:"");
	st.setString(9, entry.attributesType != null?entry.attributesType.trim():"");
	st.executeUpdate();
    }

    @Override public boolean deleteEntry(StoredDiaryEntry entry) throws Exception
    {
	if (entry == null || !(entry instanceof StoredDiaryEntrySql))
	    return false;
	StoredDiaryEntrySql e = (StoredDiaryEntrySql)entry;
	PreparedStatement st = con.prepareStatement("DELETE FROM diary_entry WHERE id=?;");
	st.setLong(1, e.id);
	return st.executeUpdate() == 1;
    }
}
