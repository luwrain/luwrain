/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class SimpleEditPopup implements Popup, PopupClosingRequest, HotPointControl, EmbeddedEditLines, RegionProvider
{
    protected Luwrain luwrain;
    public final PopupClosing closing = new PopupClosing(this);
    private final Region region = new Region(this);
    private EmbeddedSingleLineEdit edit;
    protected String name;
    protected String prefix;
    private String text;
    private int pos;
    private int popupFlags;

    public SimpleEditPopup(Luwrain luwrain,
			    String name,
			    String prefix,
			    String text)
    {
	this.luwrain = luwrain;
	this.name = name;
	this.prefix = prefix;
	this.text = text;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (prefix == null)
	    throw new NullPointerException("prefix may not be null");
	if (text == null)
	    throw new NullPointerException("text may not be null");
	this.pos = prefix.length() + text.length();
	this.edit = new EmbeddedSingleLineEdit(new DefaultControlEnvironment(luwrain), this, this, prefix.length(), 0);
	this.popupFlags = 0;
    }

    public SimpleEditPopup(Luwrain luwrain,
			    String name,
			    String prefix,
			   String text,
			   int popupFlags)
    {
	this.luwrain = luwrain;
	this.name = name ;
	this.prefix = prefix;
	this.text = text;
	this.popupFlags = popupFlags;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (prefix == null)
	    throw new NullPointerException("prefix may not be null");
	if (text == null)
	    throw new NullPointerException("text may not be null");
	this.pos = prefix.length() + text.length();
	this.edit = new EmbeddedSingleLineEdit(new DefaultControlEnvironment(luwrain), this, this, prefix.length(), 0);
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

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (closing.onKeyboardEvent(event))
	    return true;
	if (edit.isPosCovered(pos, 0) && edit.onKeyboardEvent(event))
	    return true;
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch (event.getSpecial())
	{
	case ARROW_LEFT:
	    return onArrowLeft(event);
	case ARROW_RIGHT:
	    return onArrowRight(event);
	case ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	case HOME:
	    return onHome(event);
	case END:
	    return onEnd(event);
	case ENTER:
	    return closing.doOk();
	default:
	    return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(event.getCode())
	{
	case INTRODUCE:
	    luwrain.silence();
	    luwrain.playSound(Sounds.INTRO_POPUP);
	    luwrain.say(prefix + text);
	    return true;
	}
	if (edit.isPosCovered(pos, 0) && edit.onEnvironmentEvent(event))
	    return true;
	if (region.onEnvironmentEvent(event, pos, 0))
	    return true;
	return closing.onEnvironmentEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (edit.isPosCovered(pos, 0) && edit.onAreaQuery(query))
	    return true;
	    return region.onAreaQuery(query, pos, 0);
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

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (pos == 0)
	{
	    luwrain.hint(Hints.BEGIN_OF_LINE);
	    return true;
	    }
	--pos;
	final String line = prefix + text;
	if (pos < line.length())
	    luwrain.sayLetter(line.charAt(pos)); else
	    luwrain.hint(Hints.END_OF_LINE);
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	final String line = prefix + text;
	if (pos >= line.length())
	{
	    luwrain.hint(Hints.END_OF_LINE);
	    return true;
	}
	++pos;
	if (pos < line.length())
	    luwrain.sayLetter(line.charAt(pos)); else
	    luwrain.hint(Hints.END_OF_LINE);
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	pos = edit.isPosCovered(pos, 0)?prefix.length():0;
	final String line = prefix + text;
	if (pos >= line.length())
	    luwrain.hint(Hints.EMPTY_LINE); else
	    luwrain.sayLetter(line.charAt(pos));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	final String line = prefix + text;
	pos = line.length();
	luwrain.hint(Hints.END_OF_LINE);
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltRight(KeyboardEvent event)
    {
	final String line = prefix + text;
	if (line.isEmpty())
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (pos >= line.length())
	{
	    luwrain.hint(Hints.END_OF_LINE);
	    return true;
	}
	WordIterator it = new WordIterator(line, pos);
	if (!it.stepForward())
	{
	    luwrain.hint(Hints.END_OF_LINE);
	    return true;
	}
	pos = it.pos();
	if (it.announce().length() > 0)
	    luwrain.say(it.announce()); else
	    luwrain.hint(Hints.END_OF_LINE);
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltLeft(KeyboardEvent event)
    {
	final String line = prefix + text;
	if (line.isEmpty())
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	WordIterator it = new WordIterator(line, pos);
	if (!it.stepBackward())
	{
	    luwrain.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	pos = it.pos();
	luwrain.say(it.announce());
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

    //Speaks nothing;
    protected void setText(String beforeHotPoint, String afterHotPoint)
    {
	if (beforeHotPoint == null || afterHotPoint == null)
	    return;
	text = beforeHotPoint + afterHotPoint;
	pos = prefix.length() + beforeHotPoint.length();
	luwrain.onAreaNewContent(this);
	luwrain.onAreaNewHotPoint(this);
    }

    @Override public RegionContent getWholeRegion()
    {
	final String line = prefix + text;
	return new RegionContent(new String[]{line});
    }

    @Override public RegionContent getRegion(int fromX, int fromY,
					int toX, int toY)
    {
	final String line = prefix + text;
	if (line.isEmpty())
	    return null;
	final int fromPos = fromX < line.length()?fromX:line.length();
	final int toPos = toX < line.length()?toX:line.length();
	if (fromPos >= toPos)
	    return null;
	final String res = line.substring(fromPos, toPos);
	return new RegionContent(new String[]{res});
    }

    @Override public boolean deleteWholeRegion()
    {
	return false;
    }

    @Override public boolean deleteRegion(int fromX, int fromY,
					  int toX, int toY)
    {
	return false;
    }

    @Override public boolean insertRegion(int x, int y,
					  RegionContent data)
    {
	return false;
    }

    @Override public Luwrain getLuwrainObject()
    {
	return luwrain;
    }

    @Override public EventLoopStopCondition getStopCondition()
    {
	return closing;
    }

    @Override public boolean noMultipleCopies()
    {
	return (popupFlags & Popup.NO_MULTIPLE_COPIES) != 0;
    }

    @Override public boolean isWeakPopup()
    {
	return (popupFlags & Popup.WEAK) != 0;
    }
}
