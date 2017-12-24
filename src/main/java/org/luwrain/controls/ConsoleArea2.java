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

public class ConsoleArea2 extends NavigationArea implements  EmbeddedEditLines
{
    public enum InputPos {TOP, BOTTOM};

    public interface Model
    {
	int getConsoleItemCount();
	Object getConsoleItem(int index);
    }

    public interface Appearance 
    {
	void announceItem(Object item);
	String getTextAppearance(Object item);
    }

    public interface ClickHandler
    {
	boolean onConsoleClick(ConsoleArea2 area, int index, Object obj);
    }

    public interface InputHandler
    {
	public enum Result {REJECTED, OK, CLEAR_INPUT};

	Result onConsoleInput(ConsoleArea2 area, String text);
    }

    static public class Params
    {
	public ControlEnvironment context = null;
	public String areaName = "";
	public Model model = null;
	public Appearance appearance = null;
	public ClickHandler clickHandler = null;
	public InputHandler inputHandler = null;
	public InputPos inputPos = InputPos.TOP;
    }

    protected String areaName = "";
    protected Model consoleModel;
    protected final Appearance consoleAppearance;
    protected ClickHandler consoleClickHandler = null;
    protected InputHandler consoleInputHandler = null;
    protected final EmbeddedSingleLineEdit edit;
    protected final InputPos inputPos;

    protected String enteringPrefix = ">";
    protected String enteringText = "";

    public ConsoleArea2(Params params)
    {
	super(params.context);
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.model, "params.model");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.areaName, "params.areaName");
	NullCheck.notNull(params.inputPos, "params.inputPos");
	this.inputPos = params.inputPos;
	this.consoleModel = params.model;
	this.consoleAppearance = params.appearance;
	this.consoleClickHandler = params.clickHandler;
	this.consoleInputHandler = params.inputHandler;
	this.areaName = params.areaName;
	edit = new EmbeddedSingleLineEdit(context, this, this, 0, 0);
	refresh();
    }

    public void setConsoleClickHandler(ClickHandler clickHandler)
    {
	this.consoleClickHandler = clickHandler;
    }

    public void setConsoleInputHandler(InputHandler inputHandler)
    {
	this.consoleInputHandler = inputHandler;
    }

    public void moveHotPointToInput()
    {
	setHotPoint(enteringPrefix.length(), getEnteringLineIndex());
    }

    public void setInputPrefix(String prefix)
    {
	NullCheck.notNull(prefix, "prefix");
	this.enteringPrefix = prefix;
	refresh();
    }

    public void setInput(String value)
    {
	NullCheck.notNull(value, "value");
	this.enteringText = value;
	refresh();
    }

    public void refresh()
    {
	edit.setNewOffset(enteringPrefix.length(), getEnteringLineIndex());
	context.onAreaNewContent(this);
	if (getHotPointY() >= getLineCount())
	    setHotPointY(getLineCount() - 1);
	final String line = getLine(getHotPointY());
	if (getHotPointX() > line.length())
	    setHotPointX(line.length());
    }

    public int getSelectedIndex()
    {
	return getExistingItemIndexOnLine(getHotPointY());
    }

    public Object selected()
    {
	final int index = getSelectedIndex();
	return index >= 0?consoleModel.getConsoleItem(index):null;
    }

    public int getExistingItemIndexOnLine(int lineIndex)
    {
	if (lineIndex < 0)
	    throw new IllegalArgumentException("lineIndex may not be negative (" + lineIndex + ")");
	switch(inputPos)
	{
	case TOP:
	    if (lineIndex == 0)
		return -1;
	    if (lineIndex - 1 < consoleModel.getConsoleItemCount())
		return lineIndex - 1;
	    return -1;
	case BOTTOM:
	    if (lineIndex < consoleModel.getConsoleItemCount())
		return lineIndex;
	    return -1;
	default:
	    return -1;
	}
    }

    @Override public int getLineCount()
    {
	return consoleModel.getConsoleItemCount() + 2;
    }

    @Override public String getLine(int index)
    {
	if (index < 0 || index >= consoleModel.getConsoleItemCount() + 1)
	    return "";
	switch(inputPos)
	{
	case TOP:
	    if (index == 0)
		return enteringPrefix + enteringText;
	    if (index - 1< consoleModel.getConsoleItemCount())
		return consoleAppearance.getTextAppearance(consoleModel.getConsoleItem(index - 1));
	    return "";
	case BOTTOM:
	    if (index < consoleModel.getConsoleItemCount())
		return consoleAppearance.getTextAppearance(consoleModel.getConsoleItem(index));
	    if (index == consoleModel.getConsoleItemCount())
		return enteringPrefix + enteringText;
	    return "";
	default:
	    return "";
	}
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

    if (event.isSpecial() && !event.isModified())
	switch(event.getSpecial())
	{
	case ENTER:
	    {
		final int index = getExistingItemIndexOnLine(getHotPointY());
		if (index >= 0)
		{
		    if (consoleClickHandler == null)
			return false;
		    return consoleClickHandler.onConsoleClick(this, index, consoleModel.getConsoleItem(index));
		}
	    }
	    break;
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
	refresh();
    }

    @Override public String getEmbeddedEditLine(int x,int y)
    {
	return enteringText;
    }

    @Override public void announceLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	switch(inputPos)
	{
	case BOTTOM:
	    if (index < consoleModel.getConsoleItemCount())
	    {
		consoleAppearance.announceItem(consoleModel.getConsoleItem(index));
		return;
	    }
	    if (!line.isEmpty())
		context.say(line ); else
		context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return;
	case TOP:
	    if (index > 0 && index - 1 < consoleModel.getConsoleItemCount())
	    {
		consoleAppearance.announceItem(consoleModel.getConsoleItem(index - 1));
		return;
	    }
	    if (!line.isEmpty())
		context.say(line ); else
		context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return;
	}
    }

    protected int getEnteringLineIndex()
    {
	switch(inputPos)
	{
	case BOTTOM:
	    return consoleModel.getConsoleItemCount();
	case TOP:
	    return 0;
	default:
	    return 0;
	}
    }

    protected boolean onEnterInEdit()
    {
	if (consoleInputHandler == null || enteringText.isEmpty())
	    return false;
	final InputHandler.Result res = consoleInputHandler.onConsoleInput(this, enteringText);
	if (res == null)
	    return false;
	switch(res)
	{
	case REJECTED:
	    return false;
	case CLEAR_INPUT:
	enteringText = "";
	break;
	}
	refresh();
	return true;
    }
}
