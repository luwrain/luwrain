/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.app.commander;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class PanelArea implements Area
{
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    public static final String ROOT_DIRECTORY = "/";//Yes, it is just for UNIX;;

    private File current;
    private Vector<DirItem> items;

    private int side = LEFT;
    private CommanderStringConstructor stringConstructor;
    private CommanderActions actions;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public PanelArea(CommanderActions actions, CommanderStringConstructor stringConstructor, int side)
    {
	this.stringConstructor = stringConstructor;
	this.actions = actions;
	this.side = side;
	open(new File("/"));
    }

    public void open(File file)
    {
	open(file, null);
    }

    public void open(File file, String desiredSelected)
    {
	if (file == null)
	    return;
	current = file;
	updateItems();
	if (items == null || items.isEmpty())
	{
	    hotPointX = 0;
	    hotPointY = 0;
	} else
	{
	    hotPointX = 2;
	    hotPointY = 0;
	    if (desiredSelected != null)
		for(hotPointY = 0;hotPointY < items.size();hotPointY++)
		    if (items.elementAt(hotPointY).getName().equals(desiredSelected))
			break;
	    if (hotPointY >= items.size())
		hotPointY = 0;
	}
	Dispatcher.onAreaNewContent(this);
	Dispatcher.onAreaNewHotPoint(this);
	Dispatcher.onAreaNewName(this);
    }

    private void updateItems()
    {
	if (current == null)
	    return;
	items = null;//It is needed to properly handle interrupted list updating;
	File[] files = current.listFiles();
	Vector<DirItem> newItems = new Vector<DirItem>();
	if (current.getParent() != null)
	    newItems.add(new DirItem(new File(current, "..")));
	for(int i = 0;i < files.length;i++)
	    newItems.add(new DirItem(files[i]));
		      newItems.add(new DirItem());
		      items = newItems;
    }

    public int getLineCount()
    {
	if (items == null || items.isEmpty())
	    return 1;
	return items.size();
    }

    public String getLine(int index)
    {
	if (items == null || items.isEmpty())
	    return stringConstructor.inaccessibleDirectoryContent();
	if (index >= items.size())
	    return new String();
	return items.elementAt(index).getName();
    }

    public int getHotPointX()
    {
	if (hotPointX < 0)//Actually never happens;
	    return 0;
	return hotPointX;
    }

    public int getHotPointY()
    {
	if (hotPointY < 0)//Actually never happens;
	    return 0;
	return hotPointY;
    }

    public void setHotPoint(int x, int y)
    {
	//FIXME:
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.withShift() || event.withAlt())
	    return false;

	//Tab;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    if (side == LEFT)
	    actions.gotoRightPanel(); else
	    if (side == RIGHT)
		actions.gotoTasks();
	    return true;
	}

	if (items == null || items.isEmpty())
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}

	//Enter;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ENTER && !event.isModified())
	{
	    if (hotPointY >= items.size())
		return true;
	    DirItem item = items.elementAt(hotPointY);
	    if (item.file == null)
		return true;
	    File parent = current.getParentFile();
	    if (item.getName().equals("..") && parent != null)
		open(parent, current.getName()); else
		open(item.file);
	    introduceFile(current);
	    return true;
	}

	//Down;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ARROW_DOWN && !event.isModified())
	{
	    if (hotPointY + 1>= items.size())
	    {
		Speech.say(stringConstructor.noItemsBelow(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX = 2;
	    hotPointY++;
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceItem(hotPointY);
	    return true;
	}
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ARROW_UP && !event.isModified())
	{
	    if (hotPointY < 1)
	    {
		Speech.say(stringConstructor.noItemsAbove(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX = 2;
	    hotPointY--;
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceItem(hotPointY);
	    return true;
	}
	//FIXME:
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeCommander();
	    return true;
	}
	return false;
    }

    public String getName()
    {
	if (side == LEFT)
	    return stringConstructor.leftPanelName("");
	if (side == RIGHT)
	    return stringConstructor.rightPanelName("");
	return "";
    }

    private void introduceItem(int index)
    {
	if (items == null || index >= items.size())
	    return;
	String text = items.elementAt(index).getName();
	if (text.isEmpty())
	{
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    return;
	}
	Speech.say(text);
    }

    private void introduceFile(File file)
    {
	if (file == null)
	    return;
	if (file.getAbsolutePath().equals(ROOT_DIRECTORY))
	{
	    Speech.say(stringConstructor.rootDirectory());
	    return;
	}
	Speech.say(file.getName());
    }
}
