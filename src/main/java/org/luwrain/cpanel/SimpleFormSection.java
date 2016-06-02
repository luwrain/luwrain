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

package org.luwrain.cpanel;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class SimpleFormSection extends EmptySection
{
private enum Type { STR, INT, BOOL, STATIC }

public interface StringLoader { String loadString(String name); }
public interface StringSaver { void saveString(String name, String value); }

public interface integerLoader { int loadInteger(String name); }
public interface IntegerSaver { void saveInteger(String name, int value); }

public interface booleanLoader { boolean loadBoolean(String name); }
public interface BooleanSaver { void saveBoolean(String name, boolean value); }

static private class Entry 
{
    Type type;
    String name;
    String title;

    //For strings
    StringLoader strLoader = null;
    StringSaver strSaver = null;

    Entry(String name, String title,
	  StringLoader strLoader, StringSaver strSaver)
    {
	NullCheck.notNull(name, "name");
	NullCheck.notNull(title, "title");
	NullCheck.notNull(strLoader, "strLoader");
	NullCheck.notNull(strSaver, "strSaver");
	this.type = Type.STR;
	this.name = name;
	this.title = title;
	this.strLoader = strLoader;
	this.strSaver = strSaver;
    }
}

    static private class Area extends FormArea implements SectionArea
    {
	private ControlPanel controlPanel;

	Area(ControlPanel controlPanel, String name)
	{
	    super(new DefaultControlEnvironment(controlPanel.getCoreInterface()), name);
	    NullCheck.notNull(controlPanel, "controlPanel");
	    this.controlPanel = controlPanel;
	}

	@Override public boolean onKeyboardEvent(KeyboardEvent event)
	{
	    NullCheck.notNull(event, "event");
	    if (event.isSpecial() && !event.isModified())
		switch(event.getSpecial())
		{
		case TAB:
		    controlPanel.gotoSectionsTree ();
	    return true;
		}
	return super.onKeyboardEvent(event);
	}

	@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
	{
	    NullCheck.notNull(event, "event");
	    switch(event.getCode())
	    {
	    case CLOSE:
		controlPanel.close();
		return true;
	    default:
		return super.onEnvironmentEvent(event);
	    }
	}

	/*
	@Override public String getAreaName()
	{
	    return "Персональная информация"name
	}
	*/

	/*
	boolean save()
	{
	}
	*/
    }

private Area area = null;
    private Element element;
    private String name;
    private final Vector<Entry> entries = new Vector<Entry>();

    public SimpleFormSection(Element element, String name)
    {
	NullCheck.notNull(element, "element");
	NullCheck.notNull(name, "name");
	this.element = element;
	this.name = name;
    }

    public String addString(String title, 
StringLoader strLoader, StringSaver strSaver)
    {
	NullCheck.notNull(title, "title");
	NullCheck.notNull(strLoader, "strLoader");
	NullCheck.notNull(strSaver, "strSaver");
	final String newName = "entry" + entries.size();
	entries.add(new Entry(newName, title, strLoader, strSaver));
	return newName;
    }

    @Override public Element getElement()
    {
	return element;
    }

    @Override public SectionArea getSectionArea(ControlPanel controlPanel)
    {
	if (area == null)
	    area = createArea(controlPanel);
	return area;
    }

    @Override public String toString()
    {
	return name;
    }

    @Override public boolean canCloseSection(ControlPanel controlPanel)
    {
	if (area == null)
	    return true;
	if (save())
	    controlPanel.getCoreInterface().message("Во время сохранения сделанных изменений произошла непредвиденная ошибка", Luwrain.MESSAGE_ERROR);//FIXME:
	return true;
    }

    private boolean save()
    {
	return true;
    }

    private Area  createArea(ControlPanel controlPanel)
    {
	final Area res = new Area(controlPanel, name);
for(Entry e: entries)
{
    switch(e.type)
    {
    case STR:
	res.addEdit(e.name, e.title, e.strLoader.loadString(e.name), null, true);
    }
}
return res;
    }
}
