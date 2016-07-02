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

package org.luwrain.controls;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

/**
 * The area with a set of controls. {@code FormArea} lets the user to
 * interact with a number of controls of various types in one single
 * area. The controls can be of the following types:
 * <ul>
 * <li>Single line edits</li>
 * <li>Checkboxes</li>
 * <li>Lists</li>
 * <li>UniRefs</li>
 * <li>Static items</li>
 * <li>Multiline edit</li>
 * </ul>
 * Multiline edit can be only a single in {@code FormArea} and always
 * placed at the bottom below of all other controls. Controls of all
 * other types can be inserted multiple times and in the arbitrary order.
 * <p>
 * Each control, except of multiline edit, has associated name which
 * helps the developer reference this control. As well, each control can
 * be associated with some object given by an opaque {@code Object}
 * reference. The purpose of this object every developer may define
 * completely freely as it could be convenient for a particular purpose.
 */
public class FormArea  extends NavigationArea
{
    static public final int NONE = 0;
    static public final int EDIT = 1;
    static public final int CHECKBOX = 2;
    static public final int LIST = 3;
    static public final int STATIC = 4;
    static public final int UNIREF = 5;
    static public final int MULTILINE = 6;

    static private class Item implements EmbeddedEditLines
    {
	int type;
	String name;
	String caption;
	Object obj;
	boolean enabled = true;

//A couple of variables needed for sending notifications about changing of text
	private ControlEnvironment environment;
	private Area area;

	//For an edit
	private String enteredText = "";
	private EmbeddedSingleLineEdit edit;

	//For an uniRef
	UniRefInfo uniRefInfo;

	//For a static item
	private Object staticObject;

	//For a list
	private Object selectedListItem = null;
	private FormListChoosing listChoosing;

	//For a checkbox;
	boolean checkboxState;

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

    private MutableLinesImpl multilineEditLines;
    private final HotPointShift multilineEditHotPoint = new HotPointShift(this, 0, 0);
    private String multilineEditCaption;
    private MultilineEditModel multilineEditModel;
    private MultilineEdit multilineEdit;
    private boolean multilineEditEnabled = true;//FIXME:

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

    public void clear()
    {
	items.clear();
	multilineEditCaption = null;
	multilineEditModel = null;
	multilineEdit = null;
	multilineEditEnabled = true;
	environment.onAreaNewContent(this);
	setHotPoint(0, 0);
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
	return multilineEditActivated()?MULTILINE:NONE;
    }

