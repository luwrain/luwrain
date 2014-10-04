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
import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.popups.FilePopup;

public class MessageArea extends FormArea
{
    private static final String TO_NAME = "to";
    private static final String CC_NAME = "cc";
    private static final String SUBJECT_NAME = "subject";
    private static final String ATTACHMENT = "attachment";

    class Attachment
    {
	public String name;
	public File file;

	public Attachment(String name, File file)
	{
	    this.name = name;
	    this.file = file;
	}
    }

    private Object instance;
    private ControlEnvironment environment;
    private Vector<Attachment> attachments = new Vector<Attachment>();
    private int attachmentCounter = 0;

    public MessageArea(Object instance, ControlEnvironment environment)
    {
	super(environment);
	this.instance = instance;
	this.environment = environment;
	addEdit(TO_NAME, environment.langStaticString(Langs.MESSAGE_TO), "", true);
	addEdit(CC_NAME, environment.langStaticString(Langs.MESSAGE_CC), "", true);
	addEdit(SUBJECT_NAME, environment.langStaticString(Langs.MESSAGE_SUBJECT), "", true);
	activateMultilinedEdit(environment.langStaticString(Langs.MESSAGE_TEXT));
    }

    public String getTo()
    {
	final String value = getEnteredText(TO_NAME);
	return value != null?value:"";
    }

    public String getCC()
    {
	final String value = getEnteredText(CC_NAME);
	return value != null?value:"";
    }

    public String getSubject()
    {
	final String value = getEnteredText(SUBJECT_NAME);
	return value != null?value:"";
    }

    public String getText()
    {
	final String value = getMultilinedEditText();
	return value != null?value:"";
    }

    public File[] getAttachments()
    {
	File[] res = new File[attachments.size()];
	for(int i = 0;i < attachments.size();++i)
	    res[i] = attachments.get(i).file;
	return res;
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
	if (event.isCommand() &&
	    !event.isModified() &&
	    event.getCommand() == KeyboardEvent.DELETE &&
	    removeAttachment())
	    return true;
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
	Attachment a = new Attachment(ATTACHMENT + attachmentCounter, popup.getFile());
	++attachmentCounter;
	attachments.add(a);
	addStatic("attachment" + attachmentCounter, environment.langStaticString(Langs.MESSAGE_ATTACHMENT) + " " + popup.getFile().getName() + " (" + popup.getFile().getAbsolutePath() + ")");
    }

    private boolean removeAttachment()
    {
	return false;
    }
}
