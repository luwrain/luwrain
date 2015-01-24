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
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class MainMenu  implements Area, PopupClosingRequest
{
    private ControlEnvironment environment;
    public PopupClosing closing = new PopupClosing(this);
    private StringConstructor stringConstructor;
    private Item[] items;
    private Item selectedItem;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public MainMenu(ControlEnvironment environment,
			StringConstructor stringConstructor,
			Item[] items)
    {
	this.environment = environment;
	this.stringConstructor = stringConstructor;
	this.items = items != null?items:new Item[0];
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
	return stringConstructor.mainMenuTitle();
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
	    environment.hint(stringConstructor.mainMenuNoItemsBelow());
	    return true;
	}
	++hotPointY;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	if (hotPointY < items.length)
	    items[hotPointY].introduce();
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event)
    {
	if (event.isModified())
	    return false;
	if (hotPointY <= 0)
	{
	    environment.hint(stringConstructor.mainMenuNoItemsAbove());
	    return true;
	}
	--hotPointY;
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	if (hotPointY < items.length)
	    items[hotPointY].introduce();
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	//FIXME:Words jump;
	if (event.isModified())
	    return false;
	if (hotPointY < 0 ||
	    hotPointY >= items.length ||
	    items[hotPointY] == null)
	{
	    environment.hintStaticString(Langs.EMPTY_LINE);
	    return true;
	}
	final String line = items[hotPointY].getText();
	if (line == null || line.isEmpty())
	{
	    environment.hintStaticString(Langs.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= line.length())
	{
	    environment.hintStaticString(Langs.END_OF_LINE);
	    return true;
	}
	++hotPointX;
	environment.onAreaNewHotPoint(this);
	if (hotPointX >= line.length())
	    environment.hintStaticString(Langs.END_OF_LINE); else
	    Speech.sayLetter(line.charAt(hotPointX));
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	//FIXME:Words jump;
	if (event.isModified())
	    return false;
	if (hotPointY < 0 ||
	    hotPointY >= items.length ||
	    items[hotPointY] == null)
	{
	    environment.hintStaticString(Langs.EMPTY_LINE);
	    return true;
	}
	final String line = items[hotPointY].getText();
	if (line == null || line.isEmpty())
	{
	    environment.hintStaticString(Langs.EMPTY_LINE);
	    return true;
	}
	if (hotPointX <= 0)
	{
	    environment.hintStaticString(Langs.BEGIN_OF_LINE);
	    return true;
	}
	--hotPointX;
	    environment.onAreaNewHotPoint(this);
	    if (hotPointX < line.length())
		environment.sayLetter(line.charAt(hotPointX));
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
	if (items == null ||
	    hotPointY >= items.length ||
	    !items[hotPointY].isAction())
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
	if (item == null || !(item instanceof Item))
	    return false;
	Separator sep = (Separator)item;
	return sep.isDefaultSeparator();
    }
}