    public int getItemCount()
    {
	return items.size();
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

    public boolean addEdit(String itemName, String caption)
    {
	return addEdit(itemName, caption, "");
    }

    public boolean addEdit(String itemName, String caption,
			   String initialText)
    {
	return addEdit(itemName, caption, initialText, null, true);
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

    public void setEnteredText(String itemName, String newText)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(newText, "newText");
	if (itemName.trim().isEmpty())
	    return;
	for(Item i: items)
	    if (i.type == EDIT && i.name.equals(itemName))
		i.enteredText = newText;
	environment.onAreaNewContent(this);
	//FIXME:Check if the old hot point position is still valid
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

    public String getEnteredText(int lineIndex)
    {
	if (lineIndex < 0 || lineIndex > items.size())
	    return null;
	final Item i = items.get(lineIndex);
	if (i.type == EDIT)
	    return i.enteredText;
	return null;
    }

    public boolean addUniRef(String itemName, String caption,
			   String initialUniRef, Object obj, boolean enabled)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	if (itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	final Item item = new Item(environment, this);
	item.type = UNIREF;
	item.name = itemName;
	item.caption = caption;
	if (initialUniRef != null && !initialUniRef.trim().isEmpty())
	{
	    item.uniRefInfo = environment.getUniRefInfo(initialUniRef);
	    if (item.uniRefInfo == null)
		return false;
	} else
	    item.uniRefInfo = null;

	item.obj = obj;
	item.enabled = enabled;
	items.add(item);
	updateEditsPos();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public UniRefInfo getUniRefInfo(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return null;
	for(Item i: items)
	    if (i.type == UNIREF && i.name.equals(itemName))
		return i.uniRefInfo;
	return null;
    }

    public UniRefInfo getUniRefInfo(int lineIndex)
    {
	if (lineIndex < 0 || lineIndex > items.size())
	    return null;
	final Item i = items.get(lineIndex);
	if (i.type == UNIREF)
	    return i.uniRefInfo;
	return null;
    }

    public boolean addList(String itemName, String caption,
			   Object initialSelectedItem, FormListChoosing listChoosing,
			   Object obj, boolean enabled)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(listChoosing, "listChoosing");
	if (itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	final Item item = new Item(environment, this);
	item.type = LIST;
	item.name = itemName;
	item.caption = caption;
	item.selectedListItem = initialSelectedItem;
	item.listChoosing = listChoosing;
	item.obj = obj;
	item.enabled = enabled;
	items.add(item);
	updateEditsPos();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public Object getSelectedListItem(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return null;
	for(Item i: items)
	    if (i.type == LIST && i.name.equals(itemName))
		return i.selectedListItem;
	return null;
    }

    public boolean addCheckbox(String itemName, String caption,
			       boolean initialState, Object obj, boolean enabled)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	if (itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	final Item item = new Item(environment, this);
	item.type = CHECKBOX;
	item.name = itemName;
	item.caption = caption;
	item.checkboxState = initialState;
	item.obj = obj;
	item.enabled = enabled;
	items.add(item);
	updateEditsPos();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public boolean addCheckbox(String itemName, String caption, boolean initialState)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	return addCheckbox(itemName, caption, initialState, null, true);
    }

    public boolean getCheckboxState(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return false;
	for(Item i: items)
	    if (i.type == CHECKBOX && i.name.equals(itemName))
		return i.checkboxState;
	return false;
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

    public boolean addStatic(String itemName, String caption)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	return addStatic(itemName, caption, "");
    }

    public boolean multilineEditActivated()
    {
	return multilineEdit != null && 
	multilineEditModel != null && multilineEditCaption != null;
    }

    public boolean multilineEditEnabled()
    {
	return multilineEditActivated() && multilineEditEnabled;
    }

    /**
     * Returns the {@link HotPointControl} object used in multiline edit
     * operations. The object is an instance of {@link HotPointShift} class
     * (because multiline edit is shifted vertically in the form) and can be
     * directly provided to the constructor of 
     * {@link MultilineEditModelTranslator} if necessary. This method returns the
     * object regardless whether multiline edit activated or not.
     *
     * @return The hot point control object suitable for multiline edit operations
     */
    public HotPointControl getMultilineEditHotPointControl()
    {
	return multilineEditHotPoint;
    }

    public boolean activateMultilineEdit(String caption, MultilineEditModel model,
					  boolean enabled)
    {
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(model, "model");
	if (multilineEditActivated())
	    return false;
	this.multilineEditCaption = caption;
	this.multilineEditLines = null;
	this.multilineEditModel = wrapMultilineEditModel(model);
	this.multilineEdit = new MultilineEdit(environment, model);
	multilineEditEnabled = enabled;
	updateEditsPos();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public boolean activateMultilineEdit(String caption, String lines,
					  boolean enabled)
    {
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(lines, "lines");
	if (multilineEditActivated())
	    return false;
	this.multilineEditCaption = caption;
	this.multilineEditLines = new MutableLinesImpl(lines);
	this.multilineEditModel = wrapMultilineEditModel(new MultilineEditModelTranslator(multilineEditLines, multilineEditHotPoint));
	this.multilineEdit = new MultilineEdit(environment, multilineEditModel);
	multilineEditEnabled = enabled;
	updateEditsPos();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public String getMultilineEditText()
    {
	return multilineEditLines != null?multilineEditLines.getWholeText():null;
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
	//Delete on a uniref;
	if (event.isSpecial() && event.getSpecial() == KeyboardEvent.Special.DELETE &&
	    !event.isModified()) 
	{
	    final int index = getHotPointY();
	    if (index >= 0 && index < items.size() &&
		items.get(index).type == UNIREF)
	    {
		items.get(index).uniRefInfo = null;
		environment.onAreaNewContent(this);
		return true;
	    }
	}

	if (	    event.isSpecial() && event.getSpecial() == KeyboardEvent.Special.ENTER &&
		    !event.isModified())
	{
	    //If the user is pressing Enter on the list;
	    if (getHotPointY() < items.size() && items.get(getHotPointY()).type == LIST)
	{
	    final Item item = items.get(getHotPointY());
	    final Object newSelectedItem = item.listChoosing.chooseItem(this, item.name, item.selectedListItem); 
	    if (newSelectedItem == null)
		return true;
	    item.selectedListItem = newSelectedItem;
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    return true;
	}
	    //If the user is pressing Enter on the checkbox;
	    if (getHotPointY() < items.size() && items.get(getHotPointY()).type == CHECKBOX)
	    {
	    final Item item = items.get(getHotPointY());
	    if (item.checkboxState)
	    {
		item.checkboxState = false;
		environment.say(environment.staticStr(LangStatic.NO));
	    } else
	    {
		item.checkboxState = true;
		environment.say(environment.staticStr(LangStatic.YES));
	    }
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    return true;
	    }
	}
	//If the user is typing on the caption of the edit, moving a hot point to the end of line;
	if (!event.isSpecial() && getHotPointY() < items.size())
	{
	    final int index = getHotPointY();
	    final Item item = items.get(index);
	    if (item.type == EDIT && getHotPointX() < item.caption.length())
		setHotPointX(item.caption.length() + item.enteredText.length());
	}
	for(Item i: items)
	    if (i.type == EDIT && i.edit != null &&
		i.enabled && i.edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		i.onKeyboardEvent(event))
		return true;
	if (multilineEditEnabled() && isMultilineEditCovering(getHotPointX(), getHotPointY()) &&
	    multilineEdit.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }
    
    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	//Insert command for a uniref;
	if (event.getCode() == EnvironmentEvent.Code.INSERT && (event instanceof InsertEvent))
	{
	    final int index = getHotPointY();
	    if (index >= 0 && index < items.size() &&
		items.get(index).type == UNIREF)
	    {
		final InsertEvent insertEvent = (InsertEvent)event;
		final RegionContent data = insertEvent.getData();
		if (data == null || data.strings == null ||
data.strings.length < 1 || data.strings[0] == null)
		    return false;
		final UniRefInfo uniRefInfo = environment.getUniRefInfo(data.strings[0]);
		if (uniRefInfo == null)
		    return false;
		items.get(index).uniRefInfo = uniRefInfo;
		environment.onAreaNewContent(this);
		return true;
	    }

	}

	for(Item i: items)
		if (i.type == EDIT && i.edit != null &&
		    i.enabled && i.edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		    i.onEnvironmentEvent(event))
		    return true;
	if (multilineEditEnabled() && isMultilineEditCovering(getHotPointX(), getHotPointY()) &&
	    multilineEdit.onEnvironmentEvent(event))
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
	if (multilineEditEnabled() && isMultilineEditCovering(getHotPointX(), getHotPointY()) &&
	    multilineEdit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

    @Override public int getLineCount()
    {
	int res = items.size();
	if (!multilineEditActivated())
	    return res + 1;
	final int count = multilineEditModel.getLineCount();
	res += count;
	if (count == 0)
	    ++res;
	if (multilineEditCaption != null && !multilineEditCaption.isEmpty())
	    ++res;
	return res;
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
	    case UNIREF:
		return item.caption + (item.uniRefInfo != null?item.uniRefInfo.toString():"");
	    case LIST:
		return item.caption + (item.selectedListItem != null?item.selectedListItem.toString():"");
	    case CHECKBOX:
		return item.caption + (item.checkboxState?environment.staticStr(LangStatic.YES):environment.staticStr(LangStatic.NO));
	    case STATIC:
		return item.caption;
	    default:
		return "FIXME";
	    }
	}
	if (!multilineEditActivated())
	    return "";
	final int pos = index - items.size();
	if (!multilineEditCaption.isEmpty())
	{
	    if (pos == 0)
		return multilineEditCaption;
	    if (pos < multilineEditModel.getLineCount() + 1)
		return multilineEditModel.getLine(pos - 1);
	    return "";
	}
	if (pos < multilineEditModel.getLineCount())
	    return multilineEditModel.getLine(pos);
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
		final Item item = items.get(i);
		item.edit.setNewPos(item.caption != null?item.caption.length():0, i);
	    }
	if (!multilineEditActivated())
	    return;
	int offset = items.size();
	if (multilineEditCaption != null && !multilineEditCaption.isEmpty())
	    ++offset;
	multilineEditHotPoint.setOffsetY(offset);
    }

    private MultilineEditModel wrapMultilineEditModel(MultilineEditModel model)
    {
	final ControlEnvironment env = environment;
	final Area thisArea = this;
	return new MultilineEditModelChangeListener(model){
	    @Override public void onMultilineEditChange()
	    {
		env.onAreaNewContent(thisArea);
		env.onAreaNewHotPoint(thisArea);
	    }
	};
    }

    private boolean isMultilineEditCovering(int x, int y)
    {
	return x >= multilineEditHotPoint.offsetX() && y >= multilineEditHotPoint.offsetY();
    }
}
