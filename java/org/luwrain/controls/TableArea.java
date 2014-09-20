/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.controls;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

/**
 * The area class with table behaviour.  The data to be provided is
 * requested from the instance of TableModel interface which can be
 * either standard or custom. Application developers can make their own
 * appearance of table content through extending the TableAppearance
 * interface.
 */
public class TableArea  implements Area, CopyCutRequest
{
    static final public int INTRODUCTION_BRIEF = 1;

    private ControlEnvironment environment = null;
    private CopyCutInfo copyCutInfo;
    private String name = "";
    private TableModel model = null;
    private TableAppearance appearance = null;
    private TableClickHandler clickHandler = null;
    private int initialHotPointX = 0;

    private int hotPointX = 0;
    private int hotPointY = 0;
    private int[] colWidth = null;
    private int cellShift = 0;

    public TableArea(ControlEnvironment environment, TableModel model)
    {
	this.environment = environment;
	this.model = model;
	this.appearance = new DefaultTableAppearance(environment);
	this.initialHotPointX = appearance.getInitialHotPointX(model);
	this.copyCutInfo = new CopyCutInfo(this);
    }

    public TableArea(ControlEnvironment environment,
		     TableModel model,
		     String name)
    {
	this.environment = environment;
	this.model = model;
	this.appearance = new DefaultTableAppearance(environment);
	this.initialHotPointX = appearance.getInitialHotPointX(model);
	this.name = name != null?name:"";
	this.copyCutInfo = new CopyCutInfo(this);
    }

    public TableArea(ControlEnvironment environment,
		     TableModel model,
		    String name,
		    TableAppearance appearance,
		    TableClickHandler clickHandler)
    {
	this.environment = environment;
	this.model = model;
	this.name = name != null?name:"";
	this.appearance = appearance != null?appearance:new DefaultTableAppearance(environment);
	this.initialHotPointX = appearance.getInitialHotPointX(model);
	this.clickHandler = clickHandler;
	this.copyCutInfo = new CopyCutInfo(this);
    }

    public Object getSelectedRow()
    {
	//FIXME:
	return null;
    }

    public Object getSelectedCol()
    {
	//FIXME:
	return null;
    }

    public Object getSelectedCell()
    {
	//FIXME:
	return null;
    }

    public int getSelectedRowIndex()
    {
	//FIXME:
	return -1;
    }

    public int getSelectedColIndex()
    {
	//FIXME:
	return -1;
    }

