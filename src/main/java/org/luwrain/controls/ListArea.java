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

package org.luwrain.controls;

//FIXME:DESCRIBE;
//FIXME:ControlEnvironment interface support;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class ListArea  implements Area, CopyCutRequest
{
    static final public int BRIEF_VALUE = 1;
    static final public int CLIPBOARD_VALUE = 2;

    private ControlEnvironment environment;
    private String name = "";
    private ListModel model = null;
    private ListItemAppearance appearance = null;
    private ListClickHandler clickHandler = null;
    private int initialHotPointX = 0;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private CopyCutInfo copyCutInfo;

    private String noItemsAbove = Langs.staticValue(Langs.BEGIN_OF_LIST);
    private String noItemsBelow = Langs.staticValue(Langs.END_OF_LIST);
    private String beginOfLine = Langs.staticValue(Langs.BEGIN_OF_LINE);
    private String endOfLine = Langs.staticValue(Langs.END_OF_LINE);
    private String noItems = Langs.staticValue(Langs.LIST_NO_ITEMS);
    private String emptyLine = Langs.staticValue(Langs.EMPTY_LINE);

    public ListArea(ControlEnvironment environment, ListModel model)
    {
	this.environment = environment;
	this.model = model;
	this.copyCutInfo = new CopyCutInfo(this);
    }

    public ListArea(ControlEnvironment environment,
		    ListModel model,
		    String name)
    {
	this.environment = environment;
	this.model = model;
	this.name = name != null?name:"";
	this.copyCutInfo = new CopyCutInfo(this);
    }

    public ListArea(ControlEnvironment environment,
		    ListModel model,
		    String name,
		    ListItemAppearance appearance,
		    ListClickHandler clickHandler,
		    int initialHotPointX)
    {
	this.environment = environment;
	this.model = model;
	this.name = name != null?name:"";
	this.appearance = appearance;
	this.clickHandler = clickHandler;
	this.initialHotPointX = initialHotPointX;
	this.copyCutInfo = new CopyCutInfo(this);
	if (initialHotPointX > 0 && model != null &&
	    model.getItemCount() > 0)
	{
	    String line = getScreenAppearance(model, 0, model.getItem(0), 0);
	    if (line == null)
		line = "";
	    hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
	}
    }

    public Object getSelectedObject()
    {
	return hotPointY < model.getItemCount()?model.getItem(hotPointY):null;
    }

    public int getSelectedIndex()
    {
	if (model == null ||
	    model.getItemCount() < 1 &&
	    hotPointY < 0 ||
	    hotPointY >= model.getItemCount())
	    return -1;
	return hotPointY;
    }

    public void setSelectedIndex(int index, boolean introduce)
    {
	if (model == null)
	    return;
	if (index < 0)
	    hotPointY = 0; else
	    if (index >= model.getItemCount())
		hotPointY = model.getItemCount(); else
		hotPointY = index;
	if (hotPointY >= 0 && hotPointY < model.getItemCount())
	{
	    String line = getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0);
	    if (line == null)
		line = "";
	    hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
	    if (introduce)
		introduceItem(model, hotPointY, model.getItem(hotPointY), 0);
	} else
	{
	    hotPointX = 0;
	    if (introduce)
		Speech.say(emptyLine, Speech.PITCH_HIGH);
	}
	environment.onAreaNewHotPoint(this);
    }

    public void refresh()
    {
	if (model == null)
	    return;
	model.refresh();
	hotPointY = hotPointY < model.getItemCount()?hotPointY:model.getItemCount();
	if (hotPointY >= 0 && hotPointY < model.getItemCount())
	{
	    String line = getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0);
	    if (line == null)
		line = "";
	    hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
	} else
	    hotPointX = 0;
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (!event.isCommand() || event.isModified())
	    return false;
	switch(event.getCommand())
	{
	case KeyboardEvent.ARROW_DOWN:
	    return onArrowDown(event, false);
	case KeyboardEvent.ARROW_UP:
	    return onArrowUp(event, false);
	case KeyboardEvent.ARROW_RIGHT:
	    return onArrowRight(event);
	case KeyboardEvent.ARROW_LEFT:
	    return onArrowLeft(event);
	case KeyboardEvent.ALTERNATIVE_ARROW_DOWN:
	    return onArrowDown(event, true);
	case KeyboardEvent.ALTERNATIVE_ARROW_UP:
	    return onArrowUp(event, true);
	    //FIXME:case KeyboardEvent.ALTERNATIVE_ARROW_RIGHT:
	    //FIXME:case KeyboardEvent.ALTERNATIVE_ARROW_LEFT:
	case KeyboardEvent.HOME:
	    return onHome(event);
	case KeyboardEvent.END:
	    return onEnd(event);
	case KeyboardEvent.ALTERNATIVE_HOME:
	    return onLineHome(event);
	case KeyboardEvent.ALTERNATIVE_END:
	    return onLineEnd(event);
	case KeyboardEvent.PAGE_DOWN:
	    return onPageDown(event, false);
	case KeyboardEvent.PAGE_UP:
	    return onPageUp(event, false);
	case KeyboardEvent.ALTERNATIVE_PAGE_DOWN:
	    return onPageDown(event, true);
	case KeyboardEvent.ALTERNATIVE_PAGE_UP:
	    return onPageUp(event, true);
	case KeyboardEvent.ENTER:
	    if (model == null || 
		hotPointY < 0 || hotPointY >= model.getItemCount())
		return false;
	    return onClick(model, hotPointY, model.getItem(hotPointY));
	default:
	    return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch (event.getCode())
	{
	case EnvironmentEvent.REFRESH:
	    refresh();
	    return true;
	case EnvironmentEvent.COPY_CUT_POINT:
	    return copyCutInfo.doCopyCutPoint(hotPointX, hotPointY);
	case EnvironmentEvent.COPY:
	    if (!copyCutInfo.doCopy(hotPointX, hotPointY))
		copyEntireContent();
	    return true;
	default:
	    return false;
	}
    }

    @Override public int getLineCount()
    {
	if (model == null || model.getItemCount() <= 0)
	    return 2;
	return model.getItemCount() + 1;
    }

    @Override public String getLine(int index)
    {
	if (model == null || 
	    model.getItemCount() < 1)
	    return index == 0?noItems:"";
	if (index < 0 ||
	    index >= model.getItemCount() ||
	    model.getItem(index) == null ||
	    model.getItem(index).toString() == null)
	    return "";
	return model.getItem(index).toString();
    }

    protected boolean onClick(ListModel model,
				     int index,
				     Object item)
    {
	if (clickHandler != null)
	    return clickHandler.onClick(model, index, item);
	return false;
    }

    @Override public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    @Override public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    @Override public String getName()
    {
	return name != null?name:"";
    }

    public void setName(String value)
    {
	name = value != null?value:"";
	environment.onAreaNewName(this);
    }

    protected void introduceItem(ListModel model,
				 int index,
				 Object item,
				 int flags)
    {
	if (appearance != null)
	{
	    appearance.introduceItem(model, index, item, flags);
	    return;
	}
	String value = item != null?item.toString():"";
	if (value == null)
	    value = "";
	if (value.trim().isEmpty())
		Speech.say(emptyLine, Speech.PITCH_HIGH); else
	    Speech.say(value);
    }

    protected String getScreenAppearance(ListModel model,
					 int index,
					 Object item,
					 int flags)
    {
	if (appearance != null)
	    return appearance.getScreenAppearance(model, index, item, flags);
	String value = item != null?item.toString():"";
	return value != null?value:"";
    }

    private boolean onArrowDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= model.getItemCount())
	{
		Speech.say(noItemsBelow, Speech.PITCH_HIGH);
		return true;
	}
	++hotPointY;
	String line = hotPointY < model.getItemCount()?getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0):"";
	if (line == null)
	    line = "";
	hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
	environment.onAreaNewHotPoint(this);
	if (hotPointY < model.getItemCount())
	    introduceItem(model, hotPointY, model.getItem(hotPointY), briefIntroduction?BRIEF_VALUE:0); else
		Speech.say(emptyLine, Speech.PITCH_HIGH);
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY <= 0)
	{
	    Speech.say(noItemsAbove, Speech.PITCH_HIGH);
	    return true;
	}
	    --hotPointY;
	    if (hotPointY >= model.getItemCount())
		hotPointY = model.getItemCount() - 1;
	    String line = hotPointY < model.getItemCount()?getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0):"";
	if (line == null)
	    line = "";
	hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
	environment.onAreaNewHotPoint(this);
	introduceItem(model, hotPointY, model.getItem(hotPointY), briefIntroduction?BRIEF_VALUE:0);
	return true;
    }

    private boolean onPageDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= model.getItemCount())
	{
		Speech.say(noItemsBelow, Speech.PITCH_HIGH);
		return true;
	}
	hotPointY += environment.getAreaVisibleHeight(this);
	if (hotPointY >= model.getItemCount())
	    hotPointY = model.getItemCount();
	String line = hotPointY < model.getItemCount()?getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0):"";
	if (line == null)
	    line = "";
	hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
	environment.onAreaNewHotPoint(this);
	if (hotPointY < model.getItemCount())
	    introduceItem(model, hotPointY, model.getItem(hotPointY), briefIntroduction?BRIEF_VALUE:0); else
		Speech.say(emptyLine, Speech.PITCH_HIGH);
	return true;
    }

    private boolean onPageUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY <= 0)
	{
	    Speech.say(noItemsAbove, Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY -= environment.getAreaVisibleHeight(this);
	if (hotPointY < 0)
	    hotPointY = 0;
	String line = hotPointY < model.getItemCount()?getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0):"";
	if (line == null)
	    line = "";
	hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
	environment.onAreaNewHotPoint(this);
	introduceItem(model, hotPointY, model.getItem(hotPointY), briefIntroduction?BRIEF_VALUE:0);
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY = model.getItemCount();
	hotPointX = 0;
	environment.onAreaNewHotPoint(this);
	Speech.say(emptyLine, Speech.PITCH_HIGH);
	return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY = 0;
	String line = getScreenAppearance(model, 0, model.getItem(0), 0);
	if (line == null)
	    line = "";
	hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
	environment.onAreaNewHotPoint(this);
	introduceItem(model, 0, model.getItem(0), 0);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY < 0 ||
	    hotPointY >= model.getItemCount() ||
	    model.getItem(hotPointY) == null)
	{
	    Speech.say(emptyLine, Speech.PITCH_HIGH);
	    return true;
	}
	String line = hotPointY < model.getItemCount()?getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0):"";
	if (line == null)
	    line = "";
	if (hotPointX >= line.length())
	{
	    if (hotPointX > line.length())
	    {
		hotPointX = line.length();
		environment.onAreaNewHotPoint(this);
	    }
	    Speech.say(endOfLine, Speech.PITCH_HIGH);
	    return true;
	}
	++hotPointX;
	environment.onAreaNewHotPoint(this);
	if (hotPointX >= line.length())
	    Speech.say(endOfLine, Speech.PITCH_HIGH); else
	    Speech.sayLetter(line.charAt(hotPointX));
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY < 0 ||
	    hotPointY >= model.getItemCount() ||
	    model.getItem(hotPointY) == null)
	{
	    Speech.say(emptyLine, Speech.PITCH_HIGH);
	    return true;
	}
	String line = hotPointY < model.getItemCount()?getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0):"";
	if (line == null)
	    line = "";
	if (hotPointX <= (initialHotPointX < line.length()?initialHotPointX:line.length()))
	{
	    if (hotPointX < (initialHotPointX < line.length()?initialHotPointX:line.length()))
	    {
		hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
		environment.onAreaNewHotPoint(this);
	    }
	    Speech.say(beginOfLine, Speech.PITCH_HIGH);
	    return true;
	}
	--hotPointX;
	if (hotPointX > line.length())
	    hotPointX = line.length();
	environment.onAreaNewHotPoint(this);
	if (hotPointX >= line.length())
	    Speech.say(endOfLine, Speech.PITCH_HIGH); else
	    Speech.sayLetter(line.charAt(hotPointX));
	return true;
    }

    private boolean onLineEnd(KeyboardEvent event)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY < 0 ||
	    hotPointY >= model.getItemCount() ||
	    model.getItem(hotPointY) == null)
	{
	    Speech.say(emptyLine, Speech.PITCH_HIGH);
	    return true;
	}
	String line = hotPointY < model.getItemCount()?getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0):"";
	if (line == null)
	    line = "";
	hotPointX = line.length();
	environment.onAreaNewHotPoint(this);
	Speech.say(endOfLine, Speech.PITCH_HIGH);
	return true;
    }

    private boolean onLineHome(KeyboardEvent event)
    {
	if (model == null ||
	    model.getItemCount() < 1)
	{
	    Speech.say(noItems, Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY < 0 ||
	    hotPointY >= model.getItemCount() ||
	    model.getItem(hotPointY) == null)
	{
	    Speech.say(emptyLine, Speech.PITCH_HIGH);
	    return true;
	}
	String line = hotPointY < model.getItemCount()?getScreenAppearance(model, hotPointY, model.getItem(hotPointY), 0):"";
	if (line == null)
	    line = "";
	hotPointX = initialHotPointX < line.length()?initialHotPointX:line.length();
	environment.onAreaNewHotPoint(this);
	if (hotPointX >= line.length())
	    Speech.say(beginOfLine, Speech.PITCH_HIGH); else
	    Speech.sayLetter(line.charAt(hotPointX));
	return true;
    }

    public void setNoItemsAboveMessage(String value)
    {
	noItemsAbove = value != null?value:"";
    }

    public void setNoItemsBelowMessage(String value)
    {
	noItemsBelow = value != null?value:"";
    }

    public void setBeginOfLineMessage(String value)
    {
	beginOfLine = value != null?value:"";
    }

    public void setEndOfLineMessage(String value)
    {
	endOfLine = value != null?value:"";
    }

    public void setNoItemsMessage(String value)
    {
	noItems = value != null?value:"";
    }

    public void setEmptyLineMessage(String value)
    {
	emptyLine = value != null?value:"";
    }

    @Override public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	if (model == null || model.getItemCount() == 0)
	    return false;
	if (fromY >= model.getItemCount() || toY > model.getItemCount())
	    return false;
	Vector<String> res = new Vector<String>();
	for(int i = fromY;i < toY;++i)
	{
	    final String line = getScreenAppearance(model, i, model.getItem(i), CLIPBOARD_VALUE);
	    if (line != null)
		res.add(line); else
		res.add("");
	}
	if (res.isEmpty())
	    return false;
	environment.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    private void copyEntireContent()
    {
	Vector<String> lines = new Vector<String>();
	int maxLen = 0;
	if (model != null && model.getItemCount() > 0)
	{
	    for(int i = 0;i < model.getItemCount();++i)
	    {
		String line = getScreenAppearance(model, i, model.getItem(i), CLIPBOARD_VALUE);
		if (line == null)
		    line = "";
		lines.add(line);
		if (line.length() > maxLen)
		    maxLen = line.length();
	    }
	} else
	    {
		lines.add(noItems);
		maxLen = noItems.length();
	    }
	String dashes = "";
	while(dashes.length() < maxLen)
	    dashes += "-";
	Vector<String> res = new Vector<String>();
	res.add(getName() != null?getName():"null");
	res.add(dashes);
	res.addAll(lines);
	environment.setClipboard(res.toArray(new String[res.size()]));
    }
}
