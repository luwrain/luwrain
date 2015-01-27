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

package org.luwrain.app.commander;

import java.io.*;
import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class CommanderArea implements Area
{
    public static final int LEFT = 1;
    public static final int RIGHT = 2;

    public static final int INITIAL_HOT_POINT_X = 2;

    public static final String ROOT_DIR = "/";//Yes, it is really for UNIX;
    public static final String PARENT_DIR = "..";

    private File current = null;
    private Vector items = null;//null means directory content is inaccessible;

    private Luwrain luwrain;
    private int hotPointX = 0;
    private int hotPointY = 0;

    public CommanderArea(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	Registry registry = luwrain.getRegistry();
	if (registry.getTypeOf("FIXME:RegistryKeys.INSTANCE_USER_HOME_DIR") != Registry.STRING)
	{
	    Log.warning("commander", "registry hasn\'t value with user home directory");
	openByPath(ROOT_DIR);
	} else
	    openByPath(registry.getString("FIXME:RegistryKeys.INSTANCE_USER_HOME_DIR"));
    }

    public File[] getSelected()
    {
	/*
	if (items == null)
	    return null;
	Vector<File> files = new Vector<File>();
	for(DirItem i: items)
	    if (i.isSelected())
		files.add(i.getFileObject());
	if (files.size() > 0)
	    return files.toArray(new File[files.size()]);
	if (hotPointY >= items.size())
	    return null;
	File[] f = new File[1];
	f[0] = items.get(hotPointY).getFileObject();
	return f;
	*/
	return null;
    }

    public File getCurrentDir()
    {
	return current;
    }

    public void refresh()
    {
	/*
	if (current == null)
	    return;
	if (items == null || hotPointY >= items.size())
	{
	    openByFile(current);
	    return;
	}
	openByFile(current, items.get(hotPointY).getFileName());
	*/
    }

    @Override public int getLineCount()
    {
	if (items == null || items.isEmpty())
	    return 1;
	return items.size() + 1;
    }

    @Override public String getLine(int index)
    {
	if (items == null)
	    return "FIXME:stringConstructor.inaccessibleDirectoryContent()";
	if (index >= items.size())
	    return new String();
	return "FIXME:items.get(index).getScreenTitle()";
    }

    @Override public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    @Override public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isModified() || !event.isCommand())
	    return false;
	switch(event.getCommand())
	{
	case KeyboardEvent.BACKSPACE:
	    return onBackspace(event);
	case KeyboardEvent.ENTER:
	    return onEnter();
	case KeyboardEvent.ARROW_DOWN:
	    return onArrowDown(event, false);
	case KeyboardEvent.ARROW_UP:
	    return onArrowUp(event, false);
	case KeyboardEvent.ALTERNATIVE_ARROW_DOWN:
	    return onArrowDown(event, true);
	case KeyboardEvent.ALTERNATIVE_ARROW_UP:
	    return onArrowUp(event, true);
	case KeyboardEvent.ARROW_LEFT:
	    return onArrowLeft(event);
	case KeyboardEvent.ARROW_RIGHT:
	    return onArrowRight(event);
	    //FIXME:ALTERNATIVE_ARROW_LEFT;
	    //FIXME:ALTERNATIVE_ARROW_RIGHT;
	case KeyboardEvent.PAGE_DOWN:
	    return onPageDown(event, false);
	case KeyboardEvent.PAGE_UP:
	    return onPageUp(event, false);
	case KeyboardEvent.HOME:
	    return onHome(event);
	case KeyboardEvent.END:
	    return onEnd(event);
	case KeyboardEvent.ALTERNATIVE_PAGE_DOWN:
	    return onPageDown(event, true);
	case KeyboardEvent.ALTERNATIVE_PAGE_UP:
	    return onPageUp(event, true);
	case KeyboardEvent.ALTERNATIVE_HOME:
	    return onAlternativeHome(event);
	case KeyboardEvent.ALTERNATIVE_END:
	    return onAlternativeEnd(event);
	    //FIXME:case KeyboardEvent.INSERT:
	default:
	    return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.INTRODUCE:
    Speech.say("FIXME:stringConstructor.appName() +  + getName()");
	case EnvironmentEvent.OK:
	    return onEnter();
	case EnvironmentEvent.REFRESH:
	    refresh();
	    return true;
	case EnvironmentEvent.OPEN:
	    return onOpen(event);
	default:
	    return false;
	}
    }

    @Override public String getName()
    {
return current.getAbsolutePath();
    }

    private boolean onEnter()
    {
	/*
	if (items == null)
	{
	    Speech.say("FIXME:stringConstructor.inaccessibleDirectoryContent()", Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= items.size())
	    return false;
	final DirItem item = items.get(hotPointY);
	if (item == null)
	{
	    Log.warning("commander", "the panel has null director item");
	    return false;
	}
	if (item.getType() == DirItem.DIRECTORY)
	{
	    File parent = current.getParentFile();
	    if (item.getFileName().equals(PARENT_DIR) && parent != null)
		openByFile(parent, current.getName()); else
		openByFile(item.getFileObject());
	    introduceLocation(current);
	    return true;
	}
	File[] selected = getSelected();
	if (selected != null && selected.length > 0)
	{
	    String[] fileNames = new String[selected.length];
	    for(int i = 0;i < selected.length;++i)
		fileNames[i] = selected[i].getAbsolutePath();
	    actions.openFiles(fileNames);
	    return true;
	}
	String fileNames[] = new String[1];
	fileNames[0] = item.getFileObject().getAbsolutePath();
	actions.openFiles(fileNames);
	*/
	return true;
    }

    private boolean onBackspace(KeyboardEvent event)
    {
	if (current == null)
	    return false;
	File parent = current.getParentFile();
	if (parent == null)
	    return false;
	openByFile(parent, current.getName());
	introduceLocation(current);
	return true;
    }

    private boolean onArrowDown(KeyboardEvent event, boolean briefIntroduction)
    {
	/*
	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY + 1> items.size())
	{
	    Speech.say(stringConstructor.noItemsBelow(), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointX = hotPointY < items.size()?INITIAL_HOT_POINT_X:0;
	hotPointY++;
	luwrain.onAreaNewHotPoint(this);
	introduceItem(hotPointY, briefIntroduction);
	*/
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event, boolean briefIntroduction)
    {
	/*
	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY < 1)
	{
	    Speech.say(stringConstructor.noItemsAbove(), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointX = INITIAL_HOT_POINT_X;
	hotPointY--;
	luwrain.onAreaNewHotPoint(this);
	introduceItem(hotPointY, briefIntroduction);
	*/
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	/*
	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= items.size())
	{
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    return true;
	}
	if (items.get(hotPointY) == null ||
	    items.get(hotPointY).getFileObject() == null)
	    return true;
	final String name = items.get(hotPointY).getFileName();
	if (name == null)
	    return true;
	if (hotPointX >= name.length() + INITIAL_HOT_POINT_X)
	{
	    Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointX++;
	if (hotPointX < INITIAL_HOT_POINT_X)
	    hotPointX = INITIAL_HOT_POINT_X;
	luwrain.onAreaNewHotPoint(this);
	if (hotPointX < name.length() + 2)
	    Speech.sayLetter(name.charAt(hotPointX - 2)); else
	    Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
	*/
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	/*

	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}

	if (hotPointY >= items.size())
	{
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    return true;
	}
	if (items.get(hotPointY) == null ||
	    items.get(hotPointY).getFileObject() == null)
	    return true;
	final String name = items.get(hotPointY).getFileName();
	if (name == null)
	    return true;
	if (hotPointX <= INITIAL_HOT_POINT_X)
	{
	    Speech.say(Langs.staticValue(Langs.BEGIN_OF_LINE), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointX--;
	if (hotPointX > name.length()  + INITIAL_HOT_POINT_X)
	    hotPointX = name.length() + INITIAL_HOT_POINT_X;
	luwrain.onAreaNewHotPoint(this);
	Speech.sayLetter(name.charAt(hotPointX - 2));
	*/
	return true;
    }

    private boolean onPageDown(KeyboardEvent event, boolean briefIntroduction)
    {
	/*
	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}
	final int visibleHeight = luwrain.getAreaVisibleHeight(this);
	if (visibleHeight < 1)
	{
	    Log.warning("commander", "panel area visible height is " + visibleHeight + ", cannot process page down key");
	    return false;
	}
	if (hotPointY + visibleHeight > items.size())
	    hotPointY = items.size(); else
	    hotPointY += visibleHeight;
	luwrain.onAreaNewHotPoint(this);
	introduceItem(hotPointY, briefIntroduction);
	hotPointX = hotPointY < items.size()?INITIAL_HOT_POINT_X:0;
	*/
	return true;
    }

    private boolean onPageUp(KeyboardEvent event, boolean briefIntroduction)
    {
	/*
	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}
	final int visibleHeight = luwrain.getAreaVisibleHeight(this);
	if (visibleHeight < 1)
	{
	    Log.warning("commander", "panel area visible height is " + visibleHeight + ", cannot process page up key");
	    return false;
	}
	if (hotPointY < visibleHeight)
	    hotPointY = 0; else
		hotPointY -= visibleHeight;
	hotPointX = hotPointY < items.size()?INITIAL_HOT_POINT_X:0;
	luwrain.onAreaNewHotPoint(this);
	introduceItem(hotPointY, briefIntroduction);
	*/
	return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	/*
	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY = 0;
	hotPointX = hotPointY < items.size()?INITIAL_HOT_POINT_X:0;
	introduceItem(hotPointY, false);
	luwrain.onAreaNewHotPoint(this);
	*/
	return true;
    }

    private boolean onAlternativeHome(KeyboardEvent event)
    {
	/*
	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointX = hotPointY < items.size()?INITIAL_HOT_POINT_X:0;
	if (hotPointY >= items.size() || hotPointX >= items.get(hotPointY).getFileName().length() + INITIAL_HOT_POINT_X)
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH); else
	    Speech.sayLetter(items.get(hotPointY).getFileName().charAt(hotPointX - 2));
	*/
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	/*
	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY = items.size();
	hotPointX = 0;
	Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	luwrain.onAreaNewHotPoint(this);
	*/
	return true;
    }

    private boolean onAlternativeEnd(KeyboardEvent event)
    {
	/*
	if (items == null)
	{
	    Speech.say(stringConstructor.inaccessibleDirectoryContent(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY < items.size())
	{
	    hotPointX = items.get(hotPointY).getFileName().length() + INITIAL_HOT_POINT_X;
	    Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
	} else
	{
	    hotPointX = 0;
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	}
	luwrain.onAreaNewHotPoint(this);
	*/
	return true;
    }

    private boolean onOpen(EnvironmentEvent event)
    {
	/*
	if (current == null || !current.isDirectory())
	    return false;
	File f = luwrain.openPopup(null, null, current);
	if (f == null)
	    return true;
	if (f.isDirectory())
	    openByFile(f); else
	    luwrain.openFile(f.getAbsolutePath());
	*/
	return true;
    }

    private void introduceItem(int index, boolean brief)
    {
	/*
	if (items == null)
	    return;
	if (index >= items.size())
	{
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    return;
	}
	Speech.say(stringConstructor.dirItemIntroduction(items.get(index), brief));
	*/
    }

    private void introduceLocation(File file)
    {
	/*
	if (file == null)
	    return;
	if (file.getAbsolutePath().equals(ROOT_DIR))
	{
	    Speech.say(stringConstructor.rootDirectory());
	    return;
	}
	Speech.say(file.getName());
	*/
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
	/*
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
	luwrain.onAreaNewContent(this);
	luwrain.onAreaNewHotPoint(this);
	luwrain.onAreaNewName(this);
	*/
    }

    static private Vector constructDirItems(File f)
    {
	return null;
	/*FIXME:
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
	*/
    }

    private static void sortByNameDirSplitting(Vector items)
    {
	/*
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
	*/
    }

    private static void sortByName(Object[] items)
    {
	/*
	Arrays.sort(items, new Comparator() {
		public int compare(Object o1, Object o2)
		{
		    DirItem i1 = (DirItem)o1;
		    DirItem i2 = (DirItem)o2;
		    return i1.getFileName().compareTo(i2.getFileName());
		}
	    });
	*/
    }
}
