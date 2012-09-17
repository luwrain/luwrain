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

package com.marigostra.luwrain.app.mail;

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import com.marigostra.luwrain.core.*;
import com.marigostra.luwrain.core.events.*;
import com.marigostra.luwrain.pim.*;

class SummaryItem
{
    public String from;
    public String subject;
    public MimeMessage message;
}

public class SummaryArea implements Area
{
    private MailReaderStringConstructor stringConstructor;
    private MailReaderActions actions;
    private SummaryItem items[];
    private int hotPointX = 0;
    private int hotPointY = 0;

    public SummaryArea(MailReaderActions actions, MailReaderStringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
	this.actions = actions;
    }

    public void show(MailGroup group)
    {
	Message messages[] = group.getMessages();
	if (messages == null || messages.length < 1)
	{
	    items = null;
	    hotPointX = 0;
	    hotPointY = 0;
	    Dispatcher.onAreaNewContent(this);
	    Dispatcher.onAreaNewHotPoint(this);
	    Dispatcher.onNewAreaName(this);
	    return;
	}
	Vector<SummaryItem> v = new Vector<SummaryItem>();
	for(int i = 0;i < messages.length;i++)
	{
	    SummaryItem item = new SummaryItem();
	    try {
		if (messages[i] == null)
		    continue;
		MimeMessage m = (MimeMessage)messages[i];
		item.message = m;
		if (m.getFrom() == null || m.getFrom().length < 1 || m.getFrom()[0] == null)
		    item.from = new String("#FIXME:NO VALUE"); else
		{
		    InternetAddress a = (InternetAddress)m.getFrom()[0];
		    if (a.getPersonal() == null || a.getPersonal().isEmpty())
		    {
			if (a.getAddress() == null || a.getAddress().isEmpty())
			    item.from = "#NO FROM"; else//FIXME:stringConstructor;
			    item.from = a.getAddress();
		    } else
			item.from = a.getPersonal();
		}
		item.subject = m.getSubject() == null || m.getSubject().isEmpty()?new String("#FIXME:NO SUBJECT"):m.getSubject();//FIXME:stringConstant;
	    }
	    catch(ClassCastException e)
	    {
		//FIXME:Log warning;
		continue;
	    }
	    catch(MessagingException e)
	    {
		//FIXME:log warning and errors flags;
		continue;
	    }
	    v.add(item);
	}
	items = new SummaryItem[v.size()];
	Iterator<SummaryItem> it = v.iterator();
	int k = 0;
	while(it.hasNext())
	    items[k++] = it.next();
	//FIXME:empty item;
	hotPointX = 0;//FIXME:
	hotPointY = 0;
	Dispatcher.onAreaNewContent(this);
	Dispatcher.onAreaNewHotPoint(this);
	Dispatcher.onNewAreaName(this);
    }

    private void introduceItem(int index)
    {
	if (items == null || items.length < 1)
	    return;
	SummaryItem item = items[index];
	if (item == null)
	    return;
	Speech.say(item.from + " " + item.subject);
    }

    public int getLineCount()
    {
	if (items == null || items.length < 1)
	    return 1;
	return items.length;
    }

    public String getLine(int index)
    {
	return new String();//FIXME:
    }

    public int getHotPointX()
    {
	if (hotPointX < 0)//Actually never happens;
	    return 0;
	return hotPointX;
    }

    public int getHotPointY()
    {
	if (hotPointY < 0)//Actually never happens;
	    return 0;
	return hotPointY;
    }

    public void onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoMessage();
	    return;
	}
	if (items == null || items.length < 1)
	{
	    Speech.say("no content", Speech.PITCH_HIGH);
	    return;
	}

	//Arrow down;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ARROW_DOWN && !event.isModified())
	{
	    if (hotPointY + 1>= items.length)
	    {
		Speech.say("NO MORE", Speech.PITCH_HIGH);
		return;
	    }
	    hotPointX = 2;
	    hotPointY++;
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceItem(hotPointY);
	    return;
	}

	//Arrow up;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ARROW_UP && !event.isModified())
	{
	    if (hotPointY < 1)
	    {
		Speech.say("NO ABOVE", Speech.PITCH_HIGH);//FIXME:
		return;
	    }
	    hotPointX = 2;
	    hotPointY--;
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceItem(hotPointY);
	    return;
	}

	//FIXME:
    }

    public void onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeMailReader();
	    return;
	}
    }

    public String getName()
    {
	return stringConstructor.summaryAreaName();
    }
}
