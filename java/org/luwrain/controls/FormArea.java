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

public class FormArea  extends NavigateArea implements HotPointInfo
{
    public static final int EDIT = 1;
    public static final int CHECKBOX = 2;
    public static final int LIST = 3;

    class Item implements EmbeddedEditLines
    {
	public int type;
    public String name;
    public String caption;
	public boolean enabled = true;

	private String enteredText = "";
	private EmbeddedSingleLineEdit edit;

	public Item(int type,
		    String name,
		    String caption,
		    boolean enabled)
	{
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
	    type = EDIT;
	    this.enteredText = enteredText != null?enteredText:"";
	    this.edit = new EmbeddedSingleLineEdit(this, hotPointInfo, posX >= 0?posX:0, posY >= 0?posY:0);
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
	}
    }

    private ControlEnvironment environment = null;
    private CopyCutInfo copyCutInfo;
    private String name = "";
    private Vector<Item> items = new Vector<Item>();

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

    public boolean addEdit(String itemName,
			   String caption,
			   String initialText,
			   boolean enabled)
    {
	if (itemName == null || itemName.trim().isEmpty() && hasItemWithName(itemName))
	    return false;
	if (caption == null || caption.length() < 1)
	    return false;
	Item item = new Item(EDIT, itemName, caption, enabled);
	item.initEdit(initialText, this, caption.length() + 1, items.size());
	items.add(item);
	return true;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	for(int i = 0;i < items.size();++i)
	    if (items.get(i).type == EDIT &&
		items.get(i).isPosCovered(getHotPointX(), getHotPointY()) &&
		items.get(i).enabled)
		if (items.get(i).onKeyboardEvent(event))
		    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	for(int i = 0;i < items.size();++i)
	    if (items.get(i).type == EDIT &&
		items.get(i).isPosCovered(getHotPointX(), getHotPointY()) &&
		items.get(i).enabled)
		if (items.get(i).onEnvironmentEvent(event))
		    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public int getLineCount()
    {
	return items != null?items.size() + 1:1;
    }

    @Override public String getLine(int index)
    {
	if (items == null || index >= items.size())
	    return "";
	final Item item = items.get(index);
	switch(item.type)
	{
	case EDIT:
	    return item.caption + ":" + item.getEnteredText();
	default:
	    return "FIXME";
	}
    }

    @Override public void setHotPointX(int value)
    {
	if (value >= 0)
	    super.setHotPoint(value, getHotPointY());
    }

    @Override public void setHotPointY(int value)
    {
	if (value >= 0)
	    super.setHotPoint(getHotPointX(), value);
    }

    @Override public String getName()
    {
	return "FIXME";
    }
}
