/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

/**
 * Shows a popup for input of single line of text. This class is an
 * implementation of {@link org.luwrain.core.Popup} interface with
 * functionality to let user to give single line of text without any
 * completion and helping features. This class takes a short line which
 * will be shown before the input describing the purpose of expected
 * value. As well, this class takes acceptance object which prevents
 * input of undesirable values. 
 *
 * @see ListPopup EditListPopup FilePopup 
 */
public class SimpleEditPopup implements Popup, PopupClosingTranslator.Provider, HotPointControl, EmbeddedEditLines, ClipboardTranslator.Provider, RegionTextQueryTranslator.Provider
{
    protected final Luwrain luwrain;
    protected final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final RegionPoint regionPoint = new RegionPoint();
    protected final ClipboardTranslator clipboardTranslator = new ClipboardTranslator(this, regionPoint, EnumSet.noneOf(ClipboardTranslator.Flags.class));
    protected final RegionTextQueryTranslator regionTextQueryTranslator = new RegionTextQueryTranslator(this, regionPoint, EnumSet.noneOf(RegionTextQueryTranslator.Flags.class));
    protected final EmbeddedSingleLineEdit edit;
    protected final String name;
    protected final String prefix;
    protected String text;
    protected int pos;
    protected final Set<Popup.Flags> popupFlags;

    public SimpleEditPopup(Luwrain luwrain,
			   String name,
			   String prefix,
			   String text,
			   Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(text, "text");
	NullCheck.notNull(popupFlags, "popupFlags");
	this.luwrain = luwrain;
	this.name = name ;
	this.prefix = prefix;
	this.text = text;
	this.popupFlags = popupFlags;
	this.pos = prefix.length() + text.length();
	this.edit = new EmbeddedSingleLineEdit(new DefaultControlContext(luwrain), this, this, regionPoint, prefix.length(), 0);
    }

    @Override public int getLineCount()
    {
	return 1;
    }

    @Override public String getLine(int index)
    {
	return index == 0?(prefix + text):"";
    }

    @Override public int getHotPointX()
    {
	return pos;
    }

    @Override public int getHotPointY()
    {
	return 0;
    }

    protected String getSpeakableText(String prefix, String text)
    {
	NullCheck.notNull(text, "text");
	return prefix + text;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onInputEvent(event))
	    return true;
	if (edit.isPosCovered(pos, 0) && edit.onInputEvent(event))
	    return true;
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch (event.getSpecial())
	{
	case ARROW_LEFT:
	    return onMoveLeft(event);
	case ARROW_RIGHT:
	    return onMoveRight(event);
	case ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	    	case HOME:
	    	    return onHome(event);
	case END:
	    return onEnd(event);
	case ENTER:
	    closing.doOk();
	    return true;
	default:
	    return false;
	}
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case INTRODUCE:
	    luwrain.speak(getSpeakableText(prefix, text), Sounds.INTRO_POPUP);
	    return true;
	}
	if (edit.isPosCovered(pos, 0) && edit.onSystemEvent(event))
	    return true;
	if (clipboardTranslator.onSystemEvent(event, pos, 0))
	    return true;
		if (regionTextQueryTranslator.onSystemEvent(event, pos, 0))
	    return true;
			return closing.onSystemEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (edit.isPosCovered(pos, 0) && edit.onAreaQuery(query))
	    return true;
	if (regionTextQueryTranslator.onAreaQuery(query, getHotPointX(), getHotPointY()))
	    return true;
	    return false;
	    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public String getAreaName()
    {
	return name;
    }

    public String text()
    {
	return text;
    }

