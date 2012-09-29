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

package org.luwrain.app.mail;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

public class LocalDirectory implements MailGroup
{
    private Session session;
    private File file;
    private Message messages[];

    public LocalDirectory(Session session, File file)
    {
	this.session = session;
	this.file = file;
    }

    public boolean update()
    {
	if (file ==null )
	{
	    messages = null;
	    return false;
	}
	File files[] = file.listFiles();
	Vector<Message> m = new Vector <Message>();
	boolean wereErrors = false;
	for(int i = 0;i < files.length;i++)
	{
	    if (files[i].isDirectory())
		continue;
	    try {
		InputStream s = new FileInputStream(files[i]);
		MimeMessage message = new MimeMessage(session, s);
		m.add(message);
	    }
	    catch (FileNotFoundException e)
	    {
		wereErrors = true;
		continue;
	    }
	    catch(MessagingException e)
	    {
		wereErrors = true;
		continue;
	    }
	}
	messages = new Message[m.size()];
	Iterator<Message> it = m.iterator();
	int k = 0;
	while(it.hasNext())
	    messages[k++] = it.next();
	return !wereErrors;
    }

    public String getName()
    {
	if (file == null)
	    return new String();
	return file.getAbsolutePath();
    }

    public boolean hasChildFolders()
    {
	return false;
    }

    public MailGroup[] getChildGroups()
    {
	return new MailGroup[0];
    }

    public Message[] getMessages()
    {
	if (messages == null)
	    return new Message[0];
	return messages;
    }

    public MailGroup getParentGroup()
    {
	return null;
    }
}
