/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import org.luwrain.util.*;

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

    private ControlEnvironment environment;
    private CopyCutInfo copyCutInfo = new CopyCutInfo(this);;
    private String name = "";
    private TableModel model;
    private TableAppearance appearance;
    private TableClickHandler clickHandler;

    private int initialHotPointX = 0;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private int[] colWidth;
    private int cellShift = 0;

    public TableArea(ControlEnvironment environment, TableModel model)
    {
	this.environment = environment;
	this.model = model;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
	this.appearance = new DefaultTableAppearance(environment);
	this.initialHotPointX = appearance.getInitialHotPointX(model);
	//	refresh();
    }

    public TableArea(ControlEnvironment environment,
		     TableModel model,
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
	//	refresh();
    }

    public TableArea(ControlEnvironment environment,
		     TableModel model,
		    TableAppearance appearance,
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
	//	if (clickHandler == null)
	//	    throw new NullPointerException("clickHandler may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
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

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
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
	if (event == null)
	    throw new NullPointerException("event may not be null");
	switch (event.getCode())
	{
	case EnvironmentEvent.REFRESH:
	    refresh();
	    return true;
	case EnvironmentEvent.COPY_CUT_POINT:
	    return copyCutInfo.copyCutPoint(hotPointX, hotPointY);
	case EnvironmentEvent.COPY:
	    if (!copyCutInfo.copy(hotPointX, hotPointY))
		copyEntireContent();
	    return true;
	default:
	    return false;
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
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
	    return index <= 0?environment.staticStr(LangStatic.TABLE_NO_ROWS):"";
	if (index < 0 || index >= model.getRowCount())
	    return "";
	final int currentCol = colUnderPos(hotPointX);
	    String line = stringOfLen(appearance.getRowPrefix(model, index), initialHotPointX, "", "");
	if (index != hotPointY || currentCol < 0)
	{
	    for(int i = 0;i < model.getColCount();++i)
		line += stringOfLen(appearance.getCellText(model, i, index), colWidth[i], ">", " ");
	    return line;
	}
	//	System.out.println("colWidth.lenght=" + colWidth.length);
	System.out.println("hotPointX=" + hotPointX);

	System.out.println("currentCol=" + currentCol);
	if (currentCol > 0)
	    for(int i = 0;i < currentCol;++i)
		line += stringOfLen(appearance.getCellText(model, i, index), colWidth[i], ">", " ");
	    String currentColText = appearance.getCellText(model, currentCol, index);
	    System.out.println("currentColText=" + currentColText);
	    if (cellShift > 0 && cellShift < currentColText.length())
	    currentColText = currentColText.substring(cellShift);
		line += stringOfLen(currentColText, colWidth[currentCol], ">", " ");
		if (currentCol + 1 < colWidth.length)
		    for(int i = currentCol + 1;i < colWidth.length;++i)
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
	    environment.hint(Hints.TABLE_NO_ROWS_BELOW);
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
	    environment.hint(Hints.TABLE_NO_ROWS_ABOVE);
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
	    environment.hint(Hints.TABLE_NO_ROWS_BELOW);
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
	    environment.hint(Hints.TABLE_NO_ROWS_ABOVE);
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
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	//Checking that hot point not before proper line begin;
	if (hotPointX < initialHotPointX)
	    hotPointX = initialHotPointX;
	int currentCol = colUnderPos(hotPointX);
	//Checking that hot point not beyond proper line end;
	if (currentCol >= colWidth.length)
	{
	    //	    System.out.println("here");
	    currentCol = colWidth.length - 1;
	    hotPointX = initialHotPointX;
	    for(int i = 0;i < colWidth.length;++i)
		hotPointX += (colWidth[i] + 1);
	}
	//Calculating starting position of current and next cells;
	int colStartPos = initialHotPointX;
	for(int i = 0;i < currentCol;++i)
	    colStartPos += (colWidth[i] + 1);
	final int nextColStartPos = colStartPos + colWidth[currentCol] + 1; 
	final int currentColWidth = colWidth[currentCol];
	//	System.out.println("currentCol=" + currentCol);
	//	System.out.println("colStartPos=" + colStartPos);
	//	System.out.println("nextColStartPos=" + nextColStartPos);
	//	System.out.println("currentColWidth=" + currentColWidth);
	final TableCell c = new TableCell(hotPointX - colStartPos, cellShift, currentColWidth, appearance.getCellText(model, currentCol, hotPointY));
	if (!c.moveNext())
	{
	    cellShift = 0;
	    hotPointX = nextColStartPos;
	    final String nextColText = appearance.getCellText(model, currentCol + 1, hotPointY);
	    if (!nextColText.isEmpty())
		environment.sayLetter(nextColText.charAt(0)); else
		environment.hint(Hints.TABLE_END_OF_COL);
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    return true;
	    }
	cellShift = c.shift;
	//	System.out.println("cellShift=" + cellShift);
	hotPointX = c.pos + colStartPos;
	System.out.println("hotPointX=" + hotPointX);
	if (c.pos == c.width)
	    environment.hint(Hints.TABLE_END_OF_COL); else
	    environment.sayLetter(c.line.charAt(c.pos + c.shift));
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
		    return true;
	    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	//FIXME:
	return false;
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
	environment.onAreaNewHotPoint(this);
	if (hotPointY < count)
	    appearance.introduceRow(model, hotPointY, briefIntroduction?INTRODUCTION_BRIEF:0); else
	    environment.hint(Hints.EMPTY_LINE);
    }

    @Override public boolean onCopyAll()
    {
	return false; 
    }

    @Override public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    @Override public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	return false;
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
	environment.hint(Hints.TABLE_NO_ROWS);
	return true;
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




    private int colUnderPos(int pos)
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
}
