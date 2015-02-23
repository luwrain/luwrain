/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

    private Luwrain luwrain;
    private ControlEnvironment environment;
    private Vector<Attachment> attachments = new Vector<Attachment>();
    private int attachmentCounter = 0;

    public MessageArea(Luwrain luwrain, ControlEnvironment environment)
    {
	super(environment);
	this.luwrain = luwrain;
	this.environment = environment;
	addEdit(TO_NAME, environment.staticStr(Langs.MESSAGE_TO), "", null, true);
	addEdit(CC_NAME, environment.staticStr(Langs.MESSAGE_CC), "", null, true);
	addEdit(SUBJECT_NAME, environment.staticStr(Langs.MESSAGE_SUBJECT), "", null, true);
	activateMultilinedEdit(environment.staticStr(Langs.MESSAGE_TEXT), new String[0], true);
    }

    public MessageArea(Luwrain luwrain,
		       ControlEnvironment environment,
		       String initialTo,
		       String initialCC,
		       String initialSubject,
		       String[] initialText,
		       File initialAttachments[])
    {
	super(environment);
	this.luwrain = luwrain;
	this.environment = environment;
	addEdit(TO_NAME, environment.staticStr(Langs.MESSAGE_TO), initialTo != null?initialTo:"", null, true);
	addEdit(CC_NAME, environment.staticStr(Langs.MESSAGE_CC), initialCC != null?initialCC:"", null, true);
	addEdit(SUBJECT_NAME, environment.staticStr(Langs.MESSAGE_SUBJECT), initialSubject != null?initialSubject:"", null, true);
	activateMultilinedEdit(environment.staticStr(Langs.MESSAGE_TEXT), initialText != null?initialText:new String[0], true);
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
	return environment.staticStr(Langs.MESSAGE);
    }

    private void insertAttachment()
    {
	FilePopup popup = new FilePopup(luwrain, 
					environment.staticStr(Langs.MESSAGE_ATTACHMENT_POPUP_TITLE),
					environment.staticStr(Langs.MESSAGE_ATTACHMENT_POPUP_PREFIX),
					new File("/"));//FIXME:
	environment.popup(popup);
	if (popup.closing.cancelled())
	    return;
	Attachment a = new Attachment(ATTACHMENT + attachmentCounter, popup.getFile());
	++attachmentCounter;
	attachments.add(a);
	addStatic(a.name, environment.staticStr(Langs.MESSAGE_ATTACHMENT) + " " + popup.getFile().getName() + " (" + popup.getFile().getAbsolutePath() + ")", a);
    }

    private boolean removeAttachment()
    {
	final int index = getHotPointY();
	if (getItemTypeOnLine(index) != STATIC)
	    return false;
	final Object obj = getItemObjOnLine(index);
	if (obj == null || !(obj instanceof Attachment))
	    return false;
	final Attachment a = (Attachment)obj;
	removeItemOnLine(index);
	int k;
	for(k = 0;k < attachments.size();++k)
	    if (attachments.get(k).name.equals(a.name))
		break;
	if (k >= attachments.size())//Should never happen;
	    return false;
	attachments.remove(k);
	return true;
    }
}
