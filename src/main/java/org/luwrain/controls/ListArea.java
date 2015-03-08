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
    private ControlEnvironment environment;
    private String name = "";
    private ListModel model;
    private ListItemAppearance appearance;
    private ListClickHandler clickHandler;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private CopyCutInfo copyCutInfo = new CopyCutInfo(this);

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
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
	appearance = new DefaultListItemAppearance(environment);
	resetHotPoint();
    }

    public ListArea(ControlEnvironment environment,
		    ListModel model,
		    String name)
    {
	this.environment = environment;
	this.model = model;
	this.name = name;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	appearance = new DefaultListItemAppearance(environment);
	resetHotPoint();
    }

    public ListArea(ControlEnvironment environment,
		    ListModel model,
		    ListItemAppearance appearance,
		    String name)
    {
	this.environment = environment;
	this.model = model;
	this.appearance = appearance;
	this.name = name;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
	if (appearance == null)
	    throw new NullPointerException("appearance may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	resetHotPoint();
    }

    public ListArea(ControlEnvironment environment,
		    ListModel model,
		    ListItemAppearance appearance,
		    ListClickHandler clickHandler,
		    String name)
    {
	this.environment = environment;
	this.model = model;
	this.appearance = appearance;
	this.clickHandler = clickHandler;
	this.name = name;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
	if (appearance == null)
	    throw new NullPointerException("appearance may not be null");
	if (clickHandler == null)
	    throw new NullPointerException("clickHandler may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	resetHotPoint();
    }

    public ListModel model()
    {
	return model;
    }

    public Object selected()
    {
	return hotPointY < model.getItemCount()?model.getItem(hotPointY):null;
    }

    public int selectedIndex()
    {
	final int count = model.getItemCount();
	if (count < 1 &&
	    hotPointY < 0 ||
	    hotPointY >= count)
	    return -1;
	return hotPointY;
    }

    public void selectEmptyLine()
    {
	hotPointX = 0;
	hotPointY = model.getItemCount();
	environment.onAreaNewHotPoint(this);
    }

    public boolean setSelectedByIndex(int index, boolean introduce)
    {
	if (index < 0 || index >= model.getItemCount())
	    return false;
	hotPointY = index;
	final Object item = model.getItem(hotPointY);
	if (item != null)
	{
	    hotPointX = appearance.getObservableLeftBound(item);
	    if (introduce)
		appearance.introduceItem(item, 0);
	} else
	{
	    hotPointX = 0;
	    if (introduce)
		environment.hint(Hints.EMPTY_LINE);
	}
	environment.onAreaNewHotPoint(this);
	return true;
    }

    public void resetHotPoint()
    {
	hotPointY = 0;
	final int count = model.getItemCount();
	if (count < 1)
	{
	    hotPointX = 0;
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	final Object item = model.getItem(0);
	hotPointX = item != null?appearance.getObservableLeftBound(item):0;
	environment.onAreaNewHotPoint(this);
    }

    public void introduceSelected()
    {
	final Object item = selected();
	if (item != null)
	    appearance.introduceItem(item, 0);
    }

    /**
     * Refreshes the content of the list. This method calls {@code refresh()}
     * method of the model and displays new items. It does not produce any
     * speech announcement of the change. HotPointY is preserved if it is
     * possible (meaning, the new number of lines not less than old value of
     * hotPointY), but hotPointX is moved to the beginning of the line.
     */
    public void refresh()
    {
	model.refresh();
	final int count = model.getItemCount();
	if (count == 0)
	{
	    hotPointX = 0;
	    hotPointY = 0;
	    environment.onAreaNewContent(this);
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	hotPointY = hotPointY < count?hotPointY :count - 1;
	final Object item = model.getItem(hotPointY);
	if (item != null)
	    hotPointX = appearance.getObservableLeftBound(item); else
	    hotPointX = 0;
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
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
	case KeyboardEvent.ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	case KeyboardEvent.ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case KeyboardEvent.HOME:
	    return onHome(event);
	case KeyboardEvent.END:
	    return onEnd(event);
	case KeyboardEvent.ALTERNATIVE_HOME:
	    return onAltHome(event);
	case KeyboardEvent.ALTERNATIVE_END:
	    return onAltEnd(event);
	case KeyboardEvent.PAGE_DOWN:
	    return onPageDown(event, false);
	case KeyboardEvent.PAGE_UP:
	    return onPageUp(event, false);
	case KeyboardEvent.ALTERNATIVE_PAGE_DOWN:
	    return onPageDown(event, true);
	case KeyboardEvent.ALTERNATIVE_PAGE_UP:
	    return onPageUp(event, true);
	case KeyboardEvent.INSERT:
	    return onInsert(event);
	case KeyboardEvent.ENTER:
	    return onEnter(event);
	default:
	    return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	switch (event.getCode())
	{
	case EnvironmentEvent.REFRESH:
	    refresh();
	    return true;
	case EnvironmentEvent.OK:
	    return onOk(event);
	case EnvironmentEvent.COPY_CUT_POINT:
	    return copyCutInfo.copyCutPoint(hotPointX, hotPointY);
	case EnvironmentEvent.COPY:
	    return copyCutInfo.copy(hotPointX, hotPointY);
	default:
	    return false;
	}
    }

    @Override public int getLineCount()
    {
	if (model.getItemCount() <= 0)
	    return 2;
	return model.getItemCount() + 1;
    }

    @Override public String getLine(int index)
    {
	final int count = model.getItemCount();
	if (count < 1)
	    return index == 0?environment.staticStr(LangStatic.LIST_NO_CONTENT):"";
	if (index < 0 || index >= count)
	    return "";
	final Object item = model.getItem(index);
	return item != null?appearance.getScreenAppearance(item, 0):"";
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
	return name;
    }

    public void setName(String value)
    {
	if (value == null)
	    throw new NullPointerException("name must not be null");
	name = value != null?value:"";
	environment.onAreaNewName(this);
    }

    private boolean onArrowDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= model.getItemCount())
	{
	    environment.hint(Hints.NO_ITEMS_BELOW);
		return true;
	}
	++hotPointY;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	return true;
	if (hotPointY <= 0)
	{
	    environment.hint(Hints.NO_ITEMS_ABOVE);
	    return true;
	}
	    --hotPointY;
	    onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onPageDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getItemCount();
	if (hotPointY >= count)
	{
	    environment.hint(Hints.NO_ITEMS_BELOW);
		return true;
	}
	hotPointY += environment.getAreaVisibleHeight(this);
	if (hotPointY >= count)
	    hotPointY = count;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onPageUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0)
	{
	    environment.hint(Hints.NO_ITEMS_ABOVE);
	    return true;
	}
	final int height = environment.getAreaVisibleHeight(this);
	if (hotPointY > height)
	hotPointY -= height; else
	    hotPointY = 0;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	hotPointY = model.getItemCount();
	onNewHotPointY(false);
	return true;
    }

    private boolean onHome(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	hotPointY = 0;
	onNewHotPointY(false);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getItemCount();
	if (hotPointY < 0 ||
	    hotPointY >= count)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = model.getItem(hotPointY);
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, 0);
	if (line == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	int leftBound = appearance.getObservableLeftBound(item);
	if (leftBound < 0)
	    leftBound = 0;
	if (leftBound > line.length())
	    leftBound = line.length();
	int rightBound = appearance.getObservableRightBound(item);
	if (rightBound < 0)
	    rightBound = 0;
	if (rightBound >= line.length())
	    rightBound = line.length();
	if (leftBound >= rightBound)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= rightBound)
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	++hotPointX;
	if (hotPointX < rightBound)
	    environment.sayLetter(line.charAt(hotPointX)); else
	    environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getItemCount();
	if (hotPointY < 0 ||
	    hotPointY >= count)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = model.getItem(hotPointY);
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, 0);
	if (line == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	int leftBound = appearance.getObservableLeftBound(item);
	if (leftBound < 0)
	    leftBound = 0;
	if (leftBound > line.length())
	    leftBound = line.length();
	int rightBound = appearance.getObservableRightBound(item);
	if (rightBound < 0)
	    rightBound = 0;
	if (rightBound >= line.length())
	    rightBound = line.length();
	if (leftBound >= rightBound)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX <= leftBound)
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	--hotPointX;
	environment.sayLetter(line.charAt(hotPointX));
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltRight(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getItemCount();
	if (hotPointY < 0 ||
	    hotPointY >= count)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = model.getItem(hotPointY);
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, 0);
	if (line == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	int leftBound = appearance.getObservableLeftBound(item);
	if (leftBound < 0)
	    leftBound = 0;
	if (leftBound > line.length())
	    leftBound = line.length();
	int rightBound = appearance.getObservableRightBound(item);
	if (rightBound < 0)
	    rightBound = 0;
	if (rightBound >= line.length())
	    rightBound = line.length();
	if (leftBound >= rightBound)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= rightBound)
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepForward())
	{
	    environment.hint(Hints.END_OF_LINE);
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	if (it.announce().length() > 0)
	    environment.say(it.announce()); else
	    environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getItemCount();
	if (hotPointY < 0 ||
	    hotPointY >= count)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = model.getItem(hotPointY);
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, 0);
	if (line == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	int leftBound = appearance.getObservableLeftBound(item);
	if (leftBound < 0)
	    leftBound = 0;
	if (leftBound > line.length())
	    leftBound = line.length();
	int rightBound = appearance.getObservableRightBound(item);
	if (rightBound < 0)
	    rightBound = 0;
	if (rightBound >= line.length())
	    rightBound = line.length();
	if (leftBound >= rightBound)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX <= leftBound)
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepBackward())
	{
	    environment.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	    environment.say(it.announce());
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getItemCount();
	if (hotPointY < 0 ||
	    hotPointY >= count)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = model.getItem(hotPointY);
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, 0);
	if (line == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	int leftBound = appearance.getObservableLeftBound(item);
	if (leftBound < 0)
	    leftBound = 0;
	if (leftBound > line.length())
	    leftBound = line.length();
	int rightBound = appearance.getObservableRightBound(item);
	if (rightBound < 0)
	    rightBound = 0;
	if (rightBound >= line.length())
	    rightBound = line.length();
	if (leftBound >= rightBound)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	hotPointX = rightBound;
	environment.hint(Hints.END_OF_LINE);
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltHome(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	final int count = model.getItemCount();
	if (hotPointY < 0 ||
	    hotPointY >= count)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = model.getItem(hotPointY);
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final String line = appearance.getScreenAppearance(item, 0);
	if (line == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	int leftBound = appearance.getObservableLeftBound(item);
	if (leftBound < 0)
	    leftBound = 0;
	if (leftBound > line.length())
	    leftBound = line.length();
	int rightBound = appearance.getObservableRightBound(item);
	if (rightBound < 0)
	    rightBound = 0;
	if (rightBound >= line.length())
	    rightBound = line.length();
	if (leftBound >= rightBound)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	hotPointX = leftBound;
	    environment.sayLetter(line.charAt(hotPointX));
	environment.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onInsert(KeyboardEvent event)
    {
	final int count = model.getItemCount();
	if (count == 0 || hotPointY >= count)
	    return false;
	if (!model.toggleMark(hotPointY))
	    return false;
	environment.onAreaNewContent(this);
	return true;
    }

    private boolean onEnter(KeyboardEvent event)
    {
	if (clickHandler == null)
	    return false;
	    final int count = model.getItemCount();
	    if (count < 1)
		return false;
	    if (hotPointY < 0 || hotPointY >= count)
		return false;
	    return clickHandler.onListClick(this, hotPointY, model.getItem(hotPointY));
    }

    private boolean onOk(EnvironmentEvent event)
    {
	if (clickHandler == null)
	    return false;
	    final int count = model.getItemCount();
	    if (count < 1)
		return false;
	    if (hotPointY < 0 || hotPointY >= count)
		return false;
	    return clickHandler.onListClick(this, hotPointY, model.getItem(hotPointY));
    }

    @Override public boolean onCopyAll()
    {
	if (model == null || model.getItemCount() == 0)
	    return false;
	Vector<String> res = new Vector<String>();
	final int count = model.getItemCount();
	for(int i = 0;i < count;++i)
	{
	    final String line = appearance.getScreenAppearance(model.getItem(i), ListItemAppearance.FOR_CLIPBOARD);
	    if (line != null)
		res.add(line); else
		res.add("");
	}
	res.add("");
	if (res.size() == 2)
	    environment.say(res.get(0)); else
	    environment.say(environment.staticStr(Langs.COPIED_LINES) + (res.size() - 1));
	environment.setClipboard(res.toArray(new String[res.size()]));
	return true;
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
	    final String line = appearance.getScreenAppearance(model.getItem(i), ListItemAppearance.FOR_CLIPBOARD);
	    if (line != null)
		res.add(line); else
		res.add("");
	}
	res.add("");
	if (res.size() == 2)
	    environment.say(res.get(0)); else
	    environment.say(environment.staticStr(Langs.COPIED_LINES) + (res.size() - 1));
	environment.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

    /*
    private void copyEntireContent()
    {
	Vector<String> lines = new Vector<String>();
	int maxLen = 0;
	if (model != null && model.getItemCount() > 0)
	{
	    for(int i = 0;i < model.getItemCount();++i)
	    {
		String line = appearance.getScreenAppearance(model.getItem(i), ListItemAppearance.FOR_CLIPBOARD);
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
    */

    private void onNewHotPointY(boolean briefIntroduction)
    {
	final int count = model.getItemCount();
	if (hotPointY < 0 || hotPointY >= count)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    hotPointX = 0;
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	final Object item = model.getItem(hotPointY);
	if (item == null)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    hotPointX = 0;
	    environment.onAreaNewHotPoint(this);
	    return;
	}
	appearance.introduceItem(item, briefIntroduction?ListItemAppearance.BRIEF:0);
	hotPointX = getInitialHotPointX(item);
	environment.onAreaNewHotPoint(this);
    }

    private int getInitialHotPointX(Object item)
    {
	if (item == null)
	    return 0;
	final String line = appearance.getScreenAppearance(item, 0);
	final int leftBound = appearance.getObservableLeftBound(item);
	return leftBound < line.length()?leftBound:line.length();
    }

    private boolean noContentCheck()
    {
	if (model == null || model.getItemCount() < 1)
	{
	    environment.hint(environment.staticStr(LangStatic.LIST_NO_CONTENT), Hints.NO_CONTENT);
	    return true;
	}
	return false;
    }
}
