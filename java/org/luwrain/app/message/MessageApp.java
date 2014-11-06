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
    private Luwrain luwrain;
    private StringConstructor stringConstructor;
    private Base base = new Base();
    private MessageArea messageArea;

    public MessageApp()
    {
    }

    public boolean onLaunch(Luwrain luwrain)
    {
	Object o = Langs.requestStringConstructor("message");
	if (o == null)
	    return false;
	stringConstructor = (StringConstructor)o;
	this.luwrain = luwrain;
	base.init(luwrain, stringConstructor);
	if (!base.isValid())
	{
	    luwrain.message(stringConstructor.noMailStoring());
	    return false;
	}
	createMessageArea();
	return true;
    }

    public void sendMessage()
    {
	if (messageArea.getTo().trim().isEmpty())
	{
	    luwrain.message(stringConstructor.emptyRecipient());
			    return;
	}
	    if (base.send(messageArea.getTo(), messageArea.getCC(),
				 messageArea.getSubject(), "", new String[0])) //FIXME:
		close(); else
		luwrain.message(stringConstructor.errorSendingMessage());
    }


    private void createMessageArea()
    {
	final Actions a = this;
	messageArea = new MessageArea(luwrain, new DefaultControlEnvironment(luwrain),
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
	luwrain.closeApp();
    }

    private String[] getInitialText()
    {
	MailRegistryValues mailValues = new MailRegistryValues(luwrain.getRegistry());
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
