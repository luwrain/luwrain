/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls.block;

import java.io.*;
import java.net.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;
import org.luwrain.browser.*;
import org.luwrain.controls.*;

public class BlockArea implements Area
{
    static private final int MIN_VISIBLE_WIDTH = 20;

    public interface Appearance
    {
	void announceFirstRow(Block block, BlockRowFragment[] fragments);
	void announceRow(Block block, BlockRowFragment[] fragments);
	String getRowTextAppearance(BlockRowFragment[] fragments);
    }

    public interface ClickHandler
    {
	boolean onClick(BlockArea area, int rowIndex, BlockObject webObj);
    }

    static public class Params
    {
	public ControlContext context = null;
	public Appearance appearance;
	public ClickHandler clickHandler = null;
    }

    protected final ControlContext context;
    protected final Appearance appearance;
    protected ClickHandler clickHandler = null;
    protected View view = null;
    protected View.Iterator it = null;
    protected int rowIndex = 0;
    protected int hotPointX = 0;

    protected int itemIndex = 0;

    public BlockArea(Params params)
    {
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.context, "params.context");
	NullCheck.notNull(params.appearance, "params.appearance");
	this.context = params.context;
	this.appearance = params.appearance;
	this.clickHandler = params.clickHandler;
    }

    public void clear()
    {
	view = null;
	it = null;
    }

    public void setBlocks(Block[] blocks, int width)
    {
	NullCheck.notNull(blocks, "blocks");
	if (width < 0)
	    throw new IllegalArgumentException("width (" + width + ") may not be negative");
	if (blocks.length == 0)
	{
	    this.view = null;
	    this.it = null;
	    return;
	}
	final ViewBuilder builder = new ViewBuilder(blocks);
	this.view = builder.build(appearance, width);
	this.it = view.createIterator();
	this.rowIndex = 0;
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
	context.onAreaNewName(this);
    }

    public boolean isEmpty()
    {
	return view == null || it == null;
    }

    public BlockObject getSelectedObj()
    {
	if (isEmpty())
	    return null;
	final BlockRowFragment[] row = it.getRow(rowIndex);
	int offset = 0;
	for(int i = 0;i < row.length;++i)
	{
	    if (hotPointX >= offset && hotPointX < offset + row[i].getWidth())
		return row[i].getBlockObj();
	    offset += row[i].getWidth();
	}
	return null;
    }

    @Override public int getHotPointX()
    {
	if (isEmpty())
	    return 0;
	return it.getX() + hotPointX;
    }

    @Override public int getHotPointY()
    {
	if (isEmpty())
	    return 0;
	return it.getY() + rowIndex;
    }

    @Override public int getLineCount()
    {
	if (isEmpty())
	    return 1;
	return view.getLineCount();
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (isEmpty())
	    return (index == 0)?noContentStr():"";
	return view.getLine(index);
    }

    @Override public String getAreaName()
    {
	return "FIXME";
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		return onClick();
	    case ARROW_RIGHT:
		return onMoveRight(event);
	    case ARROW_LEFT:
		return onMoveLeft(event);
	    case ARROW_DOWN:
		return onMoveDown(event);
	    case ARROW_UP:
		return onMoveUp(event);
	    }
	return false;
    }

    protected boolean onClick()
    {
	if (isEmpty() || clickHandler == null)
	    return false;
	final BlockObject blockObj = getSelectedObj();
	if (blockObj == null)
	    return false;
	return clickHandler.onClick(this, rowIndex, blockObj);
    }

    protected boolean onMoveRight(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (noContent())
	    return true;
	final String text = appearance.getRowTextAppearance(it.getRow(rowIndex));
	if (hotPointX >= text.length())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	++hotPointX;
	if (hotPointX >= text.length())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	context.onAreaNewHotPoint(this);
	context.setEventResponse(DefaultEventResponse.letter(text.charAt(hotPointX)));
	return true;
    }

    protected boolean onMoveLeft(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (noContent())
	    return true;
	final String text = appearance.getRowTextAppearance(it.getRow(rowIndex));
	if (hotPointX == 0)
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_LINE));
	    return true;
	}
	--hotPointX;
	if (hotPointX >= text.length())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	    return true;
	}
	context.onAreaNewHotPoint(this);
	context.setEventResponse(DefaultEventResponse.letter(text.charAt(hotPointX)));
	return true;
    }

    protected boolean onMoveUp(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (noContent())
	    return true;
	if (rowIndex > 0)
	{
	    --rowIndex;
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    announceRow();
	    return true;
	}
	if (!it.movePrev())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_ABOVE));
	    return true;
	}
	final int count = it.getRowCount();
	rowIndex = count > 0?count - 1:0;
	hotPointX = 0;
	context.onAreaNewHotPoint(this);
	announceRow();
	return true;
    }

    protected boolean onMoveDown(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (noContent())
	    return true;
	if (!it.isLastRow(rowIndex))
	{
	    ++rowIndex;
	    hotPointX = 0;
	    context.onAreaNewHotPoint(this);
	    announceRow();
	    return true;
	}
	if (!it.moveNext())
	{
	    context.setEventResponse(DefaultEventResponse.hint(Hint.NO_ITEMS_BELOW));
	    return true;
	}
	rowIndex = 0;
	hotPointX = 0;
	context.onAreaNewHotPoint(this);
	announceRow();
	return true;
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	return false;
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	return false;
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    public void announceRow()
    {
	if (isEmpty())
	    return;
	if (rowIndex == 0)
	    appearance.announceFirstRow(it.getBlock(), it.getRow(rowIndex)); else
	    appearance.announceRow(it.getBlock(), it.getRow(rowIndex));
    }

    protected String noContentStr()
    {
	return context.getStaticStr("NoContent");
    }

    protected void noContentMsg()
    {
	context.setEventResponse(DefaultEventResponse.hint(Hint.NO_CONTENT));
    }

    protected boolean noContent()
    {
	if (isEmpty())
	{
	    noContentMsg();
	    return true;
	}
	return false;
    }
}
