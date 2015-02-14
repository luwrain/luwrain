/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.mainmenu;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
//import org.luwrain.controls.*;
import org.luwrain.util.*;

public class MainMenu  implements Area, PopupClosingRequest
{
    private Luwrain luwrain;
    public PopupClosing closing = new PopupClosing(this);
    private Strings strings;
    private Item[] items;
    private Item selectedItem;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public MainMenu(Luwrain luwrain,
		    Strings strings,
			Item[] items)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	this.items = items;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	if (items == null)
	    throw new NullPointerException("items may not be null");
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
	case KeyboardEvent.ENTER:
	    if (event.isModified())
		return false;
	    closing.doOk();
	    return true;
	case KeyboardEvent.ARROW_DOWN:
	    return onArrowDown(event);
	case KeyboardEvent.ARROW_UP:
	    return onArrowUp(event);
	case KeyboardEvent.ARROW_RIGHT:
	    return onArrowRight(event);
	case KeyboardEvent.ARROW_LEFT:
	    return onArrowLeft(event);
	case KeyboardEvent.HOME:
	    return onHome(event);
	case KeyboardEvent.END:
	    return onEnd(event);
	case KeyboardEvent.PAGE_DOWN:
	    return onPageDown(event);
	case KeyboardEvent.PAGE_UP:
	    return onPageUp(event);
	default:
	return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return closing.onEnvironmentEvent(event);
    }

    @Override public int getLineCount()
    {
	return items.length + 1;
    }

    @Override public String getLine(int index)
    {
	return index < items.length?items[index].getText():"";
    }

    @Override public String getName()
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
	    items[hotPointY].introduce();
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
	    items[hotPointY].introduce();
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
	final String line = items[hotPointY].getText();
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

    private boolean onArrowLeft(KeyboardEvent event)
    {
	//FIXME:Words jump;
	if (event.isModified())
	    return false;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = items[hotPointY].getText();
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

    private boolean onHome(KeyboardEvent event)
    {
	//FIXME:
	return false;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	//FIXME:
	return false;
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

    @Override public boolean onOk()
    {
	if (hotPointY >= items.length)
	    return false;
	if (!items[hotPointY].isAction())
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
