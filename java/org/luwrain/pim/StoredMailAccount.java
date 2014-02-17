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

public interface StoredMailAccount
{
    String getName();
    void setName(String value) throws Exception;
    int getType();
    void setType(int value) throws Exception;
    int getProtocol();
    void setProtocol(int value) throws Exception;
    String getHost();
    void setHost(String value) throws Exception;
    int getPort();
    void setPort(int value) throws Exception;
    String getLogin();
    void setLogin(String value) throws Exception;
    String getPasswd();
    void setPasswd(String value) throws Exception;
}
