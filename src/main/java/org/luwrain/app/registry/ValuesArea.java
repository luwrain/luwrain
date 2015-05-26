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

package org.luwrain.app.registry;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.popups.*;

class ValueItem
{
    public String name = "";
    public boolean readOnly = false;
    public int type = Registry.STRING;
    public EmbeddedSingleLineEdit edit;
    public String value, initialValue;
    public boolean boolValue, initialBoolValue;

    public boolean isModified()
    {
	if (readOnly)
	    return false;
	if (type == Registry.BOOLEAN)
	    return boolValue != initialBoolValue;
	if (value == null || initialValue == null)
	    return false;
	return !value.equals(initialValue);
    }
} 

class ValuesArea extends NavigateArea implements EmbeddedEditLines
{
    private Luwrain luwrain;
    private Registry registry;
    private RegistryActions actions;
    private StringConstructor stringConstructor;
    private RegistryDir dir;
    private ValueItem[] items;

    public ValuesArea(Luwrain luwrain,
		      Registry registry,
		      RegistryActions actions,
		      StringConstructor stringConstructor)
    {
	super(new DefaultControlEnvironment(luwrain));
	this.luwrain = luwrain;
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
		i.initialValue = i.value;
		i.edit = new EmbeddedSingleLineEdit(new DefaultControlEnvironment(luwrain), this, this, i.name.length() + 3, n.size());
		break;
	    case Registry.INTEGER:
		i.type = Registry.INTEGER;
		i.value = "" + registry.getInteger(dir.getPath() + "/" + s);
		i.initialValue = i.value;
		i.edit = new EmbeddedSingleLineEdit(new DefaultControlEnvironment(luwrain), this, this, i.name.length() + 3, n.size());
		break;
	    case Registry.BOOLEAN:
		i.type = Registry.BOOLEAN;
		i.boolValue = registry.getBoolean(dir.getPath() + "/" + s);
		i.initialBoolValue = i.boolValue;
		i.edit = new EmbeddedSingleLineEdit(new DefaultControlEnvironment(luwrain), this, this, i.name.length() + 3, n.size());
		break;
	    }
	    n.add(i);
	}
	items = n.toArray(new ValueItem[n.size()]);
	this.dir = dir;
	super.setHotPoint(0, 0);
	luwrain.onAreaNewContent(this);
	luwrain.onAreaNewHotPoint(this);
    }

    public void refresh()
    {
	if (dir == null || !registry.hasDirectory(dir.getPath()))
	{
	    dir = null;
	    items = null;
	    super.setHotPoint(0, 0);
	    luwrain.onAreaNewContent(this);
	    return;
	}
	open(dir);
    }

    public boolean hasModified()
    {
	if (items == null)
	    return false;
	for(ValueItem i: items)
	    if (i.isModified())
		return true;
	return false;
    }

    public void save()
    {
	if (items == null || dir == null)
	    return;
	boolean hasProblems = false;
	for(ValueItem i: items)
	{
	    {
		if (!i.isModified())
		    continue;
		final String path = dir.getPath() + "/" + i.name;
		switch (i.type)
		{
		case Registry.STRING:
		    if (registry.setString(path, i.value))
			i.initialValue = i.value; else
			hasProblems = true;
		    break;
		case Registry.INTEGER:
		try {
		    if (registry.setInteger(path, Integer.parseInt(i.value)))
			i.initialValue = i.value; else
			hasProblems = true;
		}
		catch(NumberFormatException e)
		{
		    hasProblems = true;
		}
		break;
		case Registry.BOOLEAN:
		    if (registry.setBoolean(path, i.boolValue))
			i.initialBoolValue = i.boolValue; else
			hasProblems = true;
		    break;
		}
	    }
	}
	if (hasProblems)
	    luwrain.message(stringConstructor.savingFailed()); else
	    luwrain.message(stringConstructor.savingOk());
    }

    public RegistryDir getOpenedDir()
    {
	return dir;
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

    @Override public String getAreaName()
    {
	return stringConstructor.valuesAreaName();
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && !event.isModified())
	    switch(event.getCommand())
	    {
	    case KeyboardEvent.TAB:
		actions.gotoDirs();
		return true;
	    case KeyboardEvent.INSERT:
		return onInsert();
	    }
	final int index = getHotPointY();
	if (items != null && index < items.length)
	{
	    final ValueItem item = items[index];
	    if (!item.readOnly && item.edit != null && item.edit.isPosCovered(getHotPointX(), index) && 
		item.edit.onKeyboardEvent(event))
		return true;
	}
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch (event.getCode())
	{
	case EnvironmentEvent.SAVE:
	case EnvironmentEvent.OK:
	    save();
	    return true;
	case EnvironmentEvent.CLOSE:
	    actions.close();
	    return true;
	    case EnvironmentEvent.REFRESH:
		//FIXME:Check if there are unsaved changes
		refresh();
		break;
	}
	final int index = getHotPointY();
	if (items != null && index < items.length)
	{
	    final ValueItem item = items[index];
	    if (!item.readOnly && item.edit != null && item.edit.isPosCovered(getHotPointX(), index) && 
		item.edit.onEnvironmentEvent(event))
		return true;
	}
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return false;
    }

    public boolean onInsert()
    {
	if (dir == null)
	    return false;
	String[] types = new String[3];
	types[0] = "integer";
	types[1] = "string";
	types[2] = "boolean";
	SimpleEditPopup linePopup = new SimpleEditPopup(luwrain, stringConstructor.newParameterTitle(), stringConstructor.newParameterName(), "");
	luwrain.popup(linePopup);
	if (linePopup.closing.cancelled())//FIXME:Validator if not empty
	    return true;
	if (linePopup.text().trim().isEmpty())
	{
	    luwrain.message(stringConstructor.parameterNameMayNotBeEmpty());
	    return true;
	}
	EditListPopup listPopup = new EditListPopup(luwrain, new FixedListPopupModel(types), stringConstructor.newParameterTitle(), stringConstructor.newParameterType(), "string");//FIXME:Validator if not from the list;;
	luwrain.popup(listPopup);
	if (listPopup.closing.cancelled())
	    return true;
	int type;
	if (listPopup.text().trim().equals("string"))
	    type = Registry.STRING; else
	    if (listPopup.text().trim().equals("integer"))
		type = Registry.INTEGER; else
		if (listPopup.text().trim().equals("boolean"))
		    type = Registry.BOOLEAN; else
		{
		    luwrain.message(stringConstructor.invalidParameterType(listPopup.text()));
		    return true;
		}
	if (!insertValue(linePopup.text(), type))
	    luwrain.message(stringConstructor.parameterInsertionFailed());
	return true;
    }

    private boolean insertValue(String name, int type)
    {
	if (name == null || name.trim().isEmpty())
	    return false;
	if (registry.hasValue(dir.getPath() + "/" + name))
	    return false;
	switch(type)
	{
	case Registry.STRING:
	    if (!registry.setString(dir.getPath() + "/" + name, ""))
		return false;
	    break;
	case Registry.INTEGER:
	    if (!registry.setInteger(dir.getPath() + "/" + name, 0))
		return false;
	    break;
	case Registry.BOOLEAN:
	    if (!registry.setBoolean(dir.getPath() + "/" + name, false))
		return false;
	    break;
	default:
	    return false;
	}
	loadNewlyInsertedItems();
	if (items != null)
	    for(int i = 0;i < items.length;++i)
		if (items[i].name.equals(name))
		{
		    super.setHotPoint(0, i);
		    break;
		}
	return true;
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
	luwrain.onAreaNewContent(this);
    }

    public void introduceLine(int index)
    {
	if (items == null || index >= items.length || items[index] == null)
	{
	    luwrain.say(Langs.staticValue(Langs.EMPTY_LINE));
	    return;
	}
	final ValueItem item = items[index];
	switch(item.type)
	{
	case Registry.STRING:
	    luwrain.say(stringConstructor.introduceStringValue(item.name, item.value));
	    break;
	case Registry.INTEGER:
	    luwrain.say(stringConstructor.introduceIntegerValue(item.name, item.value));
	    break;
	case Registry.BOOLEAN:
	    luwrain.say(stringConstructor.introduceBooleanValue(item.name, item.boolValue));
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

    private void loadNewlyInsertedItems()
    {
	//FIXME:
    }
}
