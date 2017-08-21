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
	boolean onConsoleClick(ConsoleArea area, int index, Object obj);
    }

    public interface InputHandler
    {
	boolean onConsoleInput(String text);
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

    public void setClickHandler(ClickHandler clickHandler)
    {
	this.consoleClickHandler = clickHandler;
    }

    public void moveHotPointToInput()
    {
	setHotPoint(enteringPrefix.length(), getEnteringLineIndex());
    }

    void setEnteringPrefix(String prefix)
    {
	NullCheck.notNull(prefix, "prefix");
this.enteringPrefix = prefix;
	refresh();
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
context.hint(Hints.EMPTY_LINE);
	    return;
	case TOP:
	    if (index > 0 && index - 1 < consoleModel.getConsoleItemCount())
	{
	    consoleAppearance.announceItem(consoleModel.getConsoleItem(index - 1));
	    return;
	}
	if (!line.isEmpty())
context.say(line ); else
context.hint(Hints.EMPTY_LINE);
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
	if (!consoleInputHandler.onConsoleInput(enteringText))
	    return false;
	enteringText = "";
	refresh();
	return true;
    }

public void refresh()
    {
	edit.setNewPos(enteringPrefix.length(), getEnteringLineIndex());
context.onAreaNewContent(this);
	if (getHotPointY() >= getLineCount())
	    setHotPointY(getLineCount() - 1);
	final String line = getLine(getHotPointY());
	if (getHotPointX() > line.length())
	    setHotPointX(line.length());
    }
}
