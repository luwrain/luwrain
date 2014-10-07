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

package org.luwrain.app.message;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.*;

public class MessageApp implements Application, Actions
{
    private StringConstructor stringConstructor;
    private Object instance;
    private Base base = new Base();
    private MessageArea messageArea;

    public MessageApp()
    {
    }

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("message");
	if (o == null)
	    return false;
	stringConstructor = (StringConstructor)o;
	base.init(stringConstructor);
	if (!base.isValid())
	{
	    Luwrain.message(stringConstructor.noMailStoring());
	    return false;
	}
	this.instance = instance;
	createMessageArea();
	return true;
    }

    public void sendMessage()
    {
	if (messageArea.getTo().trim().isEmpty())
	{
	    Luwrain.message(stringConstructor.emptyRecipient());
			    return;
	}
	    if (base.send(messageArea.getTo(), messageArea.getCC(),
				 messageArea.getSubject(), "", new String[0])) //FIXME:
		close(); else
		Luwrain.message(stringConstructor.errorSendingMessage());
    }


    private void createMessageArea()
    {
	final Actions a = this;
	messageArea = new MessageArea(instance, new DefaultControlEnvironment(),
				      "", "", "",
				      getInitialText(), null){
		final Actions actions = a;
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch (event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.close();
			return true;
		    case EnvironmentEvent.OK:
			actions.sendMessage();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
	    };
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(messageArea);
    }

    public void close()
    {
	Luwrain.closeApp(instance);
    }

    private String[] getInitialText()
    {
	MailRegistryValues mailValues = new MailRegistryValues(Luwrain.getRegistry());
	final String signature = mailValues.getSignature();
	if (signature == null)
	    return new String[0];
	String[] res = new String[4];
	res[0] = "";
	res[1] = "--";
	res[2] = signature;
	res[3] = "";
	return res;
    }
}
