/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

//LWR_API 1.0

package org.luwrain.controls;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.i18n.LangStatic;//FIXME:deleting

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
    public enum Type { EDIT, CHECKBOX, LIST, STATIC, UNIREF, MULTILINE };

    protected final ControlContext context;
    protected final Vector<Item> items = new Vector<Item>();
    protected String name = "";

    protected MutableLines multilineEditLines = null;
        protected MultilineEdit multilineEdit = null;
    protected final HotPointShift multilineEditHotPoint = new HotPointShift(this, 0, 0);
    protected final RegionPointShift multilineEditRegionPoint = new RegionPointShift(regionPoint, 0, 0);
    protected String multilineEditCaption = "";
    protected boolean multilineEditEnabled = true;//FIXME:

    public FormArea(ControlContext context)
    {
	super(context);
	NullCheck.notNull(context, "context");
	this.context = context;
	this.name = "";
    }

    public FormArea(ControlContext context, String name)
    {
	super(context);
	NullCheck.notNull(context, "context");
	NullCheck.notNull(name, "name");
	this.context = context;
	this.name = name;
    }

    public void clear()
    {
	items.clear();
	multilineEditCaption = "";
	multilineEdit = null;
	multilineEditEnabled = true;
	context.onAreaNewContent(this);
	setHotPoint(0, 0);
    }

    public boolean hasItemWithName(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.isEmpty())
	    return false;
	for(Item i: items)
	    if (i.name.equals(itemName))
		return true;
	return false;
    }

    //For multiline zone returns "MULTILINE" on multiline caption as well (the line above the multiline edit) 
    public Type getItemTypeOnLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index may not be negative");
	if (index < items.size())
	    return items.get(index).type;
	return isMultilineEditActivated()?Type.MULTILINE:null;
    }

    public int getItemCount()
    {
	return items.size();
    }

    public String getItemNameOnLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index may not be negative");
	if (index >= items.size())
	    return null;
	return items.get(index).name;
    }

    public Object getItemObj(int index)
    {
	if (index < 0 || index >= items.size())
	    throw new IllegalArgumentException("index (" + String.valueOf(index) + ") is out of bounds");
	return items.get(index).obj;
    }

    public Object getItemObjByName(String itemName)
    {
	NullCheck.notEmpty(itemName, "itemName");
	for(Item i: items)
	    if (i.name.equals(itemName))
		return i.obj;
	return null;
    }

    public boolean addEdit(String itemName, String caption)
    {
	NullCheck.notEmpty(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	return addEdit(itemName, caption, "");
    }

    public boolean addEdit(String itemName, String caption, String initialText)
    {
	NullCheck.notEmpty(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(initialText, "initialText");
	return addEdit(itemName, caption, initialText, null, true);
    }

    public boolean addEdit(String itemName, String caption,
			   String initialText, Object obj, boolean enabled)
    {
	NullCheck.notEmpty(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(initialText, "initialText");
	final Item item = new Item(context, this, Type.EDIT, itemName);
	item.caption = caption;
	item.enteredText = initialText;
	item.obj = obj;
	item.enabled = enabled;
	item.edit = new EmbeddedSingleLineEdit(context, item, this, regionPoint,
					       item.caption.length(), //offsetX
					       items.size()); //offsetY
	items.add(item);
	updateItems();
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
	return true;
    }

    public void setEnteredText(String itemName, String newText)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(newText, "newText");
	if (itemName.trim().isEmpty())
	    return;
	for(Item i: items)
	    if (i.type == Type.EDIT && i.name.equals(itemName))
		i.enteredText = newText;
	context.onAreaNewContent(this);
	//FIXME:Check if the old hot point position is still valid
    }

    public String getEnteredText(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return null;
	for(Item i: items)
	    if (i.type == Type.EDIT && i.name.equals(itemName))
		return i.enteredText;
	return null;
    }

    public String getEnteredText(int lineIndex)
    {
	if (lineIndex < 0 || lineIndex > items.size())
	    return null;
	final Item i = items.get(lineIndex);
	if (i.type == Type.EDIT)
	    return i.enteredText;
	return null;
    }

    public boolean addUniRef(String itemName, String caption,
			   String initialUniRef, Object obj, boolean enabled)
    {
	NullCheck.notEmpty(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	final Item item = new Item(context, this, Type.UNIREF, itemName);
	item.caption = caption;
	if (initialUniRef != null && !initialUniRef.trim().isEmpty())
	{
	    item.uniRefInfo = context.getUniRefInfo(initialUniRef);
	    if (item.uniRefInfo == null)
		return false;
	} else
	    item.uniRefInfo = null;
	item.obj = obj;
	item.enabled = enabled;
	items.add(item);
	updateItems();
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
	return true;
    }

    public UniRefInfo getUniRefInfo(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return null;
	for(Item i: items)
	    if (i.type == Type.UNIREF && i.name.equals(itemName))
		return i.uniRefInfo;
	return null;
    }

    public UniRefInfo getUniRefInfo(int lineIndex)
    {
	if (lineIndex < 0 || lineIndex > items.size())
	    return null;
	final Item i = items.get(lineIndex);
	if (i.type == Type.UNIREF)
	    return i.uniRefInfo;
	return null;
    }

    public boolean addList(String itemName, String caption,
			   Object initialSelectedItem, ListChoosing listChoosing,
			   Object obj, boolean enabled)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(listChoosing, "listChoosing");
	if (itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	final Item item = new Item(context, this, Type.LIST, itemName);
	item.caption = caption;
	item.selectedListItem = initialSelectedItem;
	item.listChoosing = listChoosing;
	item.obj = obj;
	item.enabled = enabled;
	items.add(item);
	updateItems();
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
	return true;
    }

    public Object getSelectedListItem(String itemName)
    {
	NullCheck.notNull(itemName, "itemName");
	if (itemName.trim().isEmpty())
	    return null;
	for(Item i: items)
	    if (i.type == Type.LIST && i.name.equals(itemName))
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
	final Item item = new Item(context, this, Type.CHECKBOX, itemName);
	item.caption = caption;
	item.checkboxState = initialState;
	item.obj = obj;
	item.enabled = enabled;
	items.add(item);
	updateItems();
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
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
	    if (i.type == Type.CHECKBOX && i.name.equals(itemName))
		return i.checkboxState;
	return false;
    }

    public boolean addStatic(String itemName, String caption, Object obj)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	if (itemName.trim().isEmpty() || hasItemWithName(itemName))
	    return false;
	final Item item = new Item(context, this, Type.STATIC, itemName);
	    item.caption = caption;
	    item.obj = obj;
	    items.add(item);
	    updateItems();
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
	return true;
    }

    public boolean addStatic(String itemName, String caption)
    {
	NullCheck.notNull(itemName, "itemName");
	NullCheck.notNull(caption, "caption");
	return addStatic(itemName, caption, "");
    }

    public boolean isMultilineEditActivated()
    {
	return multilineEdit != null;
    }

    public boolean isMultilineEditEnabled()
    {
	return isMultilineEditActivated() && multilineEditEnabled;
    }

    public boolean multilineEditHasCaption()
    {
	return multilineEditCaption != null && !multilineEditCaption.isEmpty();
    }

    public MultilineEdit.Params createMultilineEditParams(ControlContext context, MutableLines lines)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(lines, "lines");
	final MultilineEdit.Params params = new MultilineEdit.Params();
	params.context = context;
	params.model = new EditUtils.CorrectorChangeListener(new MultilineEditCorrectorTranslator(lines, multilineEditHotPoint)){
		@Override public void onMultilineEditChange()
		{
		    context.onAreaNewContent(FormArea.this);
		    context.onAreaNewHotPoint(FormArea.this);
		}
	    };
	params.appearance = new EditUtils.DefaultMultilineEditAppearance(context);
	params.regionPoint = regionPoint;
	return params;
    }

    public boolean activateMultilineEdit(String caption, MutableLines lines, MultilineEdit.Params params, boolean enabled)
    {
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(params, "params");
	if (isMultilineEditActivated())
	    return false;
	this.multilineEditCaption = caption;
	this.multilineEditLines = lines;
	this.multilineEdit = new MultilineEdit(params);
	multilineEditEnabled = enabled;
	updateItems();
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
	return true;
    }

    public boolean activateMultilineEdit(String caption, String lines, boolean enabled)
    {
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(lines, "lines");
final MutableLines linesImpl = new MutableLinesImpl(lines);
final MultilineEdit.Params params = createMultilineEditParams(context, linesImpl);
return activateMultilineEdit(caption, linesImpl, params, enabled);
    }

        public boolean activateMultilineEdit(String caption, String[] lines, boolean enabled)
    {
	NullCheck.notNull(caption, "caption");
	NullCheck.notNullItems(lines, "lines");
final MutableLines linesImpl = new MutableLinesImpl(lines);
final MultilineEdit.Params params = createMultilineEditParams(context, linesImpl);
return activateMultilineEdit(caption, linesImpl, params, enabled);
    }

    public boolean activateMultilineEdit(String caption, String lines)
    {
	NullCheck.notNull(caption, "caption");
	NullCheck.notNull(lines, "lines");
	return activateMultilineEdit(caption, lines, true);
    }

    public String getMultilineEditText()
    {
	if (multilineEditLines  == null)
	    return null;
final int count = multilineEditLines.getLineCount();
	if (count == 0)
	    return "";
		final String lineSep = System.lineSeparator();
	final StringBuilder b = new StringBuilder();
	b.append(multilineEditLines.getLine(0));
	for(int i = 1; i < count;i++)
	    b.append(lineSep).append(multilineEditLines.getLine(i));
	return new String(b);
    }

        public String[] getMultilineEditLines()
    {
	return multilineEditLines != null?multilineEditLines.getLines():new String[0];
    }

    public boolean removeItemOnLine(int index)
    {
	if (index < 0 || index >= items.size())
	    return false;
	items.remove(index);
	updateItems();
	context.onAreaNewContent(this);
	context.onAreaNewHotPoint(this);
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
		updateItems();
		context.onAreaNewContent(this);
		context.onAreaNewHotPoint(this);
		return true;
	    }
	return false;
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	//Delete on a uniref;
	if (event.isSpecial() && event.getSpecial() == KeyboardEvent.Special.DELETE &&
	    !event.isModified()) 
	{
	    final int index = getHotPointY();
	    if (index >= 0 && index < items.size() &&
		items.get(index).type == Type.UNIREF)
	    {
		items.get(index).uniRefInfo = null;
		context.onAreaNewContent(this);
		return true;
	    }
	}
		    //If the user is pressing Enter on the list;
	if (	    event.isSpecial() && event.getSpecial() == KeyboardEvent.Special.ENTER && !event.isModified())
	{
	    if (getHotPointY() < items.size() && items.get(getHotPointY()).type == Type.LIST)
	{
	    final Item item = items.get(getHotPointY());
	    final Object newSelectedItem = item.listChoosing.chooseFormListItem(this, item.name, item.selectedListItem); 
	    if (newSelectedItem == null)
		return true;
	    item.selectedListItem = newSelectedItem;
	    context.onAreaNewContent(this);
	    context.onAreaNewHotPoint(this);
	    return true;
	}
	    //If the user is pressing Enter on the checkbox;
	    if (getHotPointY() < items.size() && items.get(getHotPointY()).type == Type.CHECKBOX)
	    {
	    final Item item = items.get(getHotPointY());
	    if (item.checkboxState)
	    {
		item.checkboxState = false;
		context.say(context.staticStr(LangStatic.NO));
	    } else
	    {
		item.checkboxState = true;
		context.say(context.staticStr(LangStatic.YES));
	    }
	    context.onAreaNewContent(this);
	    context.onAreaNewHotPoint(this);
	    return true;
	    }
	}
	//If the user is typing on the caption of the edit, moving a hot point to the end of line;
	if (!event.isSpecial() && getHotPointY() < items.size())
	{
	    final int index = getHotPointY();
	    final Item item = items.get(index);
	    if (item.type == Type.EDIT && getHotPointX() < item.caption.length())
		setHotPointX(item.caption.length() + item.enteredText.length());
	}
	for(Item i: items)
	    if (i.type == Type.EDIT && i.edit != null &&
		i.enabled && i.edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		i.onInputEvent(event))
		return true;
	if (isMultilineEditEnabled() && isMultilineEditCovering(getHotPointX(), getHotPointY()))
	    {
		if (org.luwrain.script.TextScriptUtils.runMultilineEditInputEventHook(context, EditArea.INPUT_EVENT_HOOK, this, multilineEdit, event, multilineEditRegionPoint))
		    return true;
		if (multilineEdit.onInputEvent(event))
	    return true;
	    }
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	//Insert command for a uniref

	/*
	if (event.getCode() == EnvironmentEvent.Code.INSERT && (event instanceof InsertEvent))
	{
	    final int index = getHotPointY();
	    if (index >= 0 && index < items.size() &&
		items.get(index).type == Type.UNIREF)
	    {
		final InsertEvent insertEvent = (InsertEvent)event;
		final RegionContent data = insertEvent.getData();
		if (data.isEmpty())
		    return false;
		final UniRefInfo uniRefInfo = context.getUniRefInfo(data.strings()[0]);
		if (uniRefInfo == null)
		    return false;
		items.get(index).uniRefInfo = uniRefInfo;
		context.onAreaNewContent(this);
		return true;
	    }
	}
	*/

	for(Item i: items)
	    if (i.isEnabledEdit() && i.edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		i.onSystemEvent(event))
		return true;
	if (isMultilineEditEnabled() && isMultilineEditCovering(getHotPointX(), getHotPointY()) &&
	    multilineEdit.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	for(Item i: items)
	    if (i.isEnabledEdit() && i.edit.isPosCovered(getHotPointX(), getHotPointY()) &&
		    i.onAreaQuery(query))
		    return true;
	if (isMultilineEditEnabled() && isMultilineEditCovering(getHotPointX(), getHotPointY()) &&
	    multilineEdit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

    @Override public int getLineCount()
    {
	int res = items.size();
	if (!isMultilineEditActivated())
	    return res + 1;
	final int count = multilineEditLines.getLineCount();
	res += count;
	if (count == 0)
	    ++res;
	if (multilineEditHasCaption())
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
		return item.caption + (item.checkboxState?context.staticStr(LangStatic.YES):context.staticStr(LangStatic.NO));
	    case STATIC:
		return item.caption;
	    default:
		return "FIXME";
	    }
	}
	if (!isMultilineEditActivated())
	    return "";
	final int pos = index - items.size();
	if (multilineEditHasCaption())
	{
	    if (pos == 0)
		return multilineEditCaption;
	    if (pos < multilineEditLines.getLineCount() + 1)
		return multilineEditLines.getLine(pos - 1);
	    return "";
	}
	if (pos < multilineEditLines.getLineCount())
	    return multilineEditLines.getLine(pos);
	return "";
    }

    @Override public String getAreaName()
    {
	return name;
    }

    public void setAreaName(String name)
    {
	NullCheck.notNull(name, "name");
	this.name = name;
	//FIXME:	context.onNewAreaName(this);
    }

    protected Item findItemByIndex(int index)
    {
	if (index < 0 || index >= items.size())
	    return null;
	return items.get(index);
    }

    protected Item findItemByName(String itemName)
    {
	NullCheck.notEmpty(itemName, "itemName");
	for(Item i: items)
	    if (i.name.equals(itemName))
		return i;
	return null;
    }

    protected void updateItems()
    {
	for(int i = 0;i < items.size();++i)
	    if (items.get(i).type == Type.EDIT)
	    {
		final Item item = items.get(i);
		item.edit.setNewOffset(item.caption.length(), i);
	    }
	if (!isMultilineEditActivated())
	    return;
	int offset = items.size();
	if (multilineEditHasCaption())
	    ++offset;
	multilineEditHotPoint.setOffsetY(offset);
		multilineEditRegionPoint.setOffsetY(offset);
    }

    protected boolean isMultilineEditCovering(int x, int y)
    {
	return x >= multilineEditHotPoint.offsetX() && y >= multilineEditHotPoint.offsetY();
    }

    public interface ListChoosing
    {
	Object chooseFormListItem(Area area, String formItemName, Object currentSelected);
    }

    static protected class Item implements EmbeddedEditLines
    {
	final Type type;
	final String name;
	String caption;
	Object obj;
	boolean enabled = true;
//A couple of variables needed for sending notifications about changing of text
	protected final ControlContext context;
	protected final Area area;
	//For edits
	protected String enteredText = "";
	protected EmbeddedSingleLineEdit edit;
	//For unirefs
	UniRefInfo uniRefInfo;
	//For static items
	protected Object staticObject;
	//For lists
protected Object selectedListItem = null;
protected ListChoosing listChoosing;
	//For checkboxes
	boolean checkboxState;
	Item(ControlContext context, Area area, Type type, String name)
	{
	    NullCheck.notNull(context, "context");
	    NullCheck.notNull(area, "area");
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(name, "name");
	    this.context = context;
	    this.area = area;
	    this.type = type;
	    this.name = name;
	}
	boolean onInputEvent(KeyboardEvent event)
	{
	    return edit != null?edit.onInputEvent(event):false;
	}
	boolean onSystemEvent(EnvironmentEvent event)
	{
	    return edit != null?edit.onSystemEvent(event):false;
	}
	boolean onAreaQuery(AreaQuery query)
	{
	    return edit != null?edit.onAreaQuery(query):false;
	}
	boolean isEnabledEdit()
	{
	    return type == Type.EDIT && edit != null && enabled;
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
	    context.onAreaNewContent(area);
	}
    }
}
