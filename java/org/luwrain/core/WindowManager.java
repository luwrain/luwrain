/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.core;

public class WindowManager 
{
    private static int MIN_RANGE_HORIZONTAL = 5;
    private static int MIN_RANGE_VERTICAL = 2;
    private static int MAX_TOP_BOTTOM_POPUP_HEIGHT = 7;

    private Interaction interaction;
    private ScreenContentManager screenContentManager;

    public WindowManager(Interaction interaction, ScreenContentManager screenContentManager)
    {
	this.interaction = interaction;
	this.screenContentManager = screenContentManager;
    }

    public void redraw()
    {
	TileManager windows = screenContentManager.getWindows();
	if (windows == null)
	    return;
	calculateGeom(interaction.getWidthInCharacters(), interaction.getHeightInCharacters(), windows);
	Object[] objs = windows.getObjects();
	interaction.startDrawSession();
	for(int i = 0;i < objs.length;i++)
	{
	    Window win = (Window)objs[i];
	    if (win == null)
		continue;
	    Area area = win.area;
	    if (area == null)
		continue;
	    if (win.scrolledVert >= area.getLineCount())
		continue;
final int count = area.getLineCount() - win.scrolledVert;
	    for(int k = 0;k < count;k++)
	    {
		String line = area.getLine(k + win.scrolledVert);
		if (line == null || line.isEmpty())
		    continue;
		if (win.scrolledHoriz > 0)
		{
		    if (line.length() >= win.scrolledHoriz)
			continue;
		    line = line.substring(win.scrolledHoriz);
		}
		if (line.length() > win.width)
		    line = line.substring(0, win.width);
		interaction.drawText(win.x, win.y + k, line);
	    }
	}
	interaction.endDrawSession();
    }

    private void calculateGeom(int screenWidth,
			       int screenHeight,
			       TileManager windows)
    {
	windows.countLeaves();
	calculateGeomImpl(windows, windows.getRoot(), 0, 1, screenWidth - 1, screenHeight - 2);//One line at top and one line at bottom are reserved for notifications and messages;
    }

    private void calculateGeomImpl(TileManager windows,
				   Object obj,
				   int left,
				   int top,
				   int right,
				   int bottom)
    {
	if (obj == null)
	    return;
	if (windows.isLeaf(obj))
	{
	    if (right - left < MIN_RANGE_HORIZONTAL ||
		bottom - top < MIN_RANGE_VERTICAL)
	    {
		markWindowsInvisible(windows, obj);
		return;
	    }
	    Window win = (Window)windows.getLeafObject(obj);
	    if (win == null)
		return;
	    win.x = left;
	    win.y = top;
	    win.width = right - top + 1;
	    win.height = bottom - top + 1;
	    calculateScrolling(win);
	    return;
	}
	Object obj1 = windows.getBranch1(obj), obj2 = windows.getBranch2(obj);
	if (windows.isLeaf(obj1))
	{
	    Window win = (Window)windows.getLeafObject(obj1);
	    if (win.popup)
	    {
		calculateGeomWithPopup(windows, obj, win, obj2, left, top, right, bottom);
		return;
	    }
	}
	if (windows.isLeaf(obj2))
	{
	    Window win = (Window)windows.getLeafObject(obj2);
	    if (win.popup)
	    {
		calculateGeomWithPopup(windows, obj, win, obj1, left, top, right, bottom);
		return;
	    }
	}
	final int leafCount1 = windows.getLeafCount(obj1), leafCount2 = windows.getLeafCount(obj2);
	if (leafCount1 < 1)
	{
	    calculateGeomImpl(windows, obj2, left, top, right, bottom);
	    return;
	}
	if (leafCount2 < 1)
	{
	    calculateGeomImpl(windows, obj1, left, top, right, bottom);
	    return;
	}
	if (windows.getDirection(obj) == TileManager.VERTICAL)
	{
	    int range = bottom - top;//One row is reserved for divider;
	    if (range < MIN_RANGE_VERTICAL || range < leafCount1 + leafCount2)
	    {
		markWindowsInvisible(windows, obj);
		return;
	    }
	    int range1 = (range / (leafCount1 + leafCount2)) * leafCount1;
	    final int range2 = (range / (leafCount1 + leafCount2)) * leafCount2;
	    final int spaceLeft = range - (range1 + range2);
	    range1 += (spaceLeft / 2);
	    //No need to fix range2 value, it is never used below;
	    calculateGeomImpl(windows, obj1, left, top, right, top + range1 - 1);
	    calculateGeomImpl(windows, obj2, left, top + range1 + 1, right, bottom);
	    //Here may be a splitter at top + range1 row;
	    return;
	}
	if (windows.getDirection(obj) == TileManager.HORIZONTAL)
	{
	    int range = right - left;//One column is reserved for divider;
	    if (range < MIN_RANGE_HORIZONTAL || range < leafCount1 + leafCount2)
	    {
		markWindowsInvisible(windows, obj);
		return;
	    }
	    int range1 = (range / (leafCount1 + leafCount2)) * leafCount1;
	    final int range2 = (range / (leafCount1 + leafCount2)) * leafCount2;
	    final int spaceLeft = range - (range1 + range2);
	    range1 += (spaceLeft / 2);
	    //No need to fix range2 value, it is never used below;
	    calculateGeomImpl(windows, obj1, left, top, right + range1 - 1, bottom);
	    calculateGeomImpl(windows, obj2, left + range1 + 1, top, right, bottom);
	    //Here may be a splitter at left + range1 column;
	    return;
	}
    }

