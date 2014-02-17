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

package org.luwrain.langs.en;

public class FetchStringConstructor implements org.luwrain.app.fetch.StringConstructor
{
    public String appName()
    {
	return "Mail and news fetching";
    }

    public String noNewsGroupsData()
    {
	return "No information about news groups!";
    }

    public String fetchingCompleted()
    {
	return "Fetching is completed!";
    }

    public String newsGroupsError()
    {
	return "An error occurred while fetching the news groups information, skipping news fetching";
    }

    public String noNewsGroups()
    {
	return "No news groups, skipping news fetching";
    }

    public String newsFetchingError(String groupName)
    {
	return "An error occurred while fetching the news of group \"" + groupName + "\", skipping";
    }

    public String newsGroupFetched(String name, int fresh, int total)
    {
	return "The group " + name + " contains " + fresh + " new articles of " + total;
    }

    public String pressEnterToStart()
{
    return "Press ENTER to launch the news and mail fetching";
	}

    public String processAlreadyRunning()
    {
	return "Fetching is already launched";
    }

    public String processNotFinished()
    {
	return "Fetching is not yet completed";
    }

    public String readingMailFromAccount(String accountName)
    {
	return "Reading mail from account \"" + accountName + "\"";
    }

    public String mailErrorWithAccount(String accountName)
    {
	return "Error fetching mail from account \"" + accountName + "\", skipping...";
    }

    public String connecting(String host)
    {
	return "Connecting to host " + host;
    }

    public String fetchedMailMessages(int count)
    {
	return "Fetched messages: " + count;
    }

    public String noMailAccounts()
    {
	return "No mail accounts, skipping mail fetching";
    }

    public String noMailStoring()
    {
	return "No mail storing connection, skipping mail fetching";
    }

    public String mailAccountsProblem()
    {
	return "Error getting the list of mail accounts, skipping mail fetching";
    }

    /*
    public String readingMailInFolder(String folder)
    {
	return "Opening folder \"" + folder + "\"";
    }

    public String readingMessage(int msgNum, int totalCount)
    {
	return "Reading message " + msgNum + "/" + totalCount;
    }

    public String noMail()
    {
	return "No new mail";
    }
*/
}
