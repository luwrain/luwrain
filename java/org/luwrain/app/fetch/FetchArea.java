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

package org.luwrain.app.fetch;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.pim.StoredMailAccount;
import org.luwrain.pim.PimManager;
import org.luwrain.pim.MailStoring;
import javax.mail.*;
import java.util.*;

public class FetchArea extends SimpleArea
{
    private FetchActions actions;
    private FetchStringConstructor stringConstructor;

    public FetchArea(FetchActions actions, FetchStringConstructor stringConstructor)
    {
	super("Fetch area");//FIXME:
	this.actions = actions;
	this.stringConstructor = stringConstructor;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ENTER && !event.isModified())
	{
	    launchFetching();
	    return true;
	}
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ESCAPE && !event.isModified())
	{
	    actions.closeFetchApp();
	    return true;
	}
	return super.onKeyboardEvent(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeFetchApp();
	    return true;
	}
	return false;
    }

    private void launchFetching()
    {
	//FIXME:
	MailStoring mailStoring = PimManager.createMailStoring();
	if (mailStoring == null)
	{
	    addLine("FIXME:No database connection");
	    return;
	}
	final SimpleArea a = this;
	Properties p = new Properties();
	Session session = Session.getInstance(p, null);
	MailFetching mailFetching = new MailFetching(session, new FetchProgressListener(){
		SimpleArea thisArea = a;
		public void onProgressLine(String line)
		{
		    thisArea.addLine(line);
		    Speech.say(line);
		}
	    }, stringConstructor, false, mailStoring);
	try {
	    StoredMailAccount accounts[] = mailStoring.loadMailAccounts();
	    for(int i = 0;i < accounts.length;i++)
	    {
		if (!accounts[i].getProtocol().equals("pop3"))
		    continue;
		addLine(stringConstructor.readingMailFromAccount(accounts[i].getName()));
		mailFetching.fetchPop3(new URLName("pop3", accounts[i].getHost(), accounts[i].getPort(), accounts[i].getFile(), accounts[i].getLogin(), accounts[i].getPasswd()));
	    }
	}
	catch(Exception e)
	{
	    addLine("Error: " + e.getMessage());
	    e.printStackTrace();
	}
    }
}
