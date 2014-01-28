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

public interface StoredMailMessage
{
    int getState();
    void setState(int value) throws SQLException;
    String getFromAddr();
    void setFromAddr(String value)  throws SQLException;
    String[] getFromAddrs();
    void setFromAddrs(String[] values)  throws SQLException;
    String getToAddr();
    void setToAddr(String value)  throws SQLException;
    String[] getToAddrs();
    void setToAddrs(String[] values)  throws SQLException;
    String getSubject();
    void setSubject(String value)  throws SQLException;
    java.util.Date getDate();
    void setDate(java.util.Date value)  throws SQLException;
    String getRawMsg();
    void setRawMsg(String value)  throws SQLException;
    String getContent();
    void setContent(String value)  throws SQLException;
    String getExtInfo();
    void setExtInfo(String value) throws SQLException;
}
