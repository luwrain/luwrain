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
public interface StringValueSaver
{
    void saveStringValue(String name, String value);
}

private enum Type
    {
STRING
    }

private class Entry 
{
    Type type;
    String name;
    String title;

    //For strings
    String defStrValue = "";
    StringValueSaver strValueSaver = null;

    Entry(String name, String title,
	  String defValue, StringValueSaver strValueSaver)
    {
	this.type = Type.STRING;
	this.name = name;
	this.title = title;
	this.defStrValue = defValue;
	this.strValueSaver = strValueSaver;
	NullCheck.notNull(name, "name");
	NullCheck.notNull(title, "title");
	NullCheck.notNull(defStrValue, "defStrValue");
	NullCheck.notNull(strValueSaver, "strValueSaver");
    }
}

    static private class Area extends FormArea
    {
	private Environment environment;

	Area(Environment environment , String name)
	{
	    super(new DefaultControlEnvironment(environment.getLuwrain()), name);
	    NullCheck.notNull(environment, "environment");
	    this.environment = environment;
	}

	@Override public boolean onKeyboardEvent(KeyboardEvent event)
	{
	    NullCheck.notNull(event, "event");
	    if (event.isSpecial() && !event.isModified())
		switch(event.getSpecial())
		{
		case TAB:
		    environment.gotoSectionsTree ();
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
		environment.close();
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
    private String name;
    private int desiredRoot;
    private final Vector<Entry> entries = new Vector<Entry>();

    public SimpleFormSection(String name, int desiredRoot)
    {
	this.name = name;
	this.desiredRoot = desiredRoot;
	NullCheck.notNull(name, "name");
    }

    public void addString(String title, String defStrValue, StringValueSaver strValueSaver)
    {
	NullCheck.notNull(title, "title");
	NullCheck.notNull(defStrValue, "defStrValue");
	NullCheck.notNull(strValueSaver, "strValueSaver");
	entries.add(new Entry("entry" + entries.size(), title, defStrValue, strValueSaver));
    }

    @Override public int getDesiredRoot()
    {
	return desiredRoot;
    }

    @Override public Area getSectionArea(Environment environment)
    {
	if (area == null)
	    area = createArea(environment);
	return area;
    }

    @Override public String toString()
    {
	return name;
    }

    @Override public boolean canCloseSection(Environment environment)
    {
	if (area == null)
	    return true;
	if (save())
	    environment.getLuwrain().message("Во время сохранения сделанных изменений произошла непредвиденная ошибка", Luwrain.MESSAGE_ERROR);//FIXME:
	return true;
    }

    private boolean save()
    {
	return true;
    }

    private Area  createArea(Environment environment)
    {
	final Area res = new Area(environment, name);
for(Entry e: entries)
{
    switch(e.type)
    {
    case STRING:
res.addEdit(e.name, e.title, e.defStrValue, null, true);
    }
}
return res;
    }
}
