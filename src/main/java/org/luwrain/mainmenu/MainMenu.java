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

package org.luwrain.mainmenu;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class MainMenu  implements Area, PopupClosingRequest, CopyCutRequest
{
    private Luwrain luwrain;
    private CommandEnvironment commandEnv;
    public PopupClosing closing = new PopupClosing(this);
    private Strings strings;
    private Item[] items;
    private Item selectedItem;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private CopyCutInfo copyCutInfo = new CopyCutInfo(this);

    public MainMenu(Luwrain luwrain,
		    CommandEnvironment commandEnv,
		    Strings strings,
			Item[] items)
    {
	this.luwrain = luwrain;
	this.commandEnv = commandEnv;
	this.strings = strings;
	this.items = items;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (commandEnv == null)
	    throw new NullPointerException("commandEnv may not be null");
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
	case KeyboardEvent.ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	case KeyboardEvent.ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case KeyboardEvent.ALTERNATIVE_HOME:
	    return onAltHome(event);
	case KeyboardEvent.ALTERNATIVE_END:
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
	case EnvironmentEvent.INTRODUCE:
	    luwrain.say(getAreaName());
	    return true;
	case EnvironmentEvent.REGION_POINT:
	    return copyCutInfo.copyCutPoint(hotPointX, hotPointY);
	case EnvironmentEvent.COPY:
	    return copyCutInfo.copy(hotPointX, hotPointY);
	default:
	    return closing.onEnvironmentEvent(event);
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
	return items.length + 1;
    }

    @Override public String getLine(int index)
    {
	return index < items.length?items[index].getText():"";
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
	    items[hotPointY].introduce(commandEnv);
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
	    items[hotPointY].introduce(commandEnv);
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

    private boolean onAltRight(KeyboardEvent event)
    {
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

    private boolean onAltLeft(KeyboardEvent event)
    {
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
	if (item == null || item.getText() == null || item.getText().isEmpty())
	    luwrain.hint(Hints.EMPTY_LINE); else
	    luwrain.say(item.getText());
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
	final String line = items[hotPointY].getText();
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
	final String line = items[hotPointY].getText();
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

    @Override public boolean onCopyAll()
    {
	if (items == null || items.length < 1)
	    return false;
	Vector<String> res = new Vector<String>();
	for(Item i: items)
	{
	    final String line = i.getText();
	    if (line != null)
		res.add(line); else
		res.add("");
	}
	res.add("");
	if (res.size() == 2)
	    luwrain.say(res.get(0)); else
	    luwrain.say(luwrain.i18n().staticStr(Langs.COPIED_LINES) + (res.size() - 1));
	luwrain.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	if (items == null || items.length < 1)
	    return false;
	if (fromY >= items.length || toY > items.length)
	    return false;
	if (fromY == toY)
	{
	    final String line = items[fromY].getText();
	    if (line.isEmpty())
		return false;
	    final int fromPos = fromX < line.length()?fromX:line.length();
	    final int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		throw new IllegalArgumentException("fromPos should be less than toPos");
	    luwrain.say(line.substring(fromPos, toPos));
	    luwrain.setClipboard(new String[]{line.substring(fromPos, toPos)});
	    return true;
	}
	Vector<String> res = new Vector<String>();
	for(int i = fromY;i < toY;++i)
	{
	    final String line = items[i].getText();
	    if (line != null)
		res.add(line); else
		res.add("");
	}
	res.add("");
	if (res.size() == 2)
	    luwrain.say(res.get(0)); else
	    luwrain.say(luwrain.i18n().staticStr(Langs.COPIED_LINES) + (res.size() - 1));
	luwrain.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
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