protected boolean onMoveLeft(InputEvent event)
    {
	if (pos == 0)
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	    return true;
	    }
	--pos;
	final String line = prefix + text;
	if (pos < line.length())
	    luwrain.setEventResponse(DefaultEventResponse.letter(line.charAt(pos))); else
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onMoveRight(InputEvent event)
    {
	final String line = prefix + text;
	if (pos >= line.length())
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	++pos;
	if (pos < line.length())
	    luwrain.setEventResponse(DefaultEventResponse.letter(line.charAt(pos))); else
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onHome(InputEvent event)
    {
	pos = 0;
	final String line = prefix + text;
	if (line.isEmpty())
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
	    luwrain.setEventResponse(DefaultEventResponse.letter(line.charAt(pos)));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onEnd(InputEvent event)
    {
	final String line = prefix + text;
	pos = line.length();
	luwrain.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onAltRight(InputEvent event)
    {
	final String line = prefix + text;
	if (line.isEmpty())
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	if (pos >= line.length())
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	final WordIterator it = new WordIterator(line, pos);
	if (!it.stepForward())
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	pos = it.pos();
	if (it.announce().length() > 0)
	    luwrain.speak(getSpeakableText("", it.announce())); else
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

protected boolean onAltLeft(InputEvent event)
    {
	final String line = prefix + text;
	if (line.isEmpty())
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	final WordIterator it = new WordIterator(line, pos);
	if (!it.stepBackward())
	{
	    luwrain.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	    return true;
	}
	pos = it.pos();
	luwrain.speak(getSpeakableText("", it.announce()));
	luwrain.onAreaNewHotPoint(this);
	    return true;
    }

    @Override public String getEmbeddedEditLine(int editPosX, int editPosY)
    {
	return text;
    }

    @Override public void setEmbeddedEditLine(int editPosX, int editPosY, String value)
    {
	text = value != null?value:"";
	luwrain.onAreaNewContent(this);
    }

    @Override public void setHotPointX(int value)
    {
	if (value < 0)
	    return;
	pos = value;
	luwrain.onAreaNewHotPoint(this);
    }

    @Override public void setHotPointY(int value)
    {
	//Nothing here;
    }

    @Override public void beginHotPointTrans()
    {
    }

    @Override public void endHotPointTrans()
    {
    }

    @Override public boolean onOk()
    {
	return true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    protected String getTextBeforeHotPoint()
    {
	if (text == null)
	    return "";
	final int offset = pos - prefix.length();
	if (offset < 0)
	    return "";
	if (offset >= text.length())
	    return text;
	return text.substring(0, offset);
    }

    protected String getTextAfterHotPoint()
    {
	if (text == null)
	    return "";
	final int offset = pos - prefix.length();
	if (offset < 0)
	    return text;
	if (offset >= text.length())
	    return "";
	return text.substring(offset);
    }

    //Speaks nothing
    protected void setText(String beforeHotPoint, String afterHotPoint)
    {
	if (beforeHotPoint == null || afterHotPoint == null)
	    return;
	text = beforeHotPoint + afterHotPoint;
	pos = prefix.length() + beforeHotPoint.length();
	luwrain.onAreaNewContent(this);
	luwrain.onAreaNewHotPoint(this);
    }

    @Override public String onRegionTextQuery(int fromX, int fromY, int toX, int toY)
    {
	if (fromX < 0 || toX < 0)
	    throw new IllegalArgumentException("fromX (" + fromX + ") and toX (" + toX + ") may not be less than zero");
	final String value = prefix + text;
	return value.substring(Math.min(fromX, value.length()), Math.min(toX, value.length()));
    }

    @Override public boolean onClipboardCopyAll()
    {
	luwrain.getClipboard().set(prefix + text);
	return true;
    }

    @Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
    {
	if (withDeleting)
	    return false;
	final String line = prefix + text;
	if (line.isEmpty())
	    return false;
	final int fromPos = Math.min(fromX, line.length());
	final int toPos = Math.min(toX, line.length());
	if (fromPos >= toPos)
	    return false;
	luwrain.getClipboard().set(line.substring(fromPos, toPos));
	return true;
    }

    @Override public boolean onDeleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    @Override public Luwrain getLuwrainObject()
    {
	return luwrain;
    }

    @Override public boolean isPopupActive()
    {
	return closing.continueEventLoop();
    }

    @Override public Set<Popup.Flags> getPopupFlags()
    {
	return popupFlags;
    }

    public boolean wasCancelled()
    {
	return closing.cancelled();
    }
}
