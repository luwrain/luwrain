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

//TODO:Refresh;

//FIXME:refresh;

import java.io.*;
import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class PanelArea implements Area
{
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    public static final String ROOT_DIR = "/";//Yes, it is really for UNIX;
    public static final String PARENT_DIR = "..";

    private File current;
    private Vector<DirItem> items = null;//null means directory content is inaccessible;

    private int side = LEFT;
    private CommanderStringConstructor stringConstructor;
    private CommanderActions actions;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public PanelArea(CommanderActions actions,
		     CommanderStringConstructor stringConstructor,
		     int side)
    {
	this.stringConstructor = stringConstructor;
	this.actions = actions;
	this.side = side;
	if (Registry.typeOf(CoreRegistryValues.INSTANCE_USER_HOME_DIR) != Registry.STRING)
	{
	    Log.warning("commander", "registry hasn\'t value with user home directory");
	openByPath(ROOT_DIR);
	} else
	    openByPath(Registry.string(CoreRegistryValues.INSTANCE_USER_HOME_DIR));
    }

    public int getLineCount()
    {
	if (items == null || items.isEmpty())
	    return 1;
	return items.size() + 1;
    }

    public String getLine(int index)
    {
	if (items == null)
	    return stringConstructor.inaccessibleDirectoryContent();
	if (index >= items.size())
	    return new String();
	return items.get(index).getScreenTitle();
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
	if (items != null && hotPointY > items.size())
	    hotPointY = items.size();
	return hotPointY;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.withShift() || event.withAlt())
	    return false;

	//Tab;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.TAB &&
	    !event.isModified())
	{
	    if (side == LEFT)
	    actions.gotoRightPanel(); else
	    if (side == RIGHT)
		actions.gotoTasks();
	    return true;
	}

	//Backspace;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.BACKSPACE &&
	    !event.isModified())
	{
	    handleBackspace();
		return true;
	}

	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}

	//Enter;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ENTER &&
	    !event.isModified())
	{
	    if (hotPointY < items.size())
		handleEnter(hotPointY);
		return true;
	}

	//Down;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_DOWN &&
	    !event.withAlt() && !event.withShift())
	{
	    if (hotPointY + 1> items.size())
	    {
		Speech.say(stringConstructor.noItemsBelow(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX = 2;
	    hotPointY++;
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceItem(hotPointY, event.withControl());
	    return true;
	}

	//Up;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_UP &&
	    !event.withAlt() && !event.withShift())
	{
	    if (hotPointY < 1)
	    {
		Speech.say(stringConstructor.noItemsAbove(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX = 2;
	    hotPointY--;
	    Dispatcher.onAreaNewHotPoint(this);
	    introduceItem(hotPointY, event.withControl());
	    return true;
	}

	//Right;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_RIGHT &&
	    !event.isModified())
	{
	    if (hotPointY >= items.size())
	    {
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    if (items.get(hotPointY) == null ||
		items.get(hotPointY).getFileObject() == null)
		return true;
	    String name = items.get(hotPointY).getFileName();
	    if (name == null)
		return true;
	    if (hotPointX >= name.length() + 2)
	    {
		Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX++;
	    if (hotPointX < 2)
		hotPointX = 2;
	    Dispatcher.onAreaNewHotPoint(this);
	    if (hotPointX < name.length() + 2)
	    Speech.sayLetter(name.charAt(hotPointX - 2)); else
		Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
	    return true;
	}

	//Left;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ARROW_LEFT &&
	    !event.isModified())
	{
	    if (hotPointY >= items.size())
	    {
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    if (items.get(hotPointY) == null ||
		items.get(hotPointY).getFileObject() == null)
		return true;
	    String name = items.get(hotPointY).getFileName();
	    if (name == null)
		return true;

	    if (hotPointX <= 2)
	    {
		Speech.say(Langs.staticValue(Langs.BEGIN_OF_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX--;
	    if (hotPointX > name.length()  + 2)
		hotPointX = name.length() + 2;
	    Dispatcher.onAreaNewHotPoint(this);
	    Speech.sayLetter(name.charAt(hotPointX - 2));
	    return true;
	}

	//Page down;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.PAGE_DOWN &&
	    !event.withAlt() && !event.withShift())
	{
	    final int visibleHeight = Dispatcher.getAreaVisibleHeight(this);
	    if (visibleHeight < 1)
	    {
		Log.warning("commander", "panel area visible height is " + visibleHeight + ", cannot process page down key");
		return true;
	    }
	    if (hotPointY + visibleHeight > items.size())
		hotPointY = items.size(); else
		hotPointY += visibleHeight;
	    introduceItem(hotPointY, event.withControl());
	    if (hotPointY < items.size())
	    hotPointX = 2; else
		hotPointX = 0;
	    Dispatcher.onAreaNewHotPoint(this);
	    return true;
	}

	//Page up;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.PAGE_UP &&
	    !event.withAlt() && !event.withShift())
	{
	    final int visibleHeight = Dispatcher.getAreaVisibleHeight(this);
	    if (visibleHeight < 1)
	    {
		Log.warning("commander", "panel area visible height is " + visibleHeight + ", cannot process page up key");
		return true;
	    }
	    if (hotPointY < visibleHeight)
		hotPointY = 0; else
		hotPointY -= visibleHeight;
	    introduceItem(hotPointY, event.withControl());
	    if (hotPointY < items.size())
	    hotPointX = 2; else
		hotPointX = 0;
	    Dispatcher.onAreaNewHotPoint(this);
	    return true;
	}

	//Home;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.HOME &&
	    !event.withAlt() && !event.withShift())
	{
	    if (!event.withControl())
		hotPointY = 0;
	    if (hotPointY < items.size())
	    hotPointX = 2; else
		hotPointX = 0;
	    if (event.withControl())
	    {
		if (hotPointY >= items.size() || hotPointX >= items.get(hotPointY).getFileName().length() + 2)
		    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH); else
		    Speech.sayLetter(items.get(hotPointY).getFileName().charAt(hotPointX - 2));
	    } else
		introduceItem(hotPointY, false);
	    Dispatcher.onAreaNewHotPoint(this);
	    return true;
	}

	//End;
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.END &&
	    !event.withAlt() && !event.withShift())
	{
	    if (!event.withControl())
	    {
		hotPointY = items.size();
		hotPointX = 0;
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    } else
	    {
		if (hotPointY < items.size())
		{
		    hotPointX = items.get(hotPointY).getFileName().length() + 2;
		    Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
		} else
		{
		    hotPointX = 0;
		    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		}
	    }
	    Dispatcher.onAreaNewHotPoint(this);
	    return true;
	}






	//FIXME:backspace;
	//FIXME:selection;

	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
case EnvironmentEvent.INTRODUCE:
    Speech.say(stringConstructor.appName() + " " + getName());
    return true;
	case EnvironmentEvent.CLOSE:
	    actions.closeCommander();
	    return true;
	case EnvironmentEvent.OK:
	    if (items != null && hotPointY < items.size())
		handleEnter(hotPointY);
	    return true;
	default:
	}
    return false;
    }

    public String getName()
    {
	if (side == LEFT)
	    return stringConstructor.leftPanelName(current != null?current.getAbsolutePath():null);
	if (side == RIGHT)
	    return stringConstructor.rightPanelName(current != null?current.getAbsolutePath():null);
	return "";
    }

    private void introduceItem(int index, boolean brief)
    {
	if (items == null)
	    return;
	if (index >= items.size())
	{
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    return;
	}
	Speech.say(stringConstructor.dirItemIntroduction(items.get(index), brief));
    }

    private void introduceLocation(File file)
    {
	if (file == null)
	    return;
	if (file.getAbsolutePath().equals(ROOT_DIR))
	{
	    Speech.say(stringConstructor.rootDirectory());
	    return;
	}
	Speech.say(file.getName());
    }

    private void handleEnter(int index)
    {
	if (items == null || index >= items.size())
	    return;
	    DirItem item = items.get(index);
	    if (item.getType() == DirItem.REGULAR)//FIXME:The same for multiple selection;
	    {
		String fileNames[] = new String[1];
		fileNames[0] = item.getFileObject().getAbsolutePath();
		actions.openFiles(fileNames);
		return;
	    }
	    File parent = current.getParentFile();
	    if (item.getFileName().equals(PARENT_DIR) && parent != null)
		openByFile(parent, current.getName()); else
		openByFile(item.getFileObject());
	    introduceLocation(current);
    }

    private void handleBackspace()
    {
	if (current == null)
	    return;
	File parent = current.getParentFile();
	if (parent == null)
	    return;
	openByFile(parent, current.getName());
	introduceLocation(current);
    }

    private void openByPath(String path)
    {
	if (path != null)
	    openByFile(new File(path));
    }

    private void openByFile(File file)
    {
	openByFile(file, null);
    }

    private void openByFile(File file, String desiredSelected)
    {
	if (file == null || !file.isDirectory())
	    return;
	current = file;
	items = constructDirItems(current);
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
		    if (items.get(hotPointY).getFileName().equals(desiredSelected))
			break;
	    if (hotPointY >= items.size())
		hotPointY = 0;
	}
	Dispatcher.onAreaNewContent(this);
	Dispatcher.onAreaNewHotPoint(this);
	Dispatcher.onAreaNewName(this);
    }

    static private Vector<DirItem> constructDirItems(File f)
    {
	if (f == null)
	    return null;
	File[] files = f.listFiles();
	if (files == null)
	    return null;
	Vector<DirItem> items = new Vector<DirItem>();
	if (f.getParent() != null)
	    items.add(new DirItem(new File(f, "..")));
	for(int i = 0;i < files.length;i++)
	    items.add(new DirItem(files[i]));
	sortByNameDirSplitting(items);
	return items;
    }

    private static void sortByNameDirSplitting(Vector<DirItem> items)
    {
	//FIXME:Parent directory always on top;
	if (items == null || items.size() < 2)
	    return;
	int dirCount = 0;
	for(int i = 0;i < items.size();i++)
	    if (items.get(i).getType() == DirItem.DIRECTORY)
		dirCount++;
	final int fileCount = items.size() - dirCount;
	DirItem[] v1 = new DirItem[dirCount];
	DirItem[] v2 = new DirItem[fileCount];
	int k1 = 0, k2 = 0;
	for(int i = 0;i < items.size();i++)
	    if (items.get(i).getType() == DirItem.DIRECTORY)
		v1[k1++] = items.get(i); else
		v2[k2++] = items.get(i);
	sortByName(v1);
	sortByName(v2);
	for(int i = 0;i < dirCount;i++)
	    items.set(i, v1[i]);
	for(int i = 0;i < fileCount;i++)
	    items.set(dirCount + i, v2[i]);
    }

    private static void sortByName(DirItem[] items)
    {
	Arrays.sort(items, new Comparator() {
		public int compare(Object o1, Object o2)
		{
		    DirItem i1 = (DirItem)o1;
		    DirItem i2 = (DirItem)o2;
		    return i1.getFileName().compareTo(i2.getFileName());
		}
	    });
    }
}
