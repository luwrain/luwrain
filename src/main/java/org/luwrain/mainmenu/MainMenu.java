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

package org.luwrain.mainmenu;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class MainMenu  implements Area, PopupClosingRequest, RegionProvider
{
    private Luwrain luwrain;
    final public PopupClosing closing = new PopupClosing(this);
    private Strings strings;
    private Item[] items;
    private Item selectedItem;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private final Region region = new Region(this);

    public MainMenu(Luwrain luwrain,
		    Strings strings,
			Item[] items)
    {
	this.luwrain = luwrain;
	//	this.commandEnv = commandEnv;
	this.strings = strings;
	this.items = items;
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	NullCheck.notNull(items, "items");
	for(int i = 0;i < items.length;++i)
	    if (items[i] == null)
		throw new NullPointerException("items[" + i + "] may not be null");
	hotPointX = 0;
	hotPointY = 0;
	while(hotPointY < items.length && !isDefaultSeparator(items[hotPointY]))
	    ++hotPointY;
	if (hotPointY >= items.length)
	    hotPointY = 0;
    }

    public Item getSelectedItem()
    {
	return selectedItem;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (closing.onKeyboardEvent(event))
	    return true;
	if (!event.isCommand())
	    return false;
	switch(event.getCommand())
	{
	case ENTER:
	    if (event.isModified())
		return false;
	    closing.doOk();
	    return true;
	case ARROW_DOWN:
	    return onArrowDown(event);
	case  ARROW_UP:
	    return onArrowUp(event);
	case ARROW_RIGHT:
	    return onArrowRight(event);
	case ARROW_LEFT:
	    return onArrowLeft(event);
	case HOME:
	    return onHome(event);
	case END:
	    return onEnd(event);
	case PAGE_DOWN:
	    return onPageDown(event);
	case PAGE_UP:
	    return onPageUp(event);
	case ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	case ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case ALTERNATIVE_HOME:
	    return onAltHome(event);
	case ALTERNATIVE_END:
	    return onAltEnd(event);
	default:
	return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	switch(event.getCode())
	{
	case INTRODUCE:
	    luwrain.say(getAreaName());
	    return true;
	default:
	    if (region.onEnvironmentEvent(event, hotPointX, hotPointY))
		return true;
	    return closing.onEnvironmentEvent(event);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return region.onAreaQuery(query, hotPointX, hotPointY);
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public int getLineCount()
    {
	return items.length + 1;
    }

    @Override public String getLine(int index)
    {
	return index < items.length?items[index].getMMItemText():"";
    }

    @Override public String getAreaName()
    {
	return strings.areaName();
    }

    @Override public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    @Override public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    private boolean onArrowDown(KeyboardEvent event)
    {
	if (event.isModified())
	    return false;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.NO_ITEMS_BELOW);
	    return true;
	}
	++hotPointY;
	hotPointX = 0;
	luwrain.onAreaNewHotPoint(this);
	if (hotPointY >= items.length)
	{
	    luwrain.silence();
	    luwrain.playSound(Sounds.MAIN_MENU_EMPTY_LINE);
	} else
	    items[hotPointY].introduceMMItem(luwrain);//FIXME:Interface for the particular extension;
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event)
    {
	if (event.isModified())
	    return false;
	if (hotPointY <= 0)
	{
	    luwrain.hint(Hints.NO_ITEMS_ABOVE);
	    return true;
	}
	--hotPointY;
	hotPointX = 0;
	luwrain.onAreaNewHotPoint(this);
	if (hotPointY < items.length)
	    items[hotPointY].introduceMMItem(luwrain); //FIXME:Interface for the particular extension
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	//FIXME:Words jump;
	if (event.isModified())
	    return false;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = items[hotPointY].getMMItemText();
	if (line == null || line.isEmpty())
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= line.length())
	{
	    luwrain.hint(Hints.END_OF_LINE);
	    return true;
	}
	++hotPointX;
	luwrain.onAreaNewHotPoint(this);
	if (hotPointX >= line.length())
	    luwrain.hint(Hints.END_OF_LINE); else
	    luwrain.sayLetter(line.charAt(hotPointX));
	return true;
    }

    private boolean onAltRight(KeyboardEvent event)
    {
	if (event.isModified())
	    return false;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = items[hotPointY].getMMItemText();
	if (line == null || line.isEmpty())
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= line.length())
	{
	    luwrain.hint(Hints.END_OF_LINE);
	    return true;
	}
	WordIterator it = new WordIterator(line, hotPointX);
	if (!it.stepForward())
	{
	    luwrain.hint(Hints.END_OF_LINE);
	    return true;
	}
	hotPointX = it.pos();
	if (it.announce().length() > 0)
	    luwrain.say(it.announce()); else
	    luwrain.hint(Hints.END_OF_LINE);
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (event.isModified())
	    return false;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = items[hotPointY].getMMItemText();
	if (line == null || line.isEmpty())
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX <= 0)
	{
	    luwrain.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	--hotPointX;
	luwrain.onAreaNewHotPoint(this);
	if (hotPointX < line.length())
	    luwrain.sayLetter(line.charAt(hotPointX));
	return true;
    }

    private boolean onAltLeft(KeyboardEvent event)
    {
	if (event.isModified())
	    return false;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = items[hotPointY].getMMItemText();
	if (line == null || line.isEmpty())
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	WordIterator it = new WordIterator(line, hotPointX);
	if (!it.stepBackward())
	{
	    luwrain.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = it.pos();
	luwrain.say(it.announce());
	luwrain.onAreaNewHotPoint(this);
	    return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	hotPointX = 0;
	hotPointY = 0;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Item item = items[hotPointY];
	if (item == null || item.getMMItemText() == null || item.getMMItemText().isEmpty())
	    luwrain.hint(Hints.EMPTY_LINE); else
	    luwrain.say(item.getMMItemText());
	return true;
    }

    private boolean onAltHome(KeyboardEvent event)
    {
	if (event.isModified())
	    return false;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = items[hotPointY].getMMItemText();
	if (line == null || line.isEmpty())
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	hotPointX = 0;
	if (!line.isEmpty())
	    luwrain.sayLetter(line.charAt(0)); else
	    luwrain.hint(Hints.END_OF_LINE);//Actually never happens;
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	if (event.isModified())
	    return false;
	hotPointY = items.length;
	hotPointX = 0;
	luwrain.hint(Hints.EMPTY_LINE);
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltEnd(KeyboardEvent event)
    {
	if (event.isModified())
	    return false;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = items[hotPointY].getMMItemText();
	if (line == null || line.isEmpty())
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	hotPointX = line.length();
	luwrain.hint(Hints.END_OF_LINE);
	luwrain.onAreaNewHotPoint(this);
	    return true;
    }


    private boolean onPageDown(KeyboardEvent event)
    {
	//FIXME:
	return false;
    }

    private boolean onPageUp(KeyboardEvent event)
    {
	//FIXME:
	return false;
    }

    @Override public HeldData getWholeRegion()
    {
	if (items == null || items.length < 1)
	    return null;
	final LinkedList<String> res = new LinkedList<String>();
	for(Item i: items)
	{
	    final String line = i.getMMItemText();
	    res.add(line != null?line:"");
	}
	res.add("");
	return new HeldData(res.toArray(new String[res.size()]));
    }

    @Override public HeldData getRegion(int fromX, int fromY,
					int toX, int toY)
    {
	if (items == null || items.length < 1)
	    return null;
	if (fromY >= items.length || toY > items.length)
	    return null;
	if (fromY == toY)
	{
	    final String line = items[fromY].getMMItemText();
	    if (line.isEmpty())
		return null;
	    final int fromPos = fromX < line.length()?fromX:line.length();
	    final int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		return null;
	    return new HeldData(new String[]{line.substring(fromPos, toPos)});
	}
	final LinkedList<String> res = new LinkedList<String>();
	for(int i = fromY;i < toY;++i)
	{
	    final String line = items[i].getMMItemText();
	    res.add(line != null?line:"");
	}
	res.add("");
	return new HeldData(res.toArray(new String[res.size()]));
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
					  HeldData data)
    {
	return false;
    }

    @Override public boolean onOk()
    {
	if (hotPointY >= items.length)
	    return false;
	if (!items[hotPointY].isMMAction())
	    return false;
	selectedItem = items[hotPointY];
	return true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    private boolean isDefaultSeparator(Item item)
    {
	if (!(item instanceof Separator))
	    return false;
	Separator sep = (Separator)item;
	return sep.isDefaultSeparator();
    }
}
