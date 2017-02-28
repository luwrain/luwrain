/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

public class ConsoleArea extends NavigationArea implements  EmbeddedEditLines
{
    public interface Appearance 
    {
	void announceItem(Object item);
	String getTextAppearance(Object item);
}

    public interface ClickHandler
    {
	boolean onEnteredText(String text);
    }

    static public class Params
    {
	public ControlEnvironment environment;
	public String areaName = "";
	public Appearance appearance;
	public ClickHandler clickHandler;
    }

    protected final ControlEnvironment environment;
    protected String areaName = "";
    protected final Appearance appearance;
    protected ClickHandler clickHandler = null;
    protected final EmbeddedSingleLineEdit edit;

    protected Object[] items = new Object[0];
    protected final String enteringPrefix = ">";
    protected String enteringText = "";

    public ConsoleArea(Params params)
    {
	super(params.environment);
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.areaName, "params.areaName");
	this.environment = params.environment;
	this.appearance = params.appearance;
	this.clickHandler = params.clickHandler;
	this.areaName = params.areaName;
	edit = new EmbeddedSingleLineEdit(environment, this, this, 0, 0);
	updateEditPos();
    }

    public void setClickHandler(ClickHandler clickHandler)
    {
	this.clickHandler = clickHandler;
    }

    public void setItems(Object[] items)
    {
	NullCheck.notNull(items, "items");
	final boolean needToUpdateHotPointY = (getHotPointY() == getEnteringLineIndex());
	this.items = items;
	refresh();
	if (needToUpdateHotPointY)
	    setHotPointY(getEnteringLineIndex());
    }

    void setEnteringPrefix(String prefix)
    {
	NullCheck.notNull(prefix, "prefix");
	//	this.enteringPrefix = prefix;
	refresh();
    }

    @Override public int getLineCount()
    {
	return items.length + 2;
    }

    public void refresh()
    {
	updateEditPos();
	environment.onAreaNewContent(this);
    }

    @Override public String getLine(int index)
    {
	if (index < items.length)
	    return appearance.getTextAppearance(items[index]);
	if (index == items.length)
	    return enteringPrefix + enteringText;
	return "";
    }

    @Override public String getAreaName()
    {
	return areaName;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!edit.isPosCovered(getHotPointX(), getHotPointY()) && !event.isSpecial())
	    setHotPoint(enteringPrefix.length() + enteringText.length(), getEnteringLineIndex());
	if (edit.isPosCovered(getHotPointX(), getHotPointY()))
	{
	    if (event.isSpecial() && !event.isModified())
		switch(event.getSpecial())
		{
		case ENTER:
		    return onEnterInEdit();
		}
	    if (edit.onKeyboardEvent(event))
		return true;
	}
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onEnvironmentEvent(event);
	if (edit.isPosCovered(getHotPointX(), getHotPointY()) && edit.onEnvironmentEvent(event))
	    return true;
	switch(event.getCode())
	{
	case OK:
	    if (edit.isPosCovered(getHotPointX(), getHotPointY()))
		return onEnterInEdit();
	    return false;
	default:
	    return super.onEnvironmentEvent(event);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (edit.isPosCovered(getHotPointX(), getHotPointY()) && edit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

    @Override public void setEmbeddedEditLine(int x, int y, String line)
    {
	NullCheck.notNull(line, "line");
	enteringText = line;
	environment.onAreaNewContent(this);
    }

    @Override public String getEmbeddedEditLine(int x,int y)
    {
	return enteringText;
    }

    @Override public void announceLine(int index, String line)
    {
	if (index < items.length)
	{
	    appearance.announceItem(items[index]);
	    return;
	}
	if (line == null || line.isEmpty())
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return;
	}
environment.silence();
environment.say(line );
    }

    protected int getEnteringLineIndex()
    {
	//FIXME:console type
	return items.length;
    }

    protected boolean onEnterInEdit()
    {
	if (clickHandler == null || enteringText.isEmpty())
	    return false;
	if (!clickHandler.onEnteredText(enteringText))
	    return false;
	enteringText = "";
	environment.onAreaNewContent(this);
	if (getHotPointY() == getEnteringLineIndex() && getHotPointX() > enteringPrefix.length())
	    setHotPointX(enteringPrefix.length());
	return true;
    }

    protected void updateEditPos()
    {
	edit.setNewPos(enteringPrefix.length(), getEnteringLineIndex());
	environment.onAreaNewContent(this);
    }
}
