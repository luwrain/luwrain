/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

/**
 * Implementation of editing behaviour for the line of text. This class
 * handles typing of characters, as well as backspace and delete keys.
 * It doesn't provide navigating functions, so should be used in
 * conjunction with some navigation handler over the text (for example,
 * with {@link NavigationArea}).
 *
 * @see org.luwrain.controls.edit.MultilineEdit
 */
public class SingleLineEdit implements ClipboardTranslator.Provider, RegionTextQueryTranslator.Provider
{
    public interface Model
    {
	String getLine();
	void setLine(String text);
	int getHotPointX();
	void setHotPointX(int value);
	String getTabSeq();
    }

    protected final ControlContext context;
        protected final Model model;
    protected final AbstractRegionPoint regionPoint;
    protected final RegionTextQueryTranslator regionTextQueryTranslator;
    protected final ClipboardTranslator clipboardTranslator;

    public SingleLineEdit(ControlContext context, Model model, AbstractRegionPoint regionPoint)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(model, "model");
	NullCheck.notNull(regionPoint, "regionPoint");
	this.context = context;
	this.model = model;
	this.regionPoint = regionPoint;
	this.clipboardTranslator = new ClipboardTranslator(this, regionPoint, EnumSet.noneOf(ClipboardTranslator.Flags.class));
	this.regionTextQueryTranslator = new RegionTextQueryTranslator(this, regionPoint, EnumSet.noneOf(RegionTextQueryTranslator.Flags.class));
    }


    public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.withControl() || event.withAlt())
	    return false;
	if (event.isSpecial())
	    switch (event.getSpecial())
	    {
	    case HOME:
		return onHome(event);
	    case BACKSPACE:
		return onBackspace(event);
	    case DELETE:
		return onDelete(event);
	    case TAB:
		return onTab(event);
	    default:
		return false;
	    }
	return onCharacter(event);
    }

    public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() !=SystemEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case CLIPBOARD_PASTE:
	    return onClipboardPaste();
	case CLEAR:
	    return onClear();
	default:
	    if (clipboardTranslator.onSystemEvent(event, model.getHotPointX(), 0))
		return true;
	    	    if (regionTextQueryTranslator.onSystemEvent(event, model.getHotPointX(), 0))
		return true;
		    return false;
	    	}
    }

    public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (regionTextQueryTranslator.onAreaQuery(query, model.getHotPointX(), 0))
	    return true;
	return false;
    }

    protected boolean onHome(InputEvent event)
    {
	final String line = model.getLine();
	NullCheck.notNull(line, "line");
	model.setHotPointX(0);
	if (!line.isEmpty())
	    context.sayLetter(line.charAt(0)); else
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	return true;
    }

    protected boolean onBackspace(InputEvent event)
    {
	final String line = model.getLine();
	NullCheck.notNull(line, "line");
	final int pos = model.getHotPointX();
	if (pos < 0 || pos > line.length())
	    return false;
	if (pos < 1)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_TEXT));
	    return true;
	}
	final String newLine = new String(line.substring(0, pos - 1) + line.substring(pos));
	model.setLine(newLine);
	model.setHotPointX(pos - 1);
	context.sayLetter(line.charAt(pos - 1));
	return true;
    }

    protected boolean onDelete(InputEvent event)
    {
	final String line = model.getLine();
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos < 0 || pos > line.length())
	    return false;
	if (pos >= line.length())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_TEXT));
	    return true;
	}
	if (pos == line.length() - 1)
	{
	    model.setLine(line.substring(0, pos));
	    context.sayLetter(line.charAt(pos));
	    return true;
	}
	final String newLine = new String(line.substring(0, pos) + line.substring(pos + 1));
	model.setLine(newLine);
	context.sayLetter(line.charAt(pos));
	return true;
    }

    protected boolean onTab(InputEvent event)
    {
	final String line = model.getLine();
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos < 0 || pos > line.length())
	    return false;
	final String tabSeq = model.getTabSeq();
	if (tabSeq == null)
	    return false;
	if (pos < line.length())
	{
	    final String newLine = new String(line.substring(0, pos) + tabSeq + line.substring(pos));
	    model.setLine(newLine);
	} else
	    model.setLine(line + tabSeq);
	context.setEventResponse(DefaultEventResponse.hint(Hint.TAB));
	model.setHotPointX(pos + tabSeq.length());
	return true;
    }

    protected boolean onCharacter(InputEvent event)
    {
	String line = model.getLine();
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos < 0 || pos > line.length())
	    return false;
	if (pos == line.length())
	{
	    model.setLine(line + event.getChar());
	    model.setHotPointX(pos + 1);
	    if (event.getChar() == ' ')
	    {
		final String lastWord = TextUtils.getLastWord(line, line.length());//Since we have attached exactly space, we can use old line value, nothing changes;
		if (lastWord != null && !lastWord.isEmpty())
		    context.say(lastWord); else
		    context.setEventResponse(DefaultEventResponse.hint(Hint.SPACE));
	    } else
		context.sayLetter(event.getChar());
	    return true;
	}
	final String newLine = new String(line.substring(0, pos) + event.getChar() + line.substring(pos));
	model.setLine(newLine);
	model.setHotPointX(pos + 1);
	if (event.getChar() == ' ')
	{
	    final String lastWord = TextUtils.getLastWord(newLine, pos + 1);
	    if (lastWord != null && !lastWord.isEmpty())
		context.say(lastWord); else
		context.setEventResponse(DefaultEventResponse.hint(Hint.SPACE));
	} else
	    context.sayLetter(event.getChar());
	return true;
    }

    @Override public String onRegionTextQuery(int fromX, int fromY, int toX, int toY)
    {
	final String line = model.getLine();
	if (line == null)
	    return null;
	final int fromPos = Math.min(fromX, line.length());
	final int toPos = Math.min(toX, line.length());
	if (fromPos >= toPos)
	    return null;
	return line.substring(fromPos, toPos);
    }

    @Override public boolean onClipboardCopyAll()
    {
	final String line = model.getLine();
	if (line == null)
	    return false;
	context.getClipboard().set(line);
	return true;
    }

    @Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
    {
	final String line = model.getLine();
	if (line == null)
	    return false;
	final int fromPos = Math.min(fromX, line.length());
	final int toPos = Math.min(toX, line.length());
	if (fromPos >= toPos)
	    return false;
		if (withDeleting && !onDeleteRegion(fromX, fromY, toX, toY))
	    return false;
	final String res = line.substring(fromPos, toPos);
	context.getClipboard().set(res);
	return true;
    }

    @Override public boolean onDeleteRegion(int fromX, int fromY, int toX, int toY)
    {
	final String line = model.getLine();
	if (line == null || line.isEmpty())
	    return false;
	final int fromPos = Math.min(fromX, line.length());
	final int toPos = Math.min(toX, line.length());
	if (fromPos >= toPos)
	    return false;
	model.setLine(line.substring(0, fromPos) + line.substring(toPos));
	model.setHotPointX(fromPos);//Is this really right?
	return true;
    }

    protected boolean onClear()
    {
	model.setLine("");
	model.setHotPointX(0);
	return true;
    }


protected boolean onClipboardPaste()
    {
	final String text = context.getClipboard().getString(" ");
	if (text.isEmpty())
	    return false;
	final String line = model.getLine();
	final int pos = Math.min(model.getHotPointX(), line.length());
	model.setLine(line.substring(0, pos) + text + line.substring(pos));
	model.setHotPointX(pos + text.length());
	return true;
    }
}
