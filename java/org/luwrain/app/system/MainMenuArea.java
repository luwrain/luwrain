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

package org.luwrain.app.system;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.mmedia.EnvironmentSounds;

public class MainMenuArea  implements Area, PopupClosingRequest
{
    public PopupClosing closing = new PopupClosing(this);
    private SystemAppStringConstructor stringConstructor;
    private MainMenuItem items[];
    private String selectedActionName = "";
    private int hotPointX = 0;
    private int hotPointY = 0;

    public MainMenuArea(SystemAppStringConstructor stringConstructor, String[] content)
    {
	this.stringConstructor = stringConstructor;
	items = constructItems(content);
	hotPointY = 0;
	while(hotPointY < items.length && !items[hotPointY].isAction())
	    hotPointY++;
	hotPointY--;
	if (hotPointY < 0 || hotPointY >= items.length)
	    hotPointY = 0;
    }

    public String getSelectedActionName()
    {
	return selectedActionName;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (closing.onKeyboardEvent(event))
	    return true;
	if (items == null)
	    return false;

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ENTER &&
	    !event.isModified())
	{
	    closing.doOk();
	    return true;
	}

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_DOWN &&
	    !event.isModified())
	{
	    if (hotPointY >= items.length)
	    {
		Speech.say(stringConstructor.mainMenuNoItemsBelow(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY++;
	    hotPointX = 0;
	    Luwrain.onAreaNewHotPoint(this);
	    introduceLine(hotPointY);
	    return true;
	}

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_UP &&
	    !event.isModified())
	{
	    if (hotPointY <= 0)
	    {
		Speech.say(stringConstructor.mainMenuNoItemsAbove(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY--;
	    hotPointX = 0;
	    Luwrain.onAreaNewHotPoint(this);
	    introduceLine(hotPointY);
	    return true;
	}

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_RIGHT &&
	    !event.isModified())
	{
	    if (hotPointY < 0 ||
		hotPointY >= items.length ||
		items[hotPointY] == null ||
		items[hotPointY].getText().trim().isEmpty())
	    {
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    final String line = items[hotPointY].getText();
	    if (hotPointX >= line.length())
	    {
		Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX++;
	    Luwrain.onAreaNewHotPoint(this);
	    if (hotPointX >= line.length())
		Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH); else
		Speech.sayLetter(line.charAt(hotPointX));
	    return true;
	}

	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_LEFT &&
	    !event.isModified())
	{
	    if (hotPointY < 0 ||
		hotPointY >= items.length ||
		items[hotPointY] == null ||
		items[hotPointY].getText().trim().isEmpty())
	    {
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    final String line = items[hotPointY].getText();
	    if (hotPointX <= 0)
	    {
		Speech.say(Langs.staticValue(Langs.BEGIN_OF_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX--;
	    Luwrain.onAreaNewHotPoint(this);
	    if (hotPointX < line.length())
		Speech.sayLetter(line.charAt(hotPointX));
	    return true;
	}

	//FIXME:home-end;
	//FIXME:page up-page down;

	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	//FIXME:Introduce;
	return closing.onEnvironmentEvent(event);
    }

    public int getLineCount()
    {
	if (items == null || items.length <= 0)
	    return 1;
	return items.length + 1;
    }

    public String getLine(int index)
    {
	if (items == null || 
	    index >= items.length ||
	    items[index] == null)
	    return "";
	return items[index].getText();
    }

    public String getName()
    {
	return stringConstructor.mainMenuTitle();
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
	if (items == null)
	    return;
	if (hotPointY >= items.length || 
	    items[hotPointY] == null ||
	    items[hotPointY].getText().trim().isEmpty())
	{
	    Speech.silence();
	    EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU_EMPTY_LINE);
	    return;
	}
	Speech.say(items[hotPointY].getText());
    }

    private MainMenuItem constructItem(String name)
    {
	if (name == null || name.trim().isEmpty())
	    return new EmptyMainMenuItem();
	String title = stringConstructor.actionTitle(name);
	if (title.trim().isEmpty())
	    return new EmptyMainMenuItem();
	return new ActionMainMenuItem(name, title);
    }

    public boolean onOk()
    {
	if (items == null)
	    return false;
	if (hotPointY >= items.length || !items[hotPointY].isAction())
	{
	    Luwrain.message("Необходимо выбрать допустимый пункт меню");//FIXME:
	    return false;
	}
	selectedActionName = items[hotPointY].getActionName();
	return true;
    }

    public boolean onCancel()
    {
	return true;
    }

    private MainMenuItem[] constructItems(String[] content)
    { 
	Vector<MainMenuItem> res = new Vector<MainMenuItem>();
	res.add(new EmptyMainMenuItem());
	res.add(new DateTimeMainMenuItem(stringConstructor));
	res.add(new EmptyMainMenuItem());
	for(int i = 0;i < content.length;++i)
	    res.add(constructItem(content[i]));
	return res.toArray(new MainMenuItem[res.size()]);
    }
}
