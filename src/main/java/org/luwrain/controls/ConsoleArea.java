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

//LWR_API 1.0

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.i18n.LangStatic;//FIXME:deleting

public class ConsoleArea<E> extends NavigationArea implements  EmbeddedEditLines
{
    public enum InputPos {TOP, BOTTOM};

    public interface Model<E>
    {
	int getItemCount();
	E getItem(int index);
    }

    public interface Appearance<E> 
    {
	void announceItem(E item);
	String getTextAppearance(E item);
    }

    public interface ClickHandler<E>
    {
	boolean onConsoleClick(ConsoleArea area, int index, E obj);
    }

    public interface InputHandler
    {
	public enum Result {REJECTED, OK, CLEAR_INPUT};

	Result onConsoleInput(ConsoleArea area, String text);
    }

    static public class Params<E>
    {
	public ControlContext context = null;
	public String name = "";
	public Model<E> model = null;
	public Appearance<E> appearance = null;
	public ClickHandler<E> clickHandler = null;
	public InputHandler inputHandler = null;
	public InputPos inputPos = InputPos.TOP;
	public String inputPrefix = ">";
    }

    protected String areaName = "";
    protected Model<E> consoleModel;
    protected final Appearance<E> consoleAppearance;
    protected ClickHandler<E> consoleClickHandler = null;
    protected InputHandler consoleInputHandler = null;
    protected final EmbeddedEdit edit;
    protected final InputPos inputPos;

    protected String enteringPrefix = "";
    protected String enteringText = "";

    public ConsoleArea(Params<E> params)
    {
	super(params.context);
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.model, "params.model");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.name, "params.name");
	NullCheck.notNull(params.inputPos, "params.inputPos");
	NullCheck.notNull(params.inputPrefix, "params.inputPrefix");
	this.inputPos = params.inputPos;
	this.consoleModel = params.model;
	this.consoleAppearance = params.appearance;
	this.consoleClickHandler = params.clickHandler;
	this.consoleInputHandler = params.inputHandler;
	this.enteringPrefix = params.inputPrefix;
	this.areaName = params.name;
	this.edit = new EmbeddedEdit(context, this, this, regionPoint, 0, 0);
	refresh();
	moveHotPointToInput();
    }

    public void setConsoleClickHandler(ClickHandler<E> clickHandler)
    {
	this.consoleClickHandler = clickHandler;
    }

    public void setConsoleInputHandler(InputHandler inputHandler)
    {
	this.consoleInputHandler = inputHandler;
    }

    public void moveHotPointToInput()
    {
	setHotPoint(enteringPrefix.length() + enteringText.length(), getEnteringLineIndex());
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

    public E selected()
    {
	final int index = getSelectedIndex();
	return index >= 0?consoleModel.getItem(index):null;
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
	    if (lineIndex - 1 < consoleModel.getItemCount())
		return lineIndex - 1;
	    return -1;
	case BOTTOM:
	    if (lineIndex < consoleModel.getItemCount())
		return lineIndex;
	    return -1;
	default:
	    return -1;
	}
    }

    @Override protected boolean onAltHome(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (this.inputPos != InputPos.TOP)
	    return super.onAltHome(event);
	moveHotPointToInput();
	if (enteringText.isEmpty())
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
	    if (enteringText.trim().isEmpty())
		context.setEventResponse(DefaultEventResponse.hint(Hint.SPACES)); else
		context.setEventResponse(DefaultEventResponse.text(enteringText));
	return true;
    }

        @Override protected boolean onAltEnd(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (this.inputPos != InputPos.BOTTOM)
	    return super.onAltEnd(event);
	moveHotPointToInput();
	if (enteringText.isEmpty())
	    context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
	    if (enteringText.trim().isEmpty())
		context.setEventResponse(DefaultEventResponse.hint(Hint.SPACES)); else
		context.setEventResponse(DefaultEventResponse.text(enteringText));
	return true;
    }

    @Override public int getLineCount()
    {
	return consoleModel.getItemCount() + 2;
    }

    @Override public String getLine(int index)
    {
	if (index < 0 || index >= consoleModel.getItemCount() + 1)
	    return "";
	switch(inputPos)
	{
	case TOP:
	    if (index == 0)
		return enteringPrefix + enteringText;
	    if (index - 1< consoleModel.getItemCount())
		return consoleAppearance.getTextAppearance(consoleModel.getItem(index - 1));
	    return "";
	case BOTTOM:
	    if (index < consoleModel.getItemCount())
		return consoleAppearance.getTextAppearance(consoleModel.getItem(index));
	    if (index == consoleModel.getItemCount())
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

    @Override public boolean onInputEvent(InputEvent event)
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
	    if (edit.onInputEvent(event))
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
		    return consoleClickHandler.onConsoleClick(this, index, consoleModel.getItem(index));
		}
	    }
	    break;
	}

	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	if (edit.isPosCovered(getHotPointX(), getHotPointY()) && edit.onSystemEvent(event))
	    return true;
	switch(event.getCode())
	{
	case OK:
	    if (edit.isPosCovered(getHotPointX(), getHotPointY()))
		return onEnterInEdit();
	    return false;
	default:
	    return super.onSystemEvent(event);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (edit.isPosCovered(getHotPointX(), getHotPointY()) && edit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

    @Override protected boolean onMoveDown(InputEvent event)
    {
	final int count = getValidLineCount();
	this.hotPointY = this.hotPointY < count?hotPointY:count - 1;
	if (hotPointY + 1 >= count)
	{
	    if (count == 1)
		context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_BELOW, context.staticStr(LangStatic.NO_LINES_BELOW) + " " + getLineNotNull(0))); else
		context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_BELOW));
	    return true;
	}
	++this.hotPointY;
	final String nextLine = getLineNotNull(hotPointY);
	this.hotPointX = 0;
	context.onAreaNewHotPoint(this);
	announceLine(hotPointY, nextLine);
	return true;
    }

    @Override protected boolean onMoveUp(InputEvent event)
    {
	final int count = getValidLineCount();
	this.hotPointY = this.hotPointY < count?hotPointY:count - 1;
	if (hotPointY == 0)
	{
	    if (count == 1)
		context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_ABOVE, context.staticStr(LangStatic.NO_LINES_ABOVE) + " " + getLineNotNull(0))); else
		context.setEventResponse(DefaultEventResponse.hint(Hint.NO_LINES_ABOVE));
	    return true;
	}
	--this.hotPointY;
	final String prevLine = getLineNotNull(hotPointY);
	this.hotPointX = 0;
	context.onAreaNewHotPoint(this);
	announceLine(hotPointY, prevLine);
	return true;
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
	    if (index < consoleModel.getItemCount())
	    {
		consoleAppearance.announceItem(consoleModel.getItem(index));
		return;
	    }
	    if (!line.isEmpty())
		context.say(line ); else
		context.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return;
	case TOP:
	    if (index > 0 && index - 1 < consoleModel.getItemCount())
	    {
		consoleAppearance.announceItem(consoleModel.getItem(index - 1));
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
	    return consoleModel.getItemCount();
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
