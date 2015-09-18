/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class FormArea  extends NavigateArea
{
    static public final int NONE = 0;
    static public final int EDIT = 1;
    static public final int CHECKBOX = 2;
    static public final int LIST = 3;
    static public final int STATIC = 4;
    static public final int MULTILINED = 5;

    static private class Item implements EmbeddedEditLines
    {
	int type;
	String name;
	String caption;
	Object obj;
	boolean enabled = true;

	private ControlEnvironment environment;//Needed for sending notifications about changing of text
	private Area area;//Needed for sending notifications about changing of text

	private String enteredText = "";
	private EmbeddedSingleLineEdit edit;

	private Object staticObject;

	Item(ControlEnvironment environment, Area area)
	{
	    this.environment = environment;
	    this.area = area;
	}

	boolean onKeyboardEvent(KeyboardEvent event)
	{
	    return edit != null?edit.onKeyboardEvent(event):false;
	}

	boolean onEnvironmentEvent(EnvironmentEvent event)
	{
	    return edit != null?edit.onEnvironmentEvent(event):false;
	}

	boolean onAreaQuery(AreaQuery query)
	{
	    return edit != null?edit.onAreaQuery(query):false;
	}


	@Override public String getEmbeddedEditLine(int editPosX, int editPosY)
	{
	    //We may skip checking of editPosX and editPosY because there is only one edit to call this method;
	    return enteredText;
	}

	@Override public void setEmbeddedEditLine(int editPosX, int editPosY,
						  String value)
	{
	    //We may skip checking of editPosX and editPosY because there is only one edit to call this method;
	    enteredText = value != null?value:"";
	    environment.onAreaNewContent(area);
	}
    }

    private ControlEnvironment environment;
    private String name = "";
    private final Vector<Item> items = new Vector<Item>();

    private String multilinedCaption;
    private DefaultMultilinedEditContent multilinedContent;
    private EmbeddedMultilinedEdit multilinedEdit;
    private boolean multilinedEnabled = true;//FIXME:

    public FormArea(ControlEnvironment environment)
    {
	super(environment);
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
    }

    public FormArea(ControlEnvironment environment, String name)
    {
	super(environment);
	this.environment = environment;
	this.name = name;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(name, "name");
    }

    public boolean hasItemWithName(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return false;
	for(Item i: items)
	    if (i.name.equals(itemName))
		return true;
	return false;
    }

    //For multiline zone returns "MULTILINE" on multiline caption as well (the line above the multiline edit) 
    public int getItemTypeOnLine(int index)
    {
	if (index < 0)
	    return NONE;
	if (index < items.size())
	    return items.get(index).type;
	return multilinedEditActivated()?MULTILINED:NONE;
    }

    public String getItemNameOnLine(int index)
    {
	if (index < 0 || index >= items.size())
	    return null;
	return items.get(index).name;
    }

    public Object getItemObjOnLine(int index)
    {
	if (index < 0 || index >= items.size())
	    return null;
	return items.get(index).obj;
    }

    public Object getItemObjByName(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return null;
	for(Item i: items)
	    if (i.name.equals(itemName))
		return i.obj;
	return null;
    }

    public boolean addEdit(String itemName, String caption,
			   String initialText, Object obj, boolean enabled)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(initialText, "initialText");
	if (itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	final Item item = new Item(environment, this);
	item.type = EDIT;
	item.name = itemName;
	item.caption = caption;
	item.enteredText = initialText;
	item.obj = obj;
	item.enabled = enabled;
	item.edit = new EmbeddedSingleLineEdit(environment, item, this, item.caption.length(), items.size());
	items.add(item);
	updateEditsPos();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public String getEnteredText(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return null;
	for(Item i: items)
	    if (i.type == EDIT && i.name.equals(itemName))
		return i.enteredText;
	return null;
    }

    public boolean addStatic(String itemName, String caption,
			     Object obj)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	if (itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	final Item item = new Item(environment, this);
	item.type = STATIC;
	item.name = itemName;
	    item.caption = caption;
	    item.obj = obj;
	    items.add(item);
	    updateEditsPos();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public boolean multilinedEditActivated()
    {
	return multilinedEdit != null && 
	multilinedContent != null && multilinedCaption != null;
    }

    public boolean multilinedEditEnabled()
    {
	return multilinedEditActivated() && multilinedEnabled;
    }

    public boolean activateMultilinedEdit(String caption, String[] initialText,
					  boolean enabled)
    {
	NullCheck.notNull(caption, "caption");
	if (multilinedEditActivated())
	    return false;
	final ControlEnvironment env = environment;
	final Area thisArea = this;
	this.multilinedCaption = caption;
	multilinedContent = new DefaultMultilinedEditContent(org.luwrain.util.Strings.notNullArray(initialText)){
		@Override public void endEditTrans()
		{
		    super.endEditTrans();
		    env.onAreaNewContent(thisArea);
		}
	    };
	//FIXME:setLines;
	this.multilinedEdit = new EmbeddedMultilinedEdit(environment, multilinedContent,
							 this, items.size() + (!multilinedCaption.isEmpty()?1:0));
	multilinedEnabled = enabled;
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public String getMultilinedEditText()
    {
	return multilinedEditActivated()?multilinedContent.getWholeText():null;
    }

    public boolean removeItemOnLine(int index)
    {
	if (index < 0 || index >= items.size())
	    return false;
	items.remove(index);
	updateEditsPos();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public boolean removeItemByName(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return false;
	for(int i = 0;i < items.size();++i)
	    if (items.get(i).name.equals(itemName))
	    {
		items.remove(i);
		updateEditsPos();
		environment.onAreaNewContent(this);
		environment.onAreaNewHotPoint(this);
		return true;
	    }
	return false;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	for(Item i: items)
	    if (i.type == EDIT && i.edit != null &&
		i.enabled && i.edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		i.onKeyboardEvent(event))
		return true;
	if (multilinedEditEnabled() && multilinedEdit.isPosCovered(getHotPointX(), getHotPointY()) &&
	    multilinedEdit.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	for(Item i: items)
		if (i.type == EDIT && i.edit != null &&
		    i.enabled && i.edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		    i.onEnvironmentEvent(event))
		    return true;
	if (multilinedEditEnabled() && multilinedEdit.isPosCovered(getHotPointX(), getHotPointY()) &&
	    multilinedEdit.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	for(Item i: items)
		if (i.type == EDIT && i.edit != null &&
		    i.enabled && i.edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		    i.onAreaQuery(query))
		    return true;
	if (multilinedEditEnabled() && multilinedEdit.isPosCovered(getHotPointX(), getHotPointY()) &&
	    multilinedEdit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

    @Override public int getLineCount()
    {
	int res = items.size();
	if (multilinedEditActivated())
	{
	    res += multilinedEdit.getLineCount();
	    if (!multilinedCaption.isEmpty())
		++res;
	}
	return res + 1;
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    return "";
	if (index < items.size())
	{
	    final Item item = items.get(index);
	    switch(item.type)
	    {
	    case EDIT:
		return item.caption + item.enteredText;
	    case STATIC:
		return item.caption;
	    default:
		return "FIXME";
	    }
	}
	if (!multilinedEditActivated())
	    return "";
	final int pos = index - items.size();
	if (!multilinedCaption.isEmpty())
	{
	    if (pos == 0)
		return multilinedCaption;
	    if (pos < multilinedContent.getLineCount() + 1)
		return multilinedContent.getLine(pos - 1);
	    return "";
	}
	if (pos < multilinedContent.getLineCount())
	    return multilinedContent.getLine(pos);
	return "";
    }

    @Override public String getAreaName()
    {
	return name;
    }

    private void updateEditsPos()
    {
	for(int i = 0;i < items.size();++i)
	    if (items.get(i).type == EDIT)
	    {
		Item item = items.get(i);
		item.edit.setNewPos(item.caption != null?item.caption.length():0, i);
	    }
	if (!multilinedEditActivated())
	    return;
	int offset = items.size();
	if (!multilinedCaption.isEmpty())
	    ++offset;
	multilinedEdit.setNewPos(0, offset);
    }
}
