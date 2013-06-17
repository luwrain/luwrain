/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

import javax.mail.*;
import javax.mail.internet.*;
import org.luwrain.pim.MailMessage;
import java.io.InputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;

public class MailUtils
{
    public static MailMessage constructMailMessage(MimeMessage msg) throws MessagingException
    {
	System.out.println(msg.getSubject());
	System.out.println(msg.getContentType());
	System.out.println(msg.getEncoding());
	try {
	    System.out.println(decodeContent(msg.getInputStream(), "koi8-r"));
	}
	catch (IOException e)
	{
	    //FIXME:
	    e.printStackTrace();
	}
	return null;
    }

    public static String decodeContent(InputStream inputStream, String charset) throws IOException
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
}
