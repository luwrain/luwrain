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

package org.luwrain.controls;

import java.io.File;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.popups.FilePopup;

public class MessageArea extends FormArea
{
    private Object instance;
    private ControlEnvironment environment;
    private int attachmentCounter = 0;

    public MessageArea(Object instance, ControlEnvironment environment)
    {
	super(environment);
	this.instance = instance;
	this.environment = environment;
	addEdit("to", environment.langStaticString(Langs.MESSAGE_TO), "", true);
	addEdit("cc", environment.langStaticString(Langs.MESSAGE_CC), "", true);
	addEdit("subject", environment.langStaticString(Langs.MESSAGE_SUBJECT), "", true);
    }

    public String getTo()
    {
	final String value = getEnteredText("to");
	return value != null?value:"";
    }

    public String getCC()
    {
	final String value = getEnteredText("cc");
	return value != null?value:"";
    }

    public String getSubject()
    {
	final String value = getEnteredText("subject");
	return value != null?value:"";
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() &&
	    !event.isModified() &&
	    event.getCommand() == KeyboardEvent.INSERT)
	{
	    insertAttachment();
	    return true;
	}
	return super.onKeyboardEvent(event);
    }

    @Override public String getName()
    {
	return environment.langStaticString(Langs.MESSAGE);
    }

    private void insertAttachment()
    {
	FilePopup popup = new FilePopup(instance, 
					environment.langStaticString(Langs.MESSAGE_ATTACHMENT_POPUP_TITLE),
					environment.langStaticString(Langs.MESSAGE_ATTACHMENT_POPUP_PREFIX),
					new File("/"));//FIXME:
	environment.popup(popup);
	if (popup.closing.cancelled())
	    return;
	addStatic("attachment" + attachmentCounter, environment.langStaticString(Langs.MESSAGE_ATTACHMENT) + " " + popup.getFile().getName() + " (" + popup.getFile().getAbsolutePath() + ")");
	++attachmentCounter;
    }
}
