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

import java.util.*;

import javax.mail.*;
import javax.mail.internet.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.pim.*;

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
	    Luwrain.onAreaNewContent(this);
	    Luwrain.onAreaNewHotPoint(this);
	    Luwrain.onAreaNewName(this);
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
		e.printStackTrace();//FIXME:
		continue;
	    }
	    catch(MessagingException e)
	    {
		//FIXME:log warning and errors flags;
		e.printStackTrace();
		continue;
	    }
	    v.add(item);
	}
	items = new SummaryItem[v.size()];
	Iterator<SummaryItem> it = v.iterator();
	int k = 0;
	while(it.hasNext())
	    items[k++] = it.next();
	hotPointX = 0;//FIXME:
	hotPointY = 0;
	Luwrain.onAreaNewContent(this);
	Luwrain.onAreaNewHotPoint(this);
	Luwrain.onAreaNewName(this);
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

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	//Tab;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoMessage();
	    return true;
	}
	if (event.withAlt() || event.withShift())
	    return false;
	if (items == null || items.length < 1)
	{
	    Speech.say(stringConstructor.emptySummaryArea(), Speech.PITCH_HIGH);
	    return true;
	}
	final int cmd = event.getCommand();

	//Arrow down;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ARROW_DOWN && !event.isModified())
	{
	    if (hotPointY >= items.length)
	    {
		Speech.say(stringConstructor.lastSummaryLine(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY++;
	    if (hotPointY < items.length)
	    {
		hotPointX = 2;
		introduceItem(hotPointY);
	    } else
	    {
		hotPointX = 0;
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    }
	    Luwrain.onAreaNewHotPoint(this);
	    return true;
	}

	//Arrow up;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ARROW_UP && !event.isModified())
	{
	    if (hotPointY < 1)
	    {
		Speech.say(stringConstructor.firstSummaryLine(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY--;
	    if (hotPointY == items.length)
		hotPointX = 0; else
		hotPointX = 2;
	    introduceItem(hotPointY);
	    Luwrain.onAreaNewHotPoint(this);
	    return true;
	}

	//FIXME:
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeMailReader();
	    return true;
	}
	return false;
    }

    public String getName()
    {
	return stringConstructor.summaryAreaName();
    }
}
