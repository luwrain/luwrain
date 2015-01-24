/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

public class VariableValue extends Value
{
    private Connection con;
    public long id;
    public long dirId;

    public VariableValue(Connection con)
    {
	this.con = con;
    }

    public VariableValue(Connection con,long id)
    {
	this.con = con;
	this.id = id;
    }

    public boolean insert() throws SQLException
    {
	PreparedStatement st = con.prepareStatement("INSERT INTO registry_value (id,dir_id,name,value_type,int_value,str_value,bool_value) VALUES (?,?,?,?,?,?,?);");
	st.setLong(1, id);
	st.setLong(2, dirId);
	st.setString(3, name.trim());
	st.setInt(4, type);
	st.setInt(5, intValue);
	st.setString(6, strValue);
	st.setBoolean(7, boolValue);
	return st.executeUpdate() >= 1;
    }

    public boolean select() throws SQLException
    {
	PreparedStatement st = con.prepareStatement("SELECT  dir_id,name,value_type,int_value,str_value,bool_value FROM registry_value WHERE id=?;");
	st.setLong(1, id);
	ResultSet rs = st.executeQuery();
	if (!rs.next()) 
	    return false;
	dirId = rs.getLong(1);
	name = rs.getString(2).trim();
	type = rs.getInt(3);
	intValue = rs.getInt(4);
	strValue = rs.getString(5);
	boolValue = rs.getBoolean(6);
	return true;
    }

    public boolean update() throws SQLException
    {
	PreparedStatement st = con.prepareStatement("UPDATE registry_value SET dir_id=?,name=?,value_type=?,int_value=?,str_value=?,bool_value=? WHERE id=?;");
	st.setLong(1, dirId);
	st.setString(2, name.trim());
	st.setInt(3, type);
	st.setInt(4, intValue);
	st.setString(5, strValue);
	st.setBoolean(6, boolValue);
	st.setLong(7, id);
	return st.executeUpdate() >= 1;
    }

    public boolean delete() throws SQLException
    {
	PreparedStatement st = con.prepareStatement("DELETE FROM registry_value WHERE id=?;");
	st.setLong(1, id);
	return st.executeUpdate() > 0;
    }

    public static void deleteAllInDir(Connection con, long id) throws SQLException
    {
	PreparedStatement st = con.prepareStatement("DELETE FROM registry_value WHERE dir_id=?;");
	st.setLong(1, id);
	st.executeUpdate();
    }

    public static VariableValue[] selectAllInDir(Connection con, long id) throws SQLException
    {
	PreparedStatement st = con.prepareStatement("SELECT  id,dir_id,name,value_type,int_value,str_value,bool_value FROM registry_value WHERE dir_id=?;");
	st.setLong(1, id);
	ResultSet rs = st.executeQuery();
	ArrayList<VariableValue> values = new ArrayList<VariableValue>();
	while (rs.next())
	{
	    VariableValue value = new VariableValue(con);
	    value.id = rs.getLong(1);
	    value.dirId = rs.getLong(2);
	    value.name = rs.getString(3).trim();
	    value.type = rs.getInt(4);
	    value.intValue = rs.getInt(5);
	    value.strValue = rs.getString(6);
	    value.boolValue = rs.getBoolean(7);
	    values.add(value);
	}
	return values.toArray(new VariableValue[values.size()]);
    }

    public static VariableValue selectByName(Connection con,
					   long id,
					   String name) throws SQLException
    {
	if (name == null || name.trim().isEmpty())
	    return null;
	PreparedStatement st = con.prepareStatement("SELECT  id,dir_id,name,value_type,int_value,str_value,bool_value FROM registry_value WHERE dir_id=? AND name=?;");
	st.setLong(1, id);
	st.setString(2, name);
	ResultSet rs = st.executeQuery();
	if (!rs.next()) 
	    return null;
	VariableValue value = new VariableValue(con);
	value.id = rs.getLong(1);
	value.dirId = rs.getLong(2);
	value.name = rs.getString(3).trim();
	value.type = rs.getInt(4);
	value.intValue = rs.getInt(5);
	value.strValue = rs.getString(6);
	value.boolValue = rs.getBoolean(7);
	return value;
    }
}
