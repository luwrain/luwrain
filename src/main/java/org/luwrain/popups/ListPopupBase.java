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

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class ListPopupBase implements Popup, PopupClosingRequest, RegionProvider
{
    protected Luwrain luwrain;
    private final Region region = new Region(this);
    public final PopupClosing closing = new PopupClosing(this);
    protected ListModel model;
    protected ListItemAppearance appearance;
    private Object[] items;
    private int hotPointX = 0;
    private int hotPointY = 0;
    private String name;
    private int popupFlags;

    public ListPopupBase(Luwrain luwrain,
		     String name,
		     ListModel model,
		     ListItemAppearance appearance,
		     int popupFlags)
    {
	this.luwrain = luwrain;
	this.name = name;
	this.model = model;
	this.appearance = appearance;
	this.popupFlags = popupFlags;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
	if (appearance == null)
	    throw new NullPointerException("appearance may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	refresh();
    }

    public void refresh()
    {
	final int count = model.getItemCount();
	if (count < 1)
	{
	    items = new Object[0];
	    hotPointX = 0;
	    hotPointY = 0;
	    luwrain.onAreaNewContent(this);
	    luwrain.onAreaNewHotPoint(this);
	    return;
	}
	items = new Object[count];
	for(int i = 0;i < count;++i)
	    items[i] = model.getItem(i);
	if (hotPointY > items.length)
	    hotPointY = items.length;
	hotPointX = 0;
	luwrain.onAreaNewContent(this);
	luwrain.onAreaNewHotPoint(this);
    }

    @Override public int getLineCount()
    {
	return items != null?items.length + 1:1;
    }

    @Override public String getLine(int index)
    {
	if (items == null ||
	    index <= 0 ||
	    index > items.length)
	    return "";
	final Object item = items[index - 1];
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

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (closing.onKeyboardEvent(event))
	    return true;
	if (event.isModified() || !event.isCommand())
	    return false;
	switch(event.getCommand())
	{
	case KeyboardEvent.ENTER:
	    return closing.doOk();
	case KeyboardEvent.ARROW_DOWN:
	    return onArrowDown(event, false);
	case KeyboardEvent.ARROW_UP:
	    return onArrowUp(event, false);
	case KeyboardEvent.ALTERNATIVE_ARROW_DOWN:
	    return onArrowDown(event, true);
	case KeyboardEvent.ALTERNATIVE_ARROW_UP:
	    return onArrowUp(event, true);
	case KeyboardEvent.ARROW_LEFT:
	    return onArrowLeft(event);
	case KeyboardEvent.ARROW_RIGHT:
	    return onArrowRight(event);
	case KeyboardEvent.ALTERNATIVE_ARROW_LEFT:
	    return onAltLeft(event);
	case KeyboardEvent.ALTERNATIVE_ARROW_RIGHT:
	    return onAltRight(event);
	case KeyboardEvent.PAGE_DOWN:
	    return onPageDown(event, false);
	case KeyboardEvent.PAGE_UP:
	    return onPageUp(event, false);
	case KeyboardEvent.ALTERNATIVE_PAGE_DOWN:
	    return onPageDown(event, true);
	case KeyboardEvent.ALTERNATIVE_PAGE_UP:
	    return onPageUp(event, true);
	case KeyboardEvent.HOME:
	    return onHome(event);
	case KeyboardEvent.END:
	    return onEnd(event);
	case KeyboardEvent.ALTERNATIVE_HOME:
	    return onAltHome(event);
	case KeyboardEvent.ALTERNATIVE_END:
	    return onAltEnd(event);
	case KeyboardEvent.INSERT:
	    return onInsert(event);
	default:
	    return false;
	}
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onEnvironmentEvent(event))
	    return true;
	if (region.onEnvironmentEvent(event, hotPointX, hotPointY))
	    return false;
	switch(event.getCode())
	{
	case EnvironmentEvent.REFRESH:
	    refresh();
	    return true;
	case EnvironmentEvent.INTRODUCE:
	    luwrain.silence();
	    luwrain.playSound(Sounds.INTRO_POPUP);
	    luwrain.say(getAreaName());
	    return true;
	default:
	    return false;
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	return region.onAreaQuery(query, hotPointX, hotPointY);
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public String getAreaName()
    {
	return name;
    }

    public ListModel model()
    {
	return model;
    }

    public ListItemAppearance appearance()
    {
	return appearance;
    }

    private boolean onArrowDown(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.NO_ITEMS_BELOW);
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
	    luwrain.hint(Hints.NO_ITEMS_ABOVE);
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
	if (hotPointY >= items.length)
	{
	    luwrain.hint(Hints.NO_ITEMS_BELOW);
	    return true;
	}
	final int visibleHeight = luwrain.getAreaVisibleHeight(this);
	if (visibleHeight < 1)
	    return false;
	hotPointY += visibleHeight;
	if (hotPointY > items.length)
	    hotPointY = items.length;
	onNewHotPointY(briefIntroduction);
	return true;
    }

    private boolean onPageUp(KeyboardEvent event, boolean briefIntroduction)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0)
	{
	    luwrain.hint(Hints.NO_ITEMS_ABOVE);
	    return true;
	}
	final int visibleHeight = luwrain.getAreaVisibleHeight(this);
	if (visibleHeight < 1)
	    return false;
	if (hotPointY > visibleHeight)
	    hotPointY -= visibleHeight; else
	    hotPointY = 0;
	onNewHotPointY(briefIntroduction);
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

    private boolean onEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	hotPointY = items.length;
	onNewHotPointY(false);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0 ||
	    hotPointY > items.length ||
	    items[hotPointY - 1] == null)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = items[hotPointY - 1];
	final String line = appearance.getScreenAppearance(item, 0);
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
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= rightBound)
	{
	    luwrain.hint(Hints.END_OF_LINE);
	    return true;
	}
	++hotPointX;
	if (hotPointX < rightBound)
	    luwrain.sayLetter(line.charAt(hotPointX)); else
	    luwrain.hint(Hints.END_OF_LINE);
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0 ||
	    hotPointY > items.length ||
	    items[hotPointY - 1] == null)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = items[hotPointY - 1];
	final String line = appearance.getScreenAppearance(item, 0);
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
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX <= leftBound)
	{
	    luwrain.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	--hotPointX;
	luwrain.sayLetter(line.charAt(hotPointX));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltRight(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0 ||
	    hotPointY > items.length ||
	    items[hotPointY - 1] == null)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = items[hotPointY - 1];
	final String line = appearance.getScreenAppearance(item, 0);
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
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX >= rightBound)
	{
	    luwrain.hint(Hints.END_OF_LINE);
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepForward())
	{
	    luwrain.hint(Hints.END_OF_LINE);
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	if (it.announce().length() > 0)
	    luwrain.say(it.announce()); else
	    luwrain.hint(Hints.END_OF_LINE);
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltLeft(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0 ||
	    hotPointY > items.length ||
	    items[hotPointY - 1] == null)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = items[hotPointY - 1];
	final String line = appearance.getScreenAppearance(item, 0);
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
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX <= leftBound)
	{
	    luwrain.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	final String subline = line.substring(leftBound, rightBound);
	WordIterator it = new WordIterator(subline, hotPointX - leftBound);
	if (!it.stepBackward())
	{
	    luwrain.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = it.pos() + leftBound;
	    luwrain.say(it.announce());
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltHome(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0 ||
	    hotPointY > items.length ||
	    items[hotPointY - 1] == null)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = items[hotPointY - 1];
	final String line = appearance.getScreenAppearance(item, 0);
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
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX <= leftBound)
	{
	    luwrain.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = leftBound;
	luwrain.sayLetter(line.charAt(hotPointX));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAltEnd(KeyboardEvent event)
    {
	if (noContentCheck())
	    return true;
	if (hotPointY <= 0 ||
	    hotPointY > items.length ||
	    items[hotPointY - 1] == null)
	{
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	final Object item = items[hotPointY - 1];
	final String line = appearance.getScreenAppearance(item, 0);
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
	    luwrain.hint(Hints.EMPTY_LINE);
	    return true;
	}
	if (hotPointX <= leftBound)
	{
	    luwrain.hint(Hints.BEGIN_OF_LINE);
	    return true;
	}
	hotPointX = rightBound;
	luwrain.hint(Hints.END_OF_LINE);
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onInsert(KeyboardEvent event)
    {
	//FIXME:
	return false;
    }

    @Override public HeldData getWholeRegion()
    {
	if (model == null || model.getItemCount() == 0)
	    return null;
	final LinkedList<String> res = new LinkedList<String>();
	final int count = model.getItemCount();
	for(int i = 0;i < count;++i)
	{
	    final String line = appearance.getScreenAppearance(model.getItem(i), ListItemAppearance.FOR_CLIPBOARD);
	    res.add(line != null?line:"");
	}
	res.add("");
	return new HeldData(res.toArray(new String[res.size()]));
    }

    @Override public HeldData getRegion(int fromX, int fromY,
					int toX, int toY)
    {
	if (model == null || model.getItemCount() == 0)
	    return null;
	if (fromY >= model.getItemCount() || toY >= model.getItemCount())
	    return null;
	if (fromY == toY)
	{
	    final String line = appearance.getScreenAppearance(model.getItem(fromY - 1), ListItemAppearance.FOR_CLIPBOARD);
	    if (line.isEmpty())
		return null;
	    final int fromPos = fromX < line.length()?fromX:line.length();
	    final int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		return null;
	    return new HeldData(new String[]{line.substring(fromPos, toPos)});
	}
	final LinkedList<String> res = new LinkedList<String>();
	for(int i = fromY;i <= toY;++i)
	{
	    if (i == 0)
		continue;
	    final String line = appearance.getScreenAppearance(model.getItem(i - 1), ListItemAppearance.FOR_CLIPBOARD);
	    res.add(line != null?line:"");
	}
	res.add("");
	return new HeldData(res.toArray(new String[res.size()]));
    }

    @Override public boolean deleteRegion(int fromX, int fromY,
					  int toX, int toY)
    {
	return false;
    }

    @Override public boolean insertRegion(int x, int y,
					  HeldData data)
    {
	return false;
    }

    @Override public boolean onOk()
    {
	return true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    @Override public Luwrain getLuwrainObject()
    {
	return luwrain;
    }

    @Override public EventLoopStopCondition getStopCondition()
    {
	return closing;
    }

    @Override public boolean noMultipleCopies()
    {
	return (popupFlags & Popup.NO_MULTIPLE_COPIES) != 0;
    }

    @Override public boolean isWeakPopup()
    {
	return (popupFlags & Popup.WEAK) != 0;
    }

    public Object selected()
    {
	if (items == null || hotPointY < 1 || hotPointY > items.length)
	    return null;
	return model.getItem(hotPointY - 1);
    }

    private void onNewHotPointY(boolean briefIntroduction)
    {
	if (hotPointY <= 0)
	{
	    hotPointX = 0;
	    luwrain.hint(Hints.EMPTY_LINE);
	    luwrain.onAreaNewHotPoint(this);
	    return;
	}
	final Object item = items[hotPointY - 1];
	luwrain.playSound(Sounds.NEW_LIST_ITEM);
	if (item == null)
	{
	    hotPointX = 0;
	    luwrain.hint(Hints.EMPTY_LINE);
	} else
	{
	    hotPointX = getInitialHotPointX(item);
	    appearance.introduceItem(item, briefIntroduction?ListItemAppearance.BRIEF:0);
	}
	luwrain.onAreaNewHotPoint(this);
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
	if (items == null || items.length <= 0)
	{
	    luwrain.hint(luwrain.i18n().staticStr(LangStatic.LIST_NO_CONTENT), Hints.NO_CONTENT);
	    return true;
	}
	return false;
    }
}
