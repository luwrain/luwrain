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

package org.luwrain.app.fetch;

public interface StringConstructor
{
    String appName();
    String noNewsGroupsData();
    String fetchingCompleted();
    String newsGroupsError();
    String noNewsGroups();
    String newsFetchingError(String groupName);
    String newsGroupFetched(String name, int fresh, int total);
    String pressEnterToStart();
    String processAlreadyRunning();
    String processNotFinished();
    String readingMailFromAccount(String accountName);
    String fetchedMailMessages(int count);
    String mailErrorWithAccount(String accountName);
    String noMailAccounts();
    String mailAccountsProblem();
    String noMailStoring();
}
