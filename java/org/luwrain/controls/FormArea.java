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
	public boolean enabled = true;

	private ControlEnvironment environment;//Needed for sending notifications about text changing;
	private Area area;//Needed for sending notifications about text changing;

	private String enteredText = "";
	private EmbeddedSingleLineEdit edit;

	private Object staticObject;

	public Item(ControlEnvironment environment,
		    Area area,
		    int type,
		    String name,
		    String caption,
		    boolean enabled)
	{
	    this.environment = environment;
	    this.area = area;
	    this.type = type;
	    this.name = name;
	    this.caption = caption;
	    this.enabled = enabled;
	}

	public void initEdit(String enteredText,
			     HotPointInfo hotPointInfo,
			     int posX,
			     int posY)
	{
	    if (hotPointInfo == null)
		return;
	    type = EDIT;
	    this.enteredText = enteredText != null?enteredText:"";
	    this.edit = new EmbeddedSingleLineEdit(this, hotPointInfo, posX >= 0?posX:0, posY >= 0?posY:0);
	}

	public void initStatic(Object obj)
	{
	    if (obj == null)
		return;
	    type = STATIC;
	    staticObject = obj;
	    caption = obj.toString();
	}

	public String getEnteredText()
	{
	    return enteredText != null?enteredText:"";
	}

	public boolean isPosCovered(int x, int y)
	{
	    if (edit == null)
		return false;
	    return edit.isPosCovered(x, y);
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
    private CopyCutInfo copyCutInfo;
    private String name = "";
    private Vector<Item> items = new Vector<Item>();
    public String multilinedCaption = "";
    private DefaultMultilinedEditContent multilinedContent;
    private EmbeddedMultilinedEdit multilinedEdit = null;

    public FormArea(ControlEnvironment environment)
    {
	this.environment = environment;
	//	this.copyCutInfo = new CopyCutInfo(this);
    }

    public FormArea(ControlEnvironment environment, String name)
    {
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
	if (items == null || index < 0 || index >= items.size())
	    return NONE;
	return items.get(index).type;
    }

    public String getItemNameOnLine(int index)
    {
	if (items == null || index < 0 || index >= items.size())
	    return null;
	return items.get(index).name;
    }

    public boolean addEdit(String itemName,
			   String caption,
			   String initialText,
			   boolean enabled)
    {
	if (itemName == null || itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	if (caption == null || caption.length() < 1)
	    return false;
	Item item = new Item(environment, this, EDIT, itemName, caption, enabled);
	item.initEdit(initialText, this, caption.length() + 1, items.size());
	items.add(item);
	environment.onAreaNewContent(this);
	return true;
    }

    public String getEnteredText(String itemName)
    {
	if (items == null || itemName == null || itemName.trim().isEmpty())
	    return null;
	int k;
	for(k = 0;k < items.size();++k)
	    if (items.get(k).name.equals(itemName))
		break;
	if (k >= items.size() || items.get(k).type != EDIT)
	    return null;
	return items.get(k).getEnteredText();
    }

    public boolean addStatic(String itemName, Object staticObject)
    {
	if (itemName == null || itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	if (staticObject == null)
	    return false;
	Item item = new Item(environment, this, STATIC, itemName, staticObject.toString(), true);
	item.initStatic(staticObject);
	items.add(item);
	environment.onAreaNewContent(this);
	return true;
    }

    public boolean multilinedEditActivated()
    {
	return multilinedEdit != null && 
multilinedContent != null &&
multilinedCaption != null;
    }

    public boolean activateMultilinedEdit(String caption)  
    {
	if (multilinedEditActivated())
	    return false;
	final ControlEnvironment env = environment;
	final Area thisArea = this;
	this.multilinedCaption = caption != null?caption:"";
	multilinedContent = new DefaultMultilinedEditContent(){
		private ControlEnvironment environment = env;
		private Area area = thisArea;
    @Override public void setLine(int index, String line)
		{
		    super.setLine(index, line);
		    environment.onAreaNewContent(area);
		}
    @Override public void removeLine(int index)
		{
		    super.removeLine(index);
		    environment.onAreaNewContent(area);
		}
		@Override public void insertLine(int index, String line)
		{
		    super.insertLine(index, line);
		    environment.onAreaNewContent(area);
		}
    @Override public void addLine(String line)
		{
		    super.addLine(line);
		    environment.onAreaNewContent(area);
		}
	    };
	this.multilinedEdit = new EmbeddedMultilinedEdit(multilinedContent, this, (items != null?items.size():0) +
							 (!multilinedCaption.isEmpty()?1:0));
	environment.onAreaNewContent(this);
	return true;
    }

    public String getMultilinedEditText()
    {
	return !multilinedEditActivated()?multilinedContent.getWholeText():null;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (items != null)
	    for(int i = 0;i < items.size();++i)
		if (items.get(i).type == EDIT &&
		    items.get(i).isPosCovered(getHotPointX(), getHotPointY()) &&
		    items.get(i).enabled &&
		    items.get(i).onKeyboardEvent(event))
		    return true;
	if (multilinedEditActivated() && 
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
		    items.get(i).isPosCovered(getHotPointX(), getHotPointY()) &&
		    items.get(i).enabled &&
		    items.get(i).onEnvironmentEvent(event))
		    return true;
	if (multilinedEditActivated() && 
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
	    if (multilinedEdit == null)
		return "";
	    final int offset = items != null?items.size():0;
	    final int pos = index - offset;
	    if (pos < 0)//Actually never happens;
		return "";
	    if (multilinedCaption != null && !multilinedCaption.isEmpty())
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
	    return item.caption + ":" + item.getEnteredText();
	case STATIC:
	    return item.caption;
	default:
	    return "FIXME";
	}
    }

    @Override public String getName()
    {
	return name != null?name:"";
    }
}
