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

package org.luwrain.pim;

import java.sql.SQLException;

public interface MailStoring
{
    StoredMailGroup loadRootGroup() throws SQLException;
    StoredMailGroup[] loadChildGroups(StoredMailGroup parentGroup) throws SQLException;
    StoredMailMessage[] loadMessagesFromGroup(StoredMailGroup mailGroup) throws SQLException;
    void addMessageToGroup(StoredMailGroup mailGroup, MailMessage message) throws SQLException;
    StoredMailAccount[] loadMailAccounts()throws SQLException;
    String getStringIdentOfGroup(StoredMailGroup mailGroup);
    StoredMailGroup loadGroupByStringIdent(String ident) throws SQLException;
}