    public void refresh()
    {
	if (model == null)
	    return;
	model.refresh();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (!event.isCommand() || event.isModified())
	    return false;
	switch(event.getCommand())
	{
	case KeyboardEvent.ARROW_DOWN:
	    return onArrowDown(event, false);
	case KeyboardEvent.ARROW_UP:
	    return onArrowUp(event, false);
	case KeyboardEvent.ARROW_RIGHT:
	    return onArrowRight(event);
	case KeyboardEvent.ARROW_LEFT:
	    return onArrowLeft(event);
	case KeyboardEvent.ALTERNATIVE_ARROW_DOWN:
	    return onArrowDown(event, true);
	case KeyboardEvent.ALTERNATIVE_ARROW_UP:
	    return onArrowUp(event, true);
	    //FIXME:case KeyboardEvent.ALTERNATIVE_ARROW_RIGHT:
	    //FIXME:case KeyboardEvent.ALTERNATIVE_ARROW_LEFT:
	case KeyboardEvent.HOME:
	    return onHome(event);
	case KeyboardEvent.END:
	    return onEnd(event);
	case KeyboardEvent.ALTERNATIVE_HOME:
	    return onLineHome(event);
	case KeyboardEvent.ALTERNATIVE_END:
	    return onLineEnd(event);
	case KeyboardEvent.PAGE_DOWN:
	    return onPageDown(event, false);
	case KeyboardEvent.PAGE_UP:
	    return onPageUp(event, false);
	case KeyboardEvent.ALTERNATIVE_PAGE_DOWN:
	    return onPageDown(event, true);
	case KeyboardEvent.ALTERNATIVE_PAGE_UP:
	    return onPageUp(event, true);
	case KeyboardEvent.ENTER:
	    if (noProperContent() || 
		hotPointY < 0 || hotPointY >= model.getRowCount() ||
		colUnderPos(hotPointX) >= colWidth.length)
		return false;
	    return onClick(model, colUnderPos(hotPointX), hotPointY, model.getCell(colUnderPos(hotPointX), hotPointY));
	default:
	    return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch (event.getCode())
	{
	case EnvironmentEvent.REFRESH:
	    refresh();
	    return true;
	case EnvironmentEvent.COPY_CUT_POINT:
	    return copyCutInfo.doCopyCutPoint(hotPointX, hotPointY);
	case EnvironmentEvent.COPY:
	    if (!copyCutInfo.doCopy(hotPointX, hotPointY))
		copyEntireContent();
	    return true;
	default:
	    return false;
	}
    }

    @Override public int getLineCount()
    {
	if (noProperContent())
	    return 2;
	return model.getRowCount() + 1;
    }

    @Override public String getLine(int index)
    {
	if (noProperContent())
	    return index <= 0?environment.langStaticString(Langs.NO_TABLE_ROWS):"";
	if (index < 0 ||
	    index >= model.getRowCount())
	    return "";
	String line = stringOfLen(appearance.getRowPrefix(model, index), initialHotPointX, "", "");
	for(int i = 0;i < model.getColCount();++i)
	    line += stringOfLen(appearance.getCellText(model, i, index), colWidth[i], ">", " ");
	return line;
    }

    protected boolean onClick(TableModel model,
			      int col,
			      int row,
			      Object item)
    {
	if (clickHandler != null)
	    return clickHandler.onClick(model, col, row, item);
	return false;
    }

    @Override public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    @Override public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    @Override public String getName()
    {
	return name != null?name:"";
    }

    public void setName(String value)
    {
	name = value != null?value:"";
	environment.onAreaNewName(this);
    }

    private boolean onArrowDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noProperContent())
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS);
	    return true;
	}
	final int count = model.getRowCount();
	if (hotPointY >= count)
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS_BELOW);
		return true;
	}
	++hotPointY;
	hotPointX = hotPointY < count?initialHotPointX:0;
	environment.onAreaNewHotPoint(this);
	if (hotPointY < count)
	    appearance.introduceRow(model, hotPointY, briefIntroduction?INTRODUCTION_BRIEF:0); else
	    environment.hintStaticString(Langs.EMPTY_LINE);
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noProperContent())
	{
	    environment.hint(environment.langStaticString(Langs.NO_TABLE_ROWS));
	    return true;
	}
	final int count = model.getRowCount();
	if (hotPointY <= 0)
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS_ABOVE);
	    return true;
	}
	--hotPointY;
	if (hotPointY >= count)
		hotPointY = count - 1;
	hotPointX = initialHotPointX;
	environment.onAreaNewHotPoint(this);
	appearance.introduceRow(model, hotPointY, briefIntroduction?INTRODUCTION_BRIEF:0);
	return true;
    }

    private boolean onPageDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noProperContent())
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS);
	    return true;
	}
	final int count = model.getRowCount();
	if (hotPointY >= count)
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS_BELOW);
		return true;
	}
	hotPointY += environment.getAreaVisibleHeight(this);
	if (hotPointY >= count)
	    hotPointY = count;
	hotPointX = hotPointY < count?initialHotPointX:0;
	environment.onAreaNewHotPoint(this);
	if (hotPointY < count)
	    appearance.introduceRow(model, hotPointY, briefIntroduction?INTRODUCTION_BRIEF:0);
	environment.hintStaticString(Langs.EMPTY_LINE);
	return true;
    }

    private boolean onPageUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noProperContent())
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS);
	    return true;
	}
	if (hotPointY <= 0)
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS_ABOVE);
	    return true;
	}
	hotPointY -= environment.getAreaVisibleHeight(this);
	if (hotPointY < 0)
	    hotPointY = 0;
	hotPointX = initialHotPointX;
	environment.onAreaNewHotPoint(this);
	appearance.introduceRow(model, hotPointY, briefIntroduction?INTRODUCTION_BRIEF:0);
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	if (noProperContent())
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS);
	    return true;
	}
	final int count = model.getRowCount();
	hotPointY = count;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	environment.hintStaticString(Langs.EMPTY_LINE);
	return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	if (noProperContent())
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS);
	    return true;
	}
	hotPointY = 0;
	hotPointX = initialHotPointX;
	environment.onAreaNewHotPoint(this);
	appearance.introduceRow(model, 0, 0);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	if (noProperContent())
	{
	    environment.hintStaticString(Langs.NO_TABLE_ROWS);
	    return true;
	}
	final int count = model.getRowCount();
	if (hotPointY < 0 || hotPointY >= count)
	{
	    environment.hintStaticString(Langs.EMPTY_LINE);
	    return true;
	}
	//Checking that hot point not before proper line begin;
	if (hotPointX < initialHotPointX)
	    hotPointX = initialHotPointX;
	int currentCol = colUnderPos(hotPointX);
	//Checking that hot point not beyond proper line end;
	if (currentCol >= colWidth.length)
	{
	    currentCol = colWidth.length - 1;
	    hotPointX = initialHotPointX;
	    for(int i = 0;i < colWidth.length;++i)
		hotPointX += (colWidth[i] + 1);
	}
	//Calculating starting position of current and next cells;
	int colStartPos = initialHotPointX;
	for(int i = 0;i < currentCol;++i)
	    colStartPos += (colWidth[i] + 1);
	int nextColStartPos = colStartPos + colWidth[currentCol] + 1; 
	//Hot point isn't at the cell end;
	if (hotPointX + 2 < nextColStartPos)
	{
	    cellShift = 0;
	    final int newPos = hotPointX - colStartPos + 1;
	    String text = appearance.getCellText(model, currentCol, hotPointY);
	    if (text == null)
		text = "";
	    if (newPos <= text.length())
	    {
		//We may stay at the current cell;
		++hotPointX;
		environment.onAreaNewHotPoint(this);
		if (newPos == text.length())
		    environment.hint(colEndMessage(currentCol)); else
		    environment.sayLetter(text.charAt(newPos));
	    } else
	    {
		    //Jumping to next cell;
		//Checking if there is no next cell;
		if (currentCol + 1>= colWidth.length)
		{
		    environment.hint(colEndMessage(currentCol));
		    //hotPointX left unchanged;
		    return true;
		}
		//Jumping to next cell, it exists;
		    hotPointX = nextColStartPos;
		    String nextText = appearance.getCellText(model, currentCol + 1, hotPointY);
		    if (nextText == null)
			nextText = "";
		    if (nextText.isEmpty())
			environment.hint(colEndMessage(currentCol + 1)); else
			environment.sayLetter(nextText.charAt(0));
		    return true;
	    }
	}

	//At the last position of cell;
	if (hotPointX + 1 == nextColStartPos)
	{
	    //We should simply try to make step to next cell but it should exist;
	    cellShift = 0;
	    if (currentCol + 1 >= colWidth.length)
	    {
		environment.hint(colEndMessage(currentCol));
		return true;
	    }
	    hotPointX = nextColStartPos;
	    environment.onAreaNewHotPoint(this);
	    String text = appearance.getCellText(model, currentCol + 1, hotPointY);
	    if (text == null)
		text = "";
	    if (text.isEmpty())
		environment.hint(colEndMessage(currentCol + 1)); else
		environment.sayLetter(text.charAt(0));
	    return true;
	}
	//The only case remains unconsidered is the position just before the last position of cell;
	if (hotPointX + 2 != nextColStartPos)
	    Log.warning("table", "onArrowRight():expecting to be at he position before cel end but something wrong: hotPointY=" + hotPointY + ", nextCoLStartPos=" + nextColStartPos);
	hotPointX = nextColStartPos - 2;
	final int pos = hotPointX + cellShift - colStartPos;
	String text = appearance.getCellText(model, currentCol, hotPointY);
	if (text == null)
	    text = "";
	if (pos + 1 == text.length())
	{
	    cellShift = 0;
	    hotPointX = nextColStartPos - 1;
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    environment.hint(colEndMessage(currentCol));
	    return true;
	}
	if (pos >= text.length())
	{
	    //Trying to jump to next column if it exists;
	    cellShift = 0;
	    if (currentCol + 1 >= colWidth.length)
	    {
		environment.hint(colEndMessage(currentCol));
		return true;
	    }
	    hotPointX = nextColStartPos;
	    String nextText = appearance.getCellText(model, currentCol + 1, hotPointY);
	    if (nextText == null)
		nextText = "";
	    if (nextText.isEmpty())
		environment.hint(colEndMessage(currentCol + 1)); else
		environment.sayLetter(nextText.charAt(0));
	    return true;
	}
	//We shift current cell, there are more letters in in cell text;
	++cellShift;
	environment.sayLetter(text.charAt(pos + 1));
	environment.onAreaNewContent(this);
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	//FIXME:
	return false;
    }

    private boolean onLineEnd(KeyboardEvent event)
    {
	//FIXME:
	return false;
    }

    private boolean onLineHome(KeyboardEvent event)
    {
	//FIXME:
	return false;
    }

    @Override public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	/*
	if (model == null || model.getItemCount() == 0)
	    return false;
	if (fromY >= model.getItemCount() || toY > model.getItemCount())
	    return false;
	Vector<String> res = new Vector<String>();
	for(int i = fromY;i < toY;++i)
	{
	    final String line = getScreenAppearance(model, i, model.getItem(i), CLIPBOARD_VALUE);
	    if (line != null)
		res.add(line); else
		res.add("");
	}
	if (res.isEmpty())
	    return false;
	Luwrain.setClipboard(res.toArray(new String[res.size()]));
	*/
	return true;
    }

    @Override public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    private void copyEntireContent()
    {
	/*
	Vector<String> lines = new Vector<String>();
	int maxLen = 0;
	if (model != null && model.getItemCount() > 0)
	{
	    for(int i = 0;i < model.getItemCount();++i)
	    {
		String line = getScreenAppearance(model, i, model.getItem(i), CLIPBOARD_VALUE);
		if (line == null)
		    line = "";
		lines.add(line);
		if (line.length() > maxLen)
		    maxLen = line.length();
	    }
	} else
	    {
		lines.add(noItems);
		maxLen = noItems.length();
	    }
	String dashes = "";
	while(dashes.length() < maxLen)
	    dashes += "-";
	Vector<String> res = new Vector<String>();
	res.add(getName() != null?getName():"null");
	res.add(dashes);
	res.addAll(lines);
	Luwrain.setClipboard(res.toArray(new String[res.size()]));
	*/
    }

    private String stringOfLen(String value,
			       int requiredLen,
			       String suffixLonger,
			       String suffixEnoughRoom)
    {
	String v = value != null?value:"";
	if (v.length() > requiredLen)
	    return v.substring(0, requiredLen) + suffixLonger;
	while(v.length() < requiredLen)
	    v += " ";
	return v + suffixEnoughRoom;
    }

    private boolean noProperContent()
    {
	return model == null || model.getRowCount() <= 0 || model.getColCount() <= 0 ||
colWidth == null || colWidth.length <= 0;
    }

    private int colUnderPos(int pos)
    {
	if (colWidth == null || colWidth.length < 1)
	    return 0;
	int shift = initialHotPointX;
	for(int i = 0;i < colWidth.length;++i)
	{
	    if (pos >= shift && pos <= colWidth[i])
		return i;
	    shift += (colWidth[i] + 1);
	}
	return colWidth.length;
    }

    private String colEndMessage(int index)
    {
	return "FIXME";//FIXME:
    }
}
