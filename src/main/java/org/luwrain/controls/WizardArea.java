/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class WizardArea extends FormArea
{
public interface WizardValues
{
    String getText(int inputIndex);
}

    public interface WizardClickHandler
    {
	boolean handle(WizardValues values);
    }

public interface WizardItem
{
}

    public final class WizardText implements WizardItem
    {
	private final String text;
	public WizardText(String text)
	{
	    NullCheck.notEmpty(text, "text");
	    this.text = text;
	}
	public String getText()
	{
	    return text;
	}
    }

        public final class WizardClickable implements WizardItem
    {
	private final String text;
	private final WizardClickHandler handler;
	public WizardClickable(String text, WizardClickHandler handler)
	{
	    NullCheck.notEmpty(text, "text");
	    NullCheck.notNull(handler, "handler");
	    this.text = text;
	    this.handler = handler;
	}
	public String getText()
	{
	    return text;
	}
	public boolean  click(WizardValues values)
	{
	    NullCheck.notNull(values, "values");
	    return handler.handle(values);
	}
    }

            public final class WizardInput implements WizardItem
    {
	private final String prefix;
	private final String text;
	public WizardInput(String prefix, String text)
	{
	    NullCheck.notNull(prefix, "prefix");
	    NullCheck.notNull(text, "text");
	    this.prefix = prefix;
	    this.text = text;
	}
	public String getPrefix()
	{
	    return this.prefix;
	}
	public String getText()
	{
	    return this.text;
	}
    }

                public final class WizardPasswd implements WizardItem
    {
	private final String prefix;
	private final String text;
	public WizardPasswd(String prefix, String text)
	{
	    NullCheck.notNull(prefix, "prefix");
	    NullCheck.notNull(text, "text");
	    this.prefix = prefix;
	    this.text = text;
	}
	public String getPrefix()
	{
	    return this.prefix;
	}
	public String getText()
	{
	    return this.text;
	}
    }

    public final class Frame
    {
	private final List<WizardItem> items = new ArrayList<>();
	public Frame addText(String text)
	{
	    NullCheck.notEmpty(text, "text");
	    items.add(new WizardText(text));
	    return this;
	}
	public Frame addClickable(String text, WizardClickHandler handler)
	{
	    NullCheck.notEmpty(text, "text");
	    NullCheck.notNull(handler, "handler");
	    items.add(new WizardClickable(text, handler));
	    return this;
	}
	public Frame addInput(String prefix, String text)
	{
	    NullCheck.notNull(prefix, "prefix");
	    NullCheck.notNull(text, "text");
	    items.add(new WizardInput(prefix, text));
	    return this;
	}
	public Frame addINput(String prefix)
	{
	    NullCheck.notNull(prefix, "prefix");
	    return addInput(prefix, "");
	}
		public Frame addPasswd(String prefix, String text)
	{
	    NullCheck.notNull(prefix, "prefix");
	    NullCheck.notNull(text, "text");
	    items.add(new WizardPasswd(prefix, text));
	    return this;
	}
	public Frame addPasswd(String prefix)
	{
	    NullCheck.notNull(prefix, "prefix");
	    return addPasswd(prefix, "");
	}

	WizardItem[] getItems()
	{
	    return items.toArray(new WizardItem[items.size()]);
	}
    }

    protected final Values values = new Values();
    protected WizardClickable defaultClickable = null;
    protected int clickableCount = 0;

    public WizardArea(ControlContext context)
    {
	super(context);
    }

    public Frame newFrame()
    {
	return new Frame();
    }

    public void show(Frame frame)
    {
	NullCheck.notNull(frame, "frame");
	clear();
	values.edits.clear();
	defaultClickable = null;
	clickableCount = 0;
	if (frame.getItems().length == 0)
	    return;
	boolean emptyLine = true;
	for(WizardItem i: frame.getItems())
	{
	    if (i instanceof WizardText)
	    {
			    	addStatic("");
		final WizardText t = (WizardText)i;
		for(String l: TextUtils.wordWrap(t.getText(), 80))//FIXME: proper width
		addStatic(l);
		emptyLine = true;
		continue;
	    }

	    	    if (i instanceof WizardClickable)
	    {
			    if (emptyLine)
	    	addStatic("");
		final WizardClickable c = (WizardClickable)i;
		clickableCount++;
		    defaultClickable = c;
		addStatic(getItemNewAutoName(), c.getText(), c);
		emptyLine = false;
		continue;
	    }

		    	    	    if (i instanceof WizardInput)
	    {
			    if (emptyLine)
	    	addStatic("");
		final WizardInput c = (WizardInput)i;
		final String name = getItemNewAutoName();
		this.values.addEdit(name);
		addEdit(name, c.getPrefix(), c.getText(), c, true);
		emptyLine = false;
		continue;
	    }

				    		    	    	    if (i instanceof WizardPasswd)
	    {
			    if (emptyLine)
	    	addStatic("");
		final WizardPasswd c = (WizardPasswd)i;
		final String name = getItemNewAutoName();
		this.values.addEdit(name);
		addPasswd(name, c.getPrefix(), c.getText(), c, true);
		emptyLine = false;
		continue;
	    }
	}
    }

    protected boolean defaultClick()
    {
	if (clickableCount != 1 || defaultClickable == null)
	    return false;
	    return defaultClickable.click(values);
    }

    protected boolean onClick()
    {
	final String itemName = getItemNameOnLine(getHotPointY());
	if (itemName == null || itemName.isEmpty())
	    return defaultClick();
	final Object obj = getItemObjByName(itemName);
	if (obj == null)
	    return defaultClick();
	if (obj instanceof WizardClickable)
	{
	    final WizardClickable c = (WizardClickable)obj;
	    return c.click(values);
	}
	return defaultClick();
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		if (onClick())
		    return true;
		return super.onInputEvent(event);
	    }
	return super.onInputEvent(event);
    }

    @Override public void announceLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (line.trim().isEmpty())
	{
	    	super.announceLine(index, line);
		return;
	}
		final String name = getItemNameOnLine(index);
	if (name == null || name.isEmpty())
	{
	    	super.announceLine(index, line);
		return;
	}
	final Object obj = getItemObjByName(name);
	if (obj == null || !(obj instanceof WizardClickable))
	{
	    	    	super.announceLine(index, line);
		return;
	}
	context.setEventResponse(DefaultEventResponse.text(Sounds.MARKED_LIST_ITEM, context.getSpeakableText(line, Luwrain.SpeakableTextType.NATURAL)));
    }

    protected final class Values implements WizardValues
    {
	final List<String> edits = new ArrayList<>();
	public void addEdit(String name)
	{
	    NullCheck.notEmpty(name, "name");
	    this.edits.add(name);
	}
	@Override public String getText(int index)
	{
	    if (index < 0 || index >= this.edits.size())
		throw new IllegalArgumentException("Illegal index (" + String.valueOf(index) + "), the edit list contains " + String.valueOf(this.edits.size()) + " items");
	    return getEnteredText(this.edits.get(index));
	}
    }
}
