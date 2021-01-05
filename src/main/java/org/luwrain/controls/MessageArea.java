/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.controls;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;

public class MessageArea extends FormArea
{
    static protected final String HOOKS_PREFIX = "luwrain.message.edit";

    static final String TO_NAME = "to",
	CC_NAME = "cc",
	SUBJECT_NAME = "subject",
	ATTACHMENT = "attachment";

    static public class Params
    {
	public ControlContext context = null;
	public String[] text = new String[0];
	public String to = "";
	public String cc = "";
	public String subject = "";
    }

    static public final class Attachment
    {
	final String name;
	final File file;
	Attachment(String name, File file)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(file, "file");
	    this.name = name;
	    this.file = file;
	}
	public String getName()
	{
	    return this.name;
	}
	public File getFile()
	{
	    return this.file;
	}
    }

    protected final MutableLinesImpl lines;
    protected int attachmentCounter = 0;

    public MessageArea(Params params)
    {
	super(params.context);
	NullCheck.notNull(params, "params");
	NullCheck.notNullItems(params.text, "params.text");
	      this.lines = new MutableLinesImpl(params.text);
	      addEdit(TO_NAME, context.getI18n().getStaticStr("MessageTo"), params.to);
	      addEdit(CC_NAME, context.getI18n().getStaticStr("MessageCc"), params.cc);
	      addEdit(SUBJECT_NAME, context.getI18n().getStaticStr("MessageSubject"), params.subject);
	      activateMultilineEdit(context.getI18n().getStaticStr("MessageEnterTextBelow"), lines, createEditParams(), true);
    }

    public String getTo()
    {
	return getEnteredText(TO_NAME);
    }

    public void setTo(String value)
    {
	NullCheck.notNull(value, "value");
	setEnteredText(value, "value");
    }

    public void focusTo()
    {
	setHotPoint(0, 0);
    }

    public String getCc()
    {
	return getEnteredText(CC_NAME);
    }

    public void setCc(String value)
    {
	NullCheck.notNull(value, "value");
	setEnteredText(CC_NAME, value);
    }

    public String getSubject()
    {
	return getEnteredText(SUBJECT_NAME);
    }

    public void focusSubject()
    {
	setHotPoint(0, 2);
    }

    public String getText()
    {
	return lines.getWholeText();
    }

    public Attachment[] getAttachments()
    {
	final List<Attachment> res = new LinkedList();
		for(int i = 0;i < getItemCount();++i)
	{
	    if (getItemTypeOnLine(i) != FormArea.Type.STATIC)
		continue;
	    final Object o = getItemObj(i);
	    if (o == null || !(o instanceof Attachment))
		continue;
	    res.add((Attachment)o);
	}
		return res.toArray(new Attachment[res.size()]);
    }

    public File[] getAttachmentFiles()
    {
	final Attachment[] attachments = getAttachments();
	final File[] res = new File[attachments.length];
	for(int i = 0;i < attachments.length;++i)
	    res[i] = attachments[i].file;
	return res;
    }

    public void addAttachment(File file)
    {
	NullCheck.notNull(file, "file");
	for(Attachment a: getAttachments())
	    if (a.file.equals(file))
	    {
		context.message("Файл " + file.getName() + " уже прикреплён к сообщению", Luwrain.MessageType.ERROR);//FIXME:
		return;
	    }
	final Attachment a = new Attachment(ATTACHMENT + attachmentCounter, file);
	++attachmentCounter;
	addStatic(a.name, context.getI18n().getStaticStr("MessageAttachment") + " " + a.file.getName(), a);
    }

    public void removeAttachment(int lineIndex)
    {
	removeItemOnLine(lineIndex);
    }

    protected MultilineEdit.Params createEditParams()
    {
	final MultilineEdit.Params params = createMultilineEditParams(context, lines);
	final MultilineEditCorrector corrector = (MultilineEditCorrector)params.model;
	params.model = new DirectScriptMultilineEditCorrector(context, corrector, HOOKS_PREFIX);
	return params;
    }
}
