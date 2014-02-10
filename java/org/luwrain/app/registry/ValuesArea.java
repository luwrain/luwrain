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

package org.luwrain.app.registry;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.registry.Registry;
import org.luwrain.controls.*;

class ValueItem
{
    public String name = "";
    public int type = Registry.STRING;
    public EmbeddedSingleLineEdit edit;
    public String value;
    public boolean boolValue;
} 

class ValuesArea extends NavigateArea implements HotPointInfo, EmbeddedEditLines
{
    private Registry registry;
    private RegistryActions actions;
    private StringConstructor stringConstructor;
    private RegistryDir dir;
    private ValueItem[] items;

    public ValuesArea(Registry registry,
		      RegistryActions actions,
		      StringConstructor stringConstructor)
    {
	this.registry = registry;
	this.actions = actions;
	this.stringConstructor = stringConstructor;
    }

    public void open(RegistryDir dir)
    {
	if (dir == null)
	    return;
	String[] values = registry.getValues(dir.getPath());
	if (values == null)
	    return;
	Vector<ValueItem> n = new Vector<ValueItem>();
	for(String s: values)
	{
	    if (s.isEmpty())
		continue;
	    ValueItem i = new ValueItem();
	    i.name = s;
	    switch(registry.getTypeOf(dir.getPath() + "/" + s))
	    {
	    case Registry.STRING:
		i.type = Registry.STRING;
		i.value = registry.getString(dir.getPath() + "/" + s);
		i.edit = new EmbeddedSingleLineEdit(this, this, i.name.length() + 3, n.size());
		break;
	    case Registry.INTEGER:
		i.type = Registry.INTEGER;
		i.value = "" + registry.getInteger(dir.getPath() + "/" + s);
		i.edit = new EmbeddedSingleLineEdit(this, this, i.name.length() + 3, n.size());
		break;
	    case Registry.BOOLEAN:
		i.type = Registry.BOOLEAN;
		i.boolValue = registry.getBoolean(dir.getPath() + "/" + s);
		i.edit = new EmbeddedSingleLineEdit(this, this, i.name.length() + 3, n.size());
		break;
	    }
	    n.add(i);
	}
	items = n.toArray(new ValueItem[n.size()]);
	this.dir = dir;
	super.setHotPoint(0, 0);
	Luwrain.onAreaNewContent(this);
	Luwrain.onAreaNewHotPoint(this);
    }

    public int getLineCount()
    {
	return items != null?items.length + 1:1;
    }

    public String getLine(int index)
    {
	if (items == null ||index >= items.length)
	    return "";
	return constructLineForScreen(items[index]);
    }

    public String getName()
    {
	return stringConstructor.valuesAreaName();
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && !event.isModified() &&
	    event.getCommand() == KeyboardEvent.TAB)
	{
	    actions.gotoDirs();
	    return true;
	}
	final int index = getHotPointY();
	if (items != null && index < items.length)
	{
	    final ValueItem item = items[index];
	    if (item.edit != null && item.edit.isPosCovered(getHotPointX(), index) && 
		item.edit.onKeyboardEvent(event))
		return true;
	}
	return super.onKeyboardEvent(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch (event.getCode())
	{
	case EnvironmentEvent.CLOSE:
	    actions.close();
	    return true;
	    //FIXME:case EnvironmentEvent.REFRESH:
	}
	final int index = getHotPointY();
	if (items != null && index < items.length)
	{
	    final ValueItem item = items[index];
	    if (item.edit != null && item.edit.isPosCovered(getHotPointX(), index) && 
		item.edit.onEnvironmentEvent(event))
		return true;
	}
	return super.onEnvironmentEvent(event);
    }

    public void setHotPointX(int value)
    {
	if (value >= 0)
	    super.setHotPoint(value, getHotPointY());
    }

    public void setHotPointY(int value)
    {
	//Should be never used;
    }

    public String getEmbeddedEditLine(int editPosX, int editPosY)
    {
	if (items == null || editPosY >= items.length || items[editPosY].edit == null)
	    return "";
	return items[editPosY].value != null?items[editPosY].value:"";
    }

    public void setEmbeddedEditLine(int editPosX, int editPosY, String value)
    {
	if (items == null || editPosY >= items.length || items[editPosY].edit == null)
	    return;
	items[editPosY].value = value;
	Luwrain.onAreaNewContent(this);
    }

    public void introduceLine(int index)
    {
	if (items == null || index >= items.length || items[index] == null)
	{
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE));
	    return;
	}
	final ValueItem item = items[index];
	switch(item.type)
	{
	case Registry.STRING:
	    Speech.say(stringConstructor.introduceStringValue(item.name, item.value));
	    break;
	case Registry.INTEGER:
	    Speech.say(stringConstructor.introduceIntegerValue(item.name, item.value));
	    break;
	case Registry.BOOLEAN:
	    Speech.say(stringConstructor.introduceBooleanValue(item.name, item.boolValue));
	    break;
	}
    }

    private String constructLineForScreen(ValueItem item)
    {
	if (item == null)
	    return "";
	String res = "";
	switch(item.type)
	{
	case Registry.STRING:
	    res = "S";
	    break;
	case Registry.INTEGER:
	    res = "I";
	    break;
	case Registry.BOOLEAN:
	    res = "B";
	    break;
	}
	res += " " + item.name + "=";
	if (item.type == Registry.STRING || item.type == Registry.INTEGER)
	    res += item.value;
	if (item.type == Registry.BOOLEAN)
	    res += item.boolValue?stringConstructor.yes():stringConstructor.no();
	return res;
    }
}