    void calculateGeomWithPopup(TileManager windows,
				Object node,
				Window win,
				Object anotherNode,
				int left,
				int top,
				int right,
				int bottom)
    {
	if (win == null || !win.popup || win.area == null)
	{
	    calculateGeomImpl(windows, anotherNode, left, top, right, bottom);
	}
	Area area = win.area;
	int preferableHeight = area.getLineCount();
	int preferableWidth = 0;
	for(int i = 0;i < preferableHeight;i++)
	{
	    String line = win.area.getLine(i);
	    if (line == null)
		continue;
	    if (line.length() > preferableWidth)
		preferableWidth = line.length();
	}
	preferableWidth++;//Just to be nice;
	if (preferableWidth < MIN_RANGE_HORIZONTAL)
	    preferableWidth = MIN_RANGE_HORIZONTAL;
	preferableHeight++;//For title bar;
	if (preferableHeight < 2)
	    preferableHeight = 2;
	int maxHeight = (bottom - top + 1) - MIN_RANGE_VERTICAL - 1;//1 is for splitter;
	int maxWidth = (right - left + 1) - MIN_RANGE_HORIZONTAL - 1;//1 is for splitter;
	if (maxHeight > MAX_TOP_BOTTOM_POPUP_HEIGHT && (win.popupPlace == PopupRegistry.TOP || win.popupPlace == PopupRegistry.BOTTOM))
	    maxHeight = MAX_TOP_BOTTOM_POPUP_HEIGHT;
	if (maxWidth < MIN_RANGE_HORIZONTAL || maxHeight < MIN_RANGE_VERTICAL)
	{
	    win.markInvisible();
	    markWindowsInvisible(windows, anotherNode);
	    return;
	}
	final int popupWidth = preferableWidth <= maxWidth?preferableWidth:maxWidth;
	final int popupHeight = preferableHeight <= maxHeight?preferableHeight:maxHeight;
	int anotherLeft, anotherTop, anotherRight, anotherBottom;
	switch(win.popupPlace)
	{
	case PopupRegistry.LEFT:
	    win.x = left;
	    win.y = top;
	    win.width = popupWidth;
	    win.height = bottom - top - 1;
	    anotherLeft = left + popupWidth + 1;
	    anotherTop = top;
	    anotherRight = right;
	    anotherBottom = bottom;
	    break;
	case PopupRegistry.TOP:
	    win.x = left;
	    win.y = top;
	    win.width = right - left - 1;
	    win.height = popupHeight;
	    anotherLeft = left;
	    anotherTop = top + popupHeight + 1;
	    anotherRight = right;
	    anotherBottom = bottom;
	    break;
	case PopupRegistry.RIGHT:
	    win.x = right - popupWidth + 1;
	    win.y = top;
	    win.width = popupWidth;
	    win.height = bottom - top + 1;
	    anotherLeft = left;
	    anotherTop = top;
	    anotherRight = right - popupWidth - 1;
	    anotherBottom = bottom;
	    break;
	case PopupRegistry.BOTTOM:
	    win.x = bottom - popupHeight + 1;
	    win.x = left;
	    win.width = right - left + 1;
	    win.height = popupHeight;
	    anotherLeft = left;
	    anotherTop = top;
	    anotherRight = right;
	    anotherBottom = bottom - popupHeight - 1;
	    break;
	default:
	    win.markInvisible();
	    calculateGeomImpl(windows, anotherNode, left, top, right, bottom);
	    return;
	};
	calculateScrolling(win);
	calculateGeomImpl(windows, anotherNode, anotherLeft, anotherTop, anotherRight, anotherBottom);
    }

    private void calculateScrolling(Window win)
    {
	if (win == null || win.area == null)
	    return;
	final int hotPointX = win.area.getHotPointX();
	final int hotPointY = win.area.getHotPointY();
	win.scrolledVert = (hotPointY / (win.height - 1)) * (win.height - 1);//First line is used as title bar;
	win.scrolledHoriz = (hotPointX / win.width) * win.width;
    }

    private void markWindowsInvisible(TileManager windows, Object obj)
    {
	if (obj == null)
	    return;
	if (windows.isLeaf(obj))
	{
	    Window win = (Window)windows.getLeafObject(obj);
	    win.markInvisible();
	    return;
	}
	markWindowsInvisible(windows, windows.getBranch1(obj));
	markWindowsInvisible(windows, windows.getBranch2(obj));
    }
}
