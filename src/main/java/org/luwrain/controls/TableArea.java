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

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

/**
 * The area class with table behaviour.  The data to be provided is
 * requested from the instance of TableModel interface which can be
 * either standard or custom. Application developers can make their own
 * appearance of table content through extending the TableAppearance
 * interface.
 */
public class TableArea  implements Area
{
    static final public int INTRODUCTION_BRIEF = 1;

public interface Model
{
    int getRowCount();
    int getColCount();
    Object getCell(int col, int row);
    Object getRow(int index);
    Object getCol(int index);
    void refresh();
}

public interface Appearance
{
    void announceRow(TableArea.Model model, int index, int flags);
    int getInitialHotPointX(TableArea.Model model);
    String getCellText(TableArea.Model model, int col, int row);
    String getRowPrefix(TableArea.Model model, int index);
int getColWidth(TableArea.Model model, int  colIndex);
}

    protected final ControlEnvironment environment;
    protected final RegionPoint regionPoint = new RegionPoint();
    protected final ClipboardTranslator clipboardTranslator;
    protected String name = "";
    protected final Model model;
    protected final TableArea.Appearance appearance;
    protected TableClickHandler clickHandler = null;

    private int initialHotPointX = 0;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private int[] colWidth;
    private int cellShift = 0;

    public TableArea(ControlEnvironment environment, TableArea.Model model)
    {
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(model, "model");
	this.environment = environment;
	this.model = model;
	this.appearance = new DefaultTableAppearance(environment);
	this.initialHotPointX = appearance.getInitialHotPointX(model);
	this.clipboardTranslator = new ClipboardTranslator(new LinesClipboardProvider(this, ()->environment.getClipboard()), regionPoint, EnumSet.noneOf(ClipboardTranslator.Flags.class));
	//	refresh();
    }

    public TableArea(ControlEnvironment environment,
		     TableArea.Model model,
		     String name)
    {
	this.environment = environment;
	this.model = model;
	this.name = name;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	this.appearance = new DefaultTableAppearance(environment);
	this.initialHotPointX = appearance.getInitialHotPointX(model);
	this.clipboardTranslator = new ClipboardTranslator(new LinesClipboardProvider(this, ()->environment.getClipboard()), regionPoint, EnumSet.noneOf(ClipboardTranslator.Flags.class));
	//	refresh();
    }

