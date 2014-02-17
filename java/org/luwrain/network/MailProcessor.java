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

package org.luwrain.network;

import java.io.*;
import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;
import org.luwrain.pim.MailMessage;

public class MailProcessor
{
    private Session session;

    public MailProcessor()
    {
    }

    public MailProcessor(Session session)
    {
	this.session = session;
    }

    public boolean initSession()
    { 
	Properties properties = System.getProperties();
	properties.setProperty("mail.smtp.host", "localhost.localdomain");
	session = Session.getDefaultInstance(properties);
	return session != null;
    }

    public MailMessage readMessageFromFile(String fileName) throws Exception
    {
	if (session == null || fileName == null || fileName.isEmpty())
	    return null;
	    FileInputStream fis = new FileInputStream(fileName);
	    MimeMessage msg = new MimeMessage(session, fis);
	    return msg != null?constructMailMessage(msg):null;
    }

    public MailMessage constructMailMessage(MimeMessage msg) throws Exception
    {
	MailMessage newMsg = new MailMessage();
	final String contentType = msg.getContentType();
	final Object content = msg.getContent();
	newMsg.subject = msg.getSubject() != null?msg.getSubject():"";
	newMsg.contentText = "";
	if (contentType == null ||content == null)
	    return newMsg;
	if (contentType.indexOf("Multipart") == 0 || contentType.indexOf("multipart") == 0)
	{
	    Multipart multipart = (MimeMultipart)content;
	    for(int i = 0;i < multipart.getCount();++i)
		newMsg.contentText += getTextOfBodyPart((MimeBodyPart)multipart.getBodyPart(i));
	} else
	    if (contentType.indexOf("Text/plaint") == 0 || contentType.indexOf("text/plain") == 0)
		newMsg.contentText = (String)msg.getContent();
	return newMsg;

	/*Headers;
	Enumeration headers = mm.getAllHeaderLines();
	while(headers.hasMoreElements())
	    s.add(headers.nextElement());
*/



	/*Raw content
	InputStream in = mm.getRawInputStream();
	byte[] buf = new byte[256];
	int count;
	do {
	    count = in.read(buf);
	} while(count > 0);
	*/
    }

    private String getTextOfBodyPart(MimeBodyPart part) throws Exception
    {
	if (part == null)
	    return "";
	final String contentType = part.getContentType();
	if (contentType.indexOf("text/plain") == 0 || contentType.indexOf("Text/plain") == 0)
	    return (String)part.getContent();
	return "";
    }

    /*
    private String decodeContent(InputStream inputStream, String charset) throws Exception
    {
	InputStreamReader reader = new InputStreamReader(inputStream, charset);
	BufferedReader buffered = new BufferedReader(reader);
	StringBuffer str = new StringBuffer();
	int result = buffered.read();
	while(result != -1)
	{
	    str.append((char)result);
	    result = buffered.read();
	}
	return str.toString();
    }                                                            
    */
}
