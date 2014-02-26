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

package org.luwrain.core.registry;

import java.util.*;
import java.sql.*;

public class VariableDirectory
{
    private Connection con;
    public long id;
    public long parentId;
    public String name = "";

    public VariableDirectory(Connection con) 
    {
	this.con = con;
    }

    public VariableDirectory(Connection con, long id)
    {
	this.con = con;
	this.id = id;
    }

    public boolean select() throws SQLException
    {
	PreparedStatement st = con.prepareStatement("SELECT  parent_id,name FROM registry_dir WHERE id=?;");
	st.setLong(1, id);
	ResultSet rs = st.executeQuery();
	if (!rs.next())
	    return false;
	parentId = rs.getLong(1);
	name = rs.getString(2).trim();
	return true;
    }

    public VariableDirectory[] selectAllSubdirs() throws SQLException
    {
	PreparedStatement st = con.prepareStatement("SELECT  id,parent_id,name FROM registry_dir WHERE parent_id=? AND parent_id <> id;");
	st.setLong(1, id);
	ResultSet rs = st.executeQuery();
	ArrayList<VariableDirectory> dirs = new ArrayList<VariableDirectory>();
	while (rs.next())
	{
	    VariableDirectory dir = new VariableDirectory(con);
	    dir.id = rs.getLong(1);
	    dir.parentId = rs.getLong(2);
	    dir.name = rs.getString(3).trim();
	    dirs.add(dir);
	}
	return dirs.toArray(new VariableDirectory[dirs.size()]);
    }

    public VariableDirectory selectSubdirByName(String subdirName) throws SQLException
    {
	if (subdirName == null || subdirName.trim().isEmpty())
	    return null;
	PreparedStatement st = con.prepareStatement("SELECT  id,parent_id,name FROM registry_dir WHERE parent_id=? AND name=? AND parent_id <> id;");
	st.setLong(1, id);
	st.setString(2, subdirName);
	ResultSet rs = st.executeQuery();
	if (!rs.next())
	    return null;
	VariableDirectory dir = new VariableDirectory(con);
	dir.id = rs.getLong(1);
	dir.parentId = rs.getLong(2);
	dir.name = rs.getString(3).trim();
	return dir;
    }

    public boolean update() throws SQLException
    {
	PreparedStatement st = con.prepareStatement("UPDATE registry_dir SET parent_id=?,name=? WHERE id=?;");
	st.setLong(1, parentId);
	st.setString(2, name.trim());
	st.setLong(3, id);
	return st.executeUpdate() >= 1;
    }

    public boolean insert() throws SQLException
    {
	PreparedStatement st = con.prepareStatement("INSERT INTO registry_dir (parent_id,name) VALUES (?,?);");
	st.setLong(1, parentId);
	st.setString(2, name.trim());
	return st.executeUpdate() >= 1;
    }

    public boolean delete() throws SQLException
    {
	VariableDirectory[] subdirs = selectAllSubdirs();
	for(VariableDirectory i:subdirs)
	if (!i.delete())
	    return false;
	VariableValue.deleteAllInDir(con, id);
	PreparedStatement st = con.prepareStatement("DELETE FROM registry_dir WHERE id=?;");
	st.setLong(1, id);
	return st.executeUpdate() >= 1;
    }

    public static VariableDirectory selectRootDir(Connection con) throws SQLException
    {
	Statement st = con.createStatement();
	ResultSet rs = st.executeQuery("SELECT  id,parent_id,name FROM registry_dir WHERE parent_id=id;");
	if (!rs.next())
	    return null;
	VariableDirectory dir = new VariableDirectory(con);
	dir.id = rs.getLong(1);
	dir.parentId = rs.getLong(2);
	dir.name = rs.getString(3).trim();
	return dir;
    }
}
