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

package org.luwrain.controls;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class FormArea  extends NavigateArea
{
    public static final int NONE = 0;
    public static final int EDIT = 1;
    public static final int CHECKBOX = 2;
    public static final int LIST = 3;
    public static final int STATIC = 4;
    public static final int MULTILINED = 5;

    class Item implements EmbeddedEditLines
    {
	public int type;
    public String name;
    public String caption;
	public Object obj;
	public boolean enabled = true;

	private ControlEnvironment environment;//Needed for sending notifications about text changing;
	private Area area;//Needed for sending notifications about text changing;

	private String enteredText = "";
	private EmbeddedSingleLineEdit edit;

	private Object staticObject;

	public Item(ControlEnvironment environment,
		    Area area)
	{
	    this.environment = environment;
	    this.area = area;
	}

	boolean onKeyboardEvent(KeyboardEvent event)
	{
	    if (edit == null)
		return false;
	    return edit.onKeyboardEvent(event); 
	}

	boolean onEnvironmentEvent(EnvironmentEvent event)
	{
	    if (edit == null)
		return false;
	    return edit.onEnvironmentEvent(event); 
	}

	@Override public String getEmbeddedEditLine(int editPosX, int editPosY)
	{
	    //We may skip checking of editPosX and editPosY because there is only one edit to call this method;
	    return enteredText;
	}

	@Override public void setEmbeddedEditLine(int editPosX,
						  int editPosY,
						  String value)
	{
	    //We may skip checking of editPosX and editPosY because there is only one edit to call this method;
	    enteredText = value != null?value:"";
	    environment.onAreaNewContent(area);
	}
    }

    private ControlEnvironment environment = null;
    private String name = "";
    private Vector<Item> items = new Vector<Item>();

    public String multilinedCaption;
    private DefaultMultilinedEditContent multilinedContent;
    private EmbeddedMultilinedEdit multilinedEdit = null;
    private boolean multilinedEnabled = true;

    public FormArea(ControlEnvironment environment)
    {
	super(environment);
	this.environment = environment;
	//	this.copyCutInfo = new CopyCutInfo(this);
    }

    public FormArea(ControlEnvironment environment, String name)
    {
	super(environment);
	this.environment = environment;
	this.name = name != null?name:"";
	//	this.copyCutInfo = new CopyCutInfo(this);
    }

    public boolean hasItemWithName(String itemName)
    {
	if (items == null || itemName == null || itemName.trim().isEmpty())
	    return false;
	for(int i = 0;i < items.size();++i)
	    if (items.get(i).name.equals(itemName))
		return true;
	return false;
    }

    //For multiline zone returns "MULTILINE" on multiline caption as well (the line above the multiline edit) 
    public int getItemTypeOnLine(int index)
    {
	if (index < 0)
	    return NONE;
	if (items != null && index < items.size())
	    return items.get(index).type;
	if (items == null || index < 0 || index >= items.size())
	    return NONE;
	if (!multilinedEditActivated())
	    return NONE;
	return MULTILINED;
    }

    public String getItemNameOnLine(int index)
    {
	if (items == null || index < 0 || index >= items.size())
	    return null;
	return items.get(index).name;
    }

    public Object getItemObjOnLine(int index)
    {
	if (items == null || index < 0 || index >= items.size())
	    return null;
	return items.get(index).obj;
    }

    public Object getItemObjByName(String itemName)
    {
	if (items == null || itemName == null || itemName.trim().isEmpty())
	    return null;
	for(int i = 0;i < items.size();++i)
	    if (items.get(i).name.equals(itemName))
		return items.get(i).obj;
	return null;
    }

    public boolean addEdit(String itemName,
			   String caption,
			   String initialText,
			   Object obj,
			   boolean enabled)
    {
	if (itemName == null || itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	if (items == null)
	    items = new Vector<Item>();
	Item item = new Item(environment, this);
	item.type = EDIT;
	item.name = itemName;
	item.caption = caption != null?caption:"";
	item.enteredText = initialText != null?initialText:"";
	item.obj = obj;
	item.enabled = enabled;
	item.edit = new EmbeddedSingleLineEdit(environment, item, this, item.caption.length(), items.size());
	items.add(item);
	updateEditsPos();
	environment.onAreaNewContent(this);
	return true;
    }

    public String getEnteredText(String itemName)
    {
	if (items == null || itemName == null || itemName.trim().isEmpty())
	    return null;
	for(int i = 0;i < items.size();++i)
	    if (items.get(i).type == EDIT && items.get(i).name.equals(itemName))
		return items.get(i).enteredText;
	return null;
    }

    public boolean addStatic(String itemName, 
			     String caption,
			     Object obj)
    {
	if (itemName == null || itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	if (obj == null)
	    return false;
	if (items == null)
	    items = new Vector<Item>();
	Item item = new Item(environment, this);
	item.type = STATIC;
	item.name = itemName;
	if (caption == null)
	    item.caption = obj.toString() != null?obj.toString():""; else
	    item.caption = caption;
	item.obj = obj;
	items.add(item);
	updateEditsPos();
	environment.onAreaNewContent(this);
	return true;
    }

    public boolean multilinedEditActivated()
    {
	return multilinedEdit != null && 
	multilinedContent != null &&
	multilinedCaption != null;
    }

    public boolean multilinedEditEnabled()
    {
	return multilinedEditActivated() && multilinedEnabled;
    }

    public boolean activateMultilinedEdit(String caption, 
					  String[] initialText,
					  boolean enabled)
    {
	if (multilinedEditActivated())
	    return false;
	final ControlEnvironment env = environment;
	final Area thisArea = this;
	this.multilinedCaption = caption != null?caption:"";
	multilinedContent = new DefaultMultilinedEditContent(initialText != null?initialText:new String[0]){
		private ControlEnvironment environment = env;
		private Area area = thisArea;
		@Override public void endEditTrans()
		{
		    super.endEditTrans();
		    env.onAreaNewContent(thisArea);
		}
	    };
	//FIXME:setLines;
	this.multilinedEdit = new EmbeddedMultilinedEdit(environment,
							 multilinedContent,
							 this,
							 (items != null?items.size():0) +
							 (!multilinedCaption.isEmpty()?1:0));
	multilinedEnabled = enabled;
	environment.onAreaNewContent(this);
	return true;
    }

    public String getMultilinedEditText()
    {
	return !multilinedEditActivated()?multilinedContent.getWholeText():null;
    }

    public boolean removeItemOnLine(int index)
    {
	if (items == null || index < 0 || index >= items.size())
	    return false;
	items.remove(index);
	updateEditsPos();
	environment.onAreaNewContent(this);
	return true;
    }

    public boolean removeItemByName(String itemName)
    {
	if (items == null || itemName == null || itemName.trim().isEmpty())
	    return false;
	for(int i = 0;i < items.size();++i)
	    if (items.get(i).name.equals(itemName))
	    {
		items.remove(i);
		updateEditsPos();
		environment.onAreaNewContent(this);
		return true;
	    }
	return false;
    }


    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (items != null)
	    for(int i = 0;i < items.size();++i)
		if (items.get(i).type == EDIT &&
		    items.get(i).edit != null &&
		    items.get(i).edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		    items.get(i).enabled &&
		    items.get(i).onKeyboardEvent(event))
		    return true;
	if (multilinedEditEnabled() && 
	    multilinedEdit.isPosCovered(getHotPointX(), getHotPointY()) &&
	    multilinedEdit.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (items != null)
	    for(int i = 0;i < items.size();++i)
		if (items.get(i).type == EDIT &&
		    items.get(i).edit != null &&
		    items.get(i).edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		    items.get(i).enabled &&
		    items.get(i).onEnvironmentEvent(event))
		    return true;
	if (multilinedEditEnabled() && 
	    multilinedEdit.isPosCovered(getHotPointX(), getHotPointY()) &&
	    multilinedEdit.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public int getLineCount()
    {
	final int multilinedEditCount = multilinedEditActivated()?multilinedEdit.getLineCount():0;
	return (items != null?items.size():0) +
	(multilinedEditActivated() && !multilinedCaption.isEmpty()?1:0) +
	(multilinedEditCount > 0?multilinedEditCount:1);
    }

    @Override public String getLine(int index)
    {
	if (items == null || index >= items.size())
	{
	    if (!multilinedEditActivated())
		return "";
	    final int offset = items != null?items.size():0;
	    final int pos = index - offset;
	    if (pos < 0)//Actually never happens;
		return "";
	    if (!multilinedCaption.isEmpty())
	    {
		if (pos == 0)
		    return multilinedCaption;
		if (pos < multilinedContent.getLineCount() + 1)
		    return multilinedContent.getLine(pos - 1);
	    } else 
		if (pos < multilinedContent.getLineCount())
		    return multilinedContent.getLine(pos);
	    return "";
	}
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

    @Override public String getAreaName()
    {
	return name != null?name:"";
    }

    private void updateEditsPos()
    {
	if (items != null)
	    for(int i = 0;i < items.size();++i)
		if (items.get(i).type == EDIT)
		{
		    Item item = items.get(i);
		    item.edit.setNewPos(item.caption != null?item.caption.length():0, i);
		}
	if (!multilinedEditActivated())
	    return;
	int offset = items != null?items.size():0;
	if (!multilinedCaption.isEmpty())
	    ++offset;
	multilinedEdit.setNewPos(0, offset);
    }
}