    public TableArea(ControlEnvironment environment,
		     TableArea.Model model,
		    TableArea.Appearance appearance,
		     TableClickHandler clickHandler,
		     String name)
    {
	this.environment = environment;
	this.model = model;
	this.appearance = appearance != null?appearance:new DefaultTableAppearance(environment);
	this.name = name != null?name:"";
	this.clickHandler = clickHandler;
	this.initialHotPointX = appearance.getInitialHotPointX(model);
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
	if (appearance == null)
	    throw new NullPointerException("appearance may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	this.clipboardTranslator = new ClipboardTranslator(new LinesClipboardProvider(this, ()->environment.getClipboard()), regionPoint, EnumSet.noneOf(ClipboardTranslator.Flags.class));
	//	refresh();
    }

    public void refresh()
    {
	refresh(true);
    }

    public void refresh(boolean refreshModel)
    {
	if (model == null)
	{
	    colWidth = null;
	    cellShift = 0;
	    hotPointX = 0;
	    hotPointY = 0;
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	if (refreshModel)
	    model.refresh();
	final int colCount = model.getColCount();
	final int rowCount = model.getRowCount();
	if (colCount <= 0 || rowCount <= 0)
	{
	    colWidth = null;
	    cellShift = 0;
	    hotPointX = 0;
	    hotPointY = 0;
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	initialHotPointX = appearance.getInitialHotPointX(model);
	colWidth = new int[colCount];
	int totalWidth = initialHotPointX;
	for(int i = 0;i < colCount;++i)
	{
	    final int width = appearance.getColWidth(model, i);
	    colWidth[i] =  width >= 1?width:1;
	    totalWidth += (colWidth[i] + 1);
	}
	if (hotPointY > rowCount)
	    hotPointY = rowCount;
	if (hotPointY < rowCount && hotPointX >= totalWidth )
	    hotPointX = totalWidth - 1; //totalWidth may not be zero as always we have at least one column here;
	if (hotPointY == rowCount)
	    hotPointX = 0;
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
    }

    public Object getSelectedRow()
    {
	final int index = getSelectedRowIndex();
	if (index < 0)
	    return null;
	return model.getRow(index);
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
	return hotPointY < model.getRowCount()?hotPointY:-1;
    }

    public int getSelectedColIndex()
    {
	//FIXME:
	return -1;
    }

    public void setClickHandler(TableClickHandler clickHandler)
    {
	this.clickHandler = clickHandler;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (!event.isSpecial() || event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case ARROW_DOWN:
	    return onArrowDown(event, false);
	case ARROW_UP:
	    return onArrowUp(event, false);
	case ARROW_RIGHT:
	    return onArrowRight(event);
	case ARROW_LEFT:
	    return onArrowLeft(event);
	case ALTERNATIVE_ARROW_DOWN:
	    return onArrowDown(event, true);
	case ALTERNATIVE_ARROW_UP:
	    return onArrowUp(event, true);
	    //FIXME:case KeyboardEvent.ALTERNATIVE_ARROW_RIGHT:
	    //FIXME:case KeyboardEvent.ALTERNATIVE_ARROW_LEFT:
	case HOME:
	    return onHome(event);
	case END:
	    return onEnd(event);
	case ALTERNATIVE_HOME:
	    return onLineHome(event);
	case ALTERNATIVE_END:
	    return onLineEnd(event);
	case PAGE_DOWN:
	    return onPageDown(event, false);
	case PAGE_UP:
	    return onPageUp(event, false);
	case ALTERNATIVE_PAGE_DOWN:
	    return onPageDown(event, true);
	case ALTERNATIVE_PAGE_UP:
	    return onPageUp(event, true);
	case ENTER:
	    if (noProperContent() || clickHandler == null ||
		hotPointY < 0 || hotPointY >= model.getRowCount() ||
		getColUnderPos(hotPointX) < 0)
		return false;
	    return clickHandler.onClick(model, getColUnderPos(hotPointX), hotPointY, model.getCell(getColUnderPos(hotPointX), hotPointY));
	default:
	    return false;
	}
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	switch (event.getCode())
	{
	case REFRESH:
	    refresh();
	    return true;
	default:
	    return clipboardTranslator.onSystemEvent(event, hotPointX, hotPointY);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
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
	    return index <= 0?environment.staticStr(LangStatic.TABLE_NO_CONTENT):"";
	if (index < 0 || index >= model.getRowCount())
	    return "";
	final int currentCol = getColUnderPos(hotPointX);
	    String line = getStringOfLen(appearance.getRowPrefix(model, index), initialHotPointX, "", "");
	if (index != hotPointY || currentCol < 0)
	{
	    for(int i = 0;i < model.getColCount();++i)
		line += getStringOfLen(appearance.getCellText(model, i, index), colWidth[i], ">", " ");
	    return line;
	}
	if (currentCol > 0)
	    for(int i = 0;i < currentCol;++i)
		line += getStringOfLen(appearance.getCellText(model, i, index), colWidth[i], ">", " ");
	    String currentColText = appearance.getCellText(model, currentCol, index);
	    if (cellShift > 0 && cellShift < currentColText.length())
	    currentColText = currentColText.substring(cellShift);
		line += getStringOfLen(currentColText, colWidth[currentCol], ">", " ");
		if (currentCol + 1 < colWidth.length)
		    for(int i = currentCol + 1;i < colWidth.length;++i)
			line += getStringOfLen(appearance.getCellText(model, i, index), colWidth[i], ">", " ");
    return line;
    }

    @Override public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    @Override public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    @Override public String getAreaName()
    {
	return name;
    }

    public void setName(String value)
    {
	if (value == null)
	    throw new NullPointerException("value may not be null");
	name = value;
	environment.onAreaNewName(this);
    }

    private boolean onArrowDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getRowCount();
	if (hotPointY >= count)
	{
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.TABLE_NO_ROWS_BELOW));
		return true;
	}
	++hotPointY;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getRowCount();
	if (hotPointY <= 0)
	{
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.TABLE_NO_ROWS_ABOVE));
	    return true;
	}
	--hotPointY;
	if (hotPointY >= count)
		hotPointY = count - 1;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onPageDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getRowCount();
	if (hotPointY >= count)
	{
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.TABLE_NO_ROWS_BELOW));
		return true;
	}
	hotPointY += environment.getAreaVisibleHeight(this);
	if (hotPointY >= count)
	    hotPointY = count;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onPageUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0)
	{
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.TABLE_NO_ROWS_ABOVE));
	    return true;
	}
	hotPointY -= environment.getAreaVisibleHeight(this);
	if (hotPointY < 0)
	    hotPointY = 0;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getRowCount();
	hotPointY = count;
	onNewHotPointY(false);
	return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	hotPointY = 0;
	onNewHotPointY(false);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getRowCount();
	if (hotPointY < 0 || hotPointY >= count)
	{
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	//Checking that hot point not before proper line begin;
	if (hotPointX < initialHotPointX)
	    hotPointX = initialHotPointX;
	if (getColUnderPos(hotPointX) < 0)
	    hotPointX = initialHotPointX;
	final int currentCol = getColUnderPos(hotPointX);
	final int currentColWidth = colWidth[currentCol];
	final int colStartPos = getColStartPos(currentCol);
	final int nextColStartPos = colStartPos + colWidth[currentCol] + 1; 
	final TableCell c = new TableCell(hotPointX - colStartPos, cellShift, currentColWidth, appearance.getCellText(model, currentCol, hotPointY));
	if (!c.moveNext())
	{
	    if (currentCol + 1 >= colWidth.length)
	    {
		environment.setEventResponse(DefaultEventResponse.hint(Hint.TABLE_END_OF_ROW));
		return true;
	    }
	    cellShift = 0;
	    hotPointX = nextColStartPos;
	    final String nextColText = appearance.getCellText(model, currentCol + 1, hotPointY);
	    if (!nextColText.isEmpty())
		environment.sayLetter(nextColText.charAt(0)); else
		environment.setEventResponse(DefaultEventResponse.hint(currentCol + 2 < colWidth.length?Hint.TABLE_END_OF_COL:Hint.TABLE_END_OF_ROW));
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    return true;
	    }
	cellShift = c.shift;
	hotPointX = c.pos + colStartPos;
	if (c.pos + c.shift >= c.line.length())
	    environment.setEventResponse(DefaultEventResponse.hint(currentCol + 1 < colWidth.length?Hint.TABLE_END_OF_COL:Hint.TABLE_END_OF_ROW)); else
	    environment.sayLetter(c.line.charAt(c.pos + c.shift));
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
		    return true;
	    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getRowCount();
	if (hotPointY < 0 || hotPointY >= count)
	{
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
	    return true;
	}
	if (hotPointX < initialHotPointX)
	    hotPointX = initialHotPointX;
	if (getColUnderPos(hotPointX) < 0)
	    hotPointX = initialHotPointX;
	final int currentCol = getColUnderPos(hotPointX);
	final int currentColWidth = colWidth[currentCol];
	final int colStartPos = getColStartPos(currentCol);
	final TableCell c = new TableCell(hotPointX - colStartPos, cellShift, currentColWidth, appearance.getCellText(model, currentCol, hotPointY));
	if (!c.movePrev())
	{
	    if (currentCol <= 0)
	    {
		environment.setEventResponse(DefaultEventResponse.hint(Hint.TABLE_BEGIN_OF_ROW));
		return true;
	    }
	    final String prevColText = appearance.getCellText(model, currentCol - 1, hotPointY);
	    final int prevColWidth = colWidth[currentCol - 1];
	    final int prevColStartPos = getColStartPos(currentCol - 1);
	    if (prevColText.length() > prevColWidth)
	    {
		hotPointX = prevColStartPos + prevColWidth;
		cellShift = prevColText.length() - prevColWidth;
	    } else
	    {
		cellShift = 0;
		hotPointX = prevColStartPos + prevColText.length();
	    }
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.TABLE_END_OF_COL));
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    return true;
	    }
	cellShift = c.shift;
	hotPointX = c.pos + colStartPos;
	if (c.pos == c.width)//Should never happen;
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.TABLE_END_OF_COL)); else
	    environment.sayLetter(c.line.charAt(c.pos + c.shift));
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
		    return true;
    }

    private boolean onLineEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	//FIXME:
	return false;
    }

    private boolean onLineHome(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	//FIXME:
	return false;
    }

    private void onNewHotPointY(boolean briefIntroduction)
    {
	final int count = model.getRowCount();
	hotPointX = hotPointY < count?initialHotPointX:0;
	cellShift = 0;
	environment.onAreaNewHotPoint(this);
	if (hotPointY < count)
	    appearance.announceRow(model, hotPointY, briefIntroduction?INTRODUCTION_BRIEF:0); else
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
    }

    private void copyEntireContent()
    {
    }

    private boolean noProperContent()
    {
	return model == null || model.getRowCount() <= 0 || model.getColCount() <= 0 ||
	colWidth == null || colWidth.length <= 0;
    }

    private boolean noContentCheck()
    {
	if (!noProperContent())
	    return false;
	environment.setEventResponse(DefaultEventResponse.hint(Hint.NO_CONTENT, environment.staticStr(LangStatic.TABLE_NO_CONTENT)));
	return true;
	}

    private String getStringOfLen(String value,
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

    private int getColUnderPos(int pos)
    {
	if (hotPointX < initialHotPointX)
	    return -1;
	if (colWidth == null || colWidth.length < 1)
	    return 0;
	int shift = initialHotPointX;
	for(int i = 0;i < colWidth.length;++i)
	{
	    if (pos >= shift && pos <= shift + colWidth[i])
		return i;
	    shift += (colWidth[i] + 1);
	}
	return -1;
    }

    private int getColStartPos(int colIndex)
    {
	int value = initialHotPointX;
	for(int i = 0;i < colIndex;++i)
	    value += (colWidth[i] + 1);
	return value;
    }
}
