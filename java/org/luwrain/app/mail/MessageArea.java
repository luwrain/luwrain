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

import javax.mail.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class MessageArea extends SimpleArea
{
    private StringConstructor stringConstructor;
    private Actions actions;

    public MessageArea(Actions actions, StringConstructor stringConstructor)
    {
	super(stringConstructor.messageAreaName());
	this.actions =  actions;
	this.stringConstructor = stringConstructor;
    }

    public void show(Message message)
    {
	/*

	try {
	    File f = new File("/tmp/message");
	    InputStream s = new FileInputStream(f);
	    Properties p = new Properties();
	    Session session = Session.getDefaultInstance(p);
	    MimeMessage m = new MimeMessage(session, s);
	    System.out.println(m.getSubject());
	    System.out.println(m.getContentType());
	    //	    m.parse(s);

	}	    catch(Exception e)
	{
	    e.printStackTrace();
	}
	*/
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (super.onKeyboardEvent(event))
	    return true;

	//Tab;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoFolders();
	    return true;
	}
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.close();
	    return true;
	}
	return false;
    }
}
