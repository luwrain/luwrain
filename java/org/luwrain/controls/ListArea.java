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

//FIXME:fixHotPoint();

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public abstract class ListArea  implements Area
{
    private String noItemsAbove = Langs.staticValue(Langs.LIST_AREA_BEGIN);
    private String noItemsBelow = Langs.staticValue(Langs.LIST_AREA_END);
    private String emptyLine = Langs.staticValue(Langs.EMPTY_LINE);
    private String beginOfLine = Langs.staticValue(Langs.BEGIN_OF_LINE);
    private String endOfLine = Langs.staticValue(Langs.END_OF_LINE);
    private String noItems = Langs.staticValue(Langs.LIST_NO_ITEMS);

    private ListModel model;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public ListArea(ListModel model)
    {
	this.model = model;
    }

    public Object getSelectedObject()
    {
	return hotPointY < model.getItemCount()?model.getItem(hotPointY):null;
    }

    public int getSelectedIndex()
    {
	if (model == null ||
hotPointY < 0 ||
	    hotPointY >= model.getItemCount())
	    return -1;
	return hotPointY;
    }

    public void setSelectedIndex(int index, boolean introduce)
    {
	if (index < 0)
	    hotPointY = 0; else
	    if (index >= model.getItemCount())
		hotPointY = model.getItemCount(); else
		hotPointY = index;
	hotPointX = 0;
	if (introduce)
	    introduceLine(hotPointY);
	Dispatcher.onAreaNewHotPoint(this);
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	//Maybe it is not good idea to do this for all events;
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	}

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_DOWN &&
	    !event.isModified())
	{
	    if (hotPointY >= model.getItemCount())
	    {
		Speech.say(noItemsBelow, Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY++;
	    hotPointX = 0;
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceLine(hotPointY);
	    return true;
	}

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_UP &&
	    !event.isModified())
	{
	    if (hotPointY <= 0)
	    {
		Speech.say(noItemsAbove, Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY--;
	    hotPointX = 0;
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceLine(hotPointY);
	    return true;
	}

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_RIGHT &&
	    !event.isModified())
	{
	    if (hotPointY < 0 ||
		hotPointY >= model.getItemCount() ||
		model.getItem(hotPointY) == null ||
		model.getItem(hotPointY).toString() == null  ||
		model.getItem(hotPointY).toString().isEmpty())
	    {
		Speech.say(emptyLine, Speech.PITCH_HIGH);
		return true;
	    }
		final String line = model.getItem(hotPointY).toString();
	    if (hotPointX >= line.length())
	    {
		Speech.say(endOfLine, Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX++;
	    Dispatcher.onAreaNewHotPoint(this);
	    if (hotPointX >= line.length())
		Speech.say(endOfLine, Speech.PITCH_HIGH); else
		Speech.sayLetter(line.charAt(hotPointX));
	    return true;
	}

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_LEFT &&
	    !event.isModified())
	{
	    if (hotPointY < 0 ||
		hotPointY >= model.getItemCount() ||
		model.getItem(hotPointY) == null ||
		model.getItem(hotPointY).toString() == null  ||
		model.getItem(hotPointY).toString().isEmpty())
	    {
		Speech.say(emptyLine, Speech.PITCH_HIGH);
		return true;
	    }
		final String line = model.getItem(hotPointY).toString();
	    if (hotPointX <= 0)
	    {
		Speech.say(beginOfLine, Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX--;
	    Dispatcher.onAreaNewHotPoint(this);
	    if (hotPointX < line.length())
		Speech.sayLetter(line.charAt(hotPointX));
	    return true;
	}

	//FIXME:home-end;
	//FIXME:page up-page down;

	return false;
    }

    public int getLineCount()
    {
	if (model == null || model.getItemCount() <= 0)
	    return 2;
	return model.getItemCount() + 1;
    }

    public String getLine(int index)
    {
	if (model == null || 
	    model.getItemCount() < 1)
	    return index == 0?noItems:"";
	if (index < 0 ||
	    index >= model.getItemCount() ||
	    model.getItem(index) == null ||
	    model.getItem(index).toString() == null)
	    return "";
	return model.getItem(index).toString();
    }

    public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    private void introduceLine(int index)
    {
	if (model == null)
	    return;
	if (hotPointY >= model.getItemCount() || 
	    model.getItem(index) == null ||
	    model.getItem(index).toString() == null ||
	    model.getItem(index).toString().trim().isEmpty())
	{
	    Speech.say(emptyLine, Speech.PITCH_HIGH);
	    return;
	}
	Speech.say(model.getItem(index).toString());
    }

}
