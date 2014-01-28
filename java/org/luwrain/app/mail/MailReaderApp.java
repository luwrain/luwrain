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

package org.luwrain.app.mail;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.luwrain.pim.StoredMailGroup;
import org.luwrain.pim.MailStoring;
import org.luwrain.pim.PimManager;
import org.luwrain.core.*;

public class MailReaderApp implements Application, MailReaderActions
{
    private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";

    private Object instance = null;
    private MailReaderStringConstructor stringConstructor = null;
    private GroupArea groupArea;
    private SummaryArea summaryArea;
    private MessageArea messageArea;
    MailStoring mailStoring;

    private Session session;
    private MailGroup topLevelGroups[]; 
    private RootMailGroup rootGroup;

    private void initSession()
    {
	Properties p = new Properties();
	//	p.setProperty("mail.pop3.socketFactory.class", SSL_FACTORY);
	//	p.setProperty("mail.pop3.socketFactory.fallback", "false");
	//	p.setProperty("mail.pop3.port",  "995");
	//	p.setProperty("mail.pop3.socketFactory.port", "995");//FIXME:
	session = Session.getInstance(p, null);
    }

    private void fillTopLevelGroups()
    {
	rootGroup = new RootMailGroup(stringConstructor);
	Vector<MailGroup> groups = new Vector<MailGroup>();
	if (mailStoring != null)
	{
	    StoredMailGroup storedRoot;
	    StoredMailGroup[] children;
	    try {
		storedRoot = mailStoring.loadRootGroup();
		children = mailStoring.loadChildGroups(storedRoot);
	    }
	    catch(Exception e)
	    {
		storedRoot = null;
children = null;
		//FIXME:Log report;
		groups.add(new EmptyMailGroup(rootGroup, "(Ошибка доставки групп)"));
	    }
	    if (storedRoot != null && children != null)
		for(int i = 0;i < children.length;i++)
		    groups.add(new LocalGroup(rootGroup, mailStoring, children[i]));
	} else
	    groups.add(new EmptyMailGroup(rootGroup, "(Нет соединения с базой данных)"));//FIXME:
	//FIXME:Online groups;
	topLevelGroups = new MailGroup[groups.size()];
	Iterator<MailGroup> it = groups.iterator();
	int k = 0;
	while(it.hasNext())
	    topLevelGroups[k++] = it.next();
	rootGroup.setChildGroups(topLevelGroups);
    }

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("mail-reader");
	if (o == null)
	    return false;
	stringConstructor = (MailReaderStringConstructor)o;
	mailStoring = PimManager.createMailStoring();
	if (mailStoring == null)
	{
	    //FIXME:
	}
	initSession();
	fillTopLevelGroups();
	groupArea = new GroupArea(this, stringConstructor, new MailGroupTreeModel(rootGroup));
	summaryArea = new SummaryArea(this, stringConstructor);
	messageArea = new MessageArea(this, stringConstructor);
	this.instance = instance;
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, messageArea);
    }

    public void openGroup(MailGroup group)
    {
	summaryArea.show(group);
    }

    public void gotoGroups()
    {
	Dispatcher.setActiveArea(instance, groupArea);
    }

    public void gotoSummary()
    {
	Dispatcher.setActiveArea(instance, summaryArea);
    }

    public void gotoMessage()
    {
	Dispatcher.setActiveArea(instance, messageArea);
    }

    public void closeMailReader()
    {
	Dispatcher.closeApplication(instance);
    }
}
