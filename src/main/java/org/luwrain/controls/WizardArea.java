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

//LWR_API 1.0

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class WizardArea extends FormArea
{
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
	private final Runnable handler;
	public WizardClickable(String text, Runnable handler)
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
	public void click()
	{
	    handler.run();
	}
    }

    public final class WizardFrame
    {
	private final List<WizardItem> items = new ArrayList();
	public WizardFrame addText(String text)
	{
	    NullCheck.notEmpty(text, "text");
	    items.add(new WizardText(text));
	    return this;
	}
	public WizardFrame addClickable(String text, Runnable handler)
	{
	    NullCheck.notEmpty(text, "text");
	    NullCheck.notNull(handler, "handler");
	    items.add(new WizardClickable(text, handler));
	    return this;
	}
	WizardItem[] getItems()
	{
	    return items.toArray(new WizardItem[items.size()]);
	}
    }

    protected final List<WizardFrame> frames = new ArrayList();

    public WizardArea(ControlContext context)
    {
	super(context);
    }

    public WizardFrame addFrame()
    {
	final WizardFrame frame = new WizardFrame();
	this.frames.add(frame);
	return frame;
    }

    public void start()
    {
	if (this.frames.isEmpty())
	    throw new IllegalStateException("No frames to start the wizard");
	fillForm(frames.get(0));
    }

    void fillForm(WizardFrame frame)
    {
	NullCheck.notNull(frame, "frame");
	clear();
	if (frame.getItems().length == 0)
	    return;
	for(WizardItem i: frame.getItems())
	{
	    	addStatic("");
	    if (i instanceof WizardText)
	    {
		final WizardText t = (WizardText)i;
		for(String l: TextUtils.wordWrap(t.getText(), 80))//FIXME: proper width
		addStatic(l);
		continue;
	    }
	    	    if (i instanceof WizardClickable)
	    {
		final WizardClickable c = (WizardClickable)i;
		addStatic(getItemNewAutoName(), c.getText(), c);
		continue;
	    }
	}
    }

    protected boolean onOk()
    {
	final String itemName = getItemNameOnLine(getHotPointY());
	if (name == null || name.isEmpty())
	    return false;
	final Object obj = getItemObjByName(itemName);
	if (obj == null)
	    return false;
	if (obj instanceof WizardClickable)
	{
	    final WizardClickable c = (WizardClickable)obj;
	    c.click();
	    return true;
	}
	return false;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		if (onOk())
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
	context.setEventResponse(DefaultEventResponse.text(Sounds.MAIN_MENU_ITEM, context.getSpeakableText(line, Luwrain.SpeakableTextType.NATURAL)));
    }
}
