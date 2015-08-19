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

package org.luwrain.util;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.queries.*;
import org.luwrain.core.events.*;

public class Region
{
    private RegionProvider provider;
    private Lines lines;

    private int fromX = -1;
    private int fromY  = -1;


    //lines may be null, this means using the region provider only;
    public Region(RegionProvider provider, Lines lines)
    {
	this.provider = provider;
	this.lines = lines;
	if (provider == null)
	    throw new NullPointerException("provider may not be null");
    }

    public boolean firstPoint(int hotPointX, int hotPointY)
    {
	fromX = hotPointX;
	fromY = hotPointY;
	return true;
    }

    public boolean onRegionQuery(RegionQuery query,
				 int hotPointX,
				 int hotPointY)
    {
	if (fromX < 0 || fromY < 0)
	{
	    HeldData res = provider.getWholeRegion();
	    if (res == null)
		res = constructAllLinesHeldData();
	    if (res == null)
		return false;
	    query.setData(res);
	    return true;
	}
	int x1, y1, x2, y2;
	if (fromY < hotPointY)
	{
	    x1 = fromX;
	    y1 = fromY;
	    x2 = hotPointX;
	    y2 = hotPointY;
	} else
	    if (fromY > hotPointY)
	    {
		x1 = hotPointX;
		y1 = hotPointY;
		x2 = fromX;
		y2 = fromY;
	    } else
	    {
		if (fromX < hotPointX)
		{
		    x1 = fromX;
		    y1 = fromY;
		    x2 = hotPointX;
		    y2 = hotPointY;
		} else
		    if (fromX > hotPointX)
		    {
			x1 = hotPointX;
			y1 = hotPointY;
			x2 = fromX;
			y2 = fromY;
		    } else
		    {
			HeldData res = provider.getWholeRegion();
			if (res == null)
			    res = constructAllLinesHeldData();
			if (res == null)
			    return false;
			query.setData(res);
			return true;
		    }
	    }
	HeldData res = provider.getRegion(x1, y1, x2, y2);
	if (res == null)
	    res = constructLinesHeldData(x1, y1, x2, y2);
	if (res == null)
	    return false;
	query.setData(res);
	return true;
    }

    public boolean delete(int hotPointX, int hotPointY)
    {
	if (fromX < 0 || fromY < 0)
	    return false;
	if (fromY < hotPointY)
	    return provider.deleteRegion(fromX, fromY, hotPointX, hotPointY);
	if (fromY > hotPointY)
	    return provider.deleteRegion(hotPointX, hotPointY, fromX, fromY);
	if (fromX < hotPointX)
	    return provider.deleteRegion(fromX, fromY, hotPointX, hotPointY);
	if (fromX > hotPointX)
	    return provider.deleteRegion(hotPointX, hotPointY, fromX, fromY);
	return false;
    }

    public boolean insert(InsertEvent event,
			  int hotPointX, int hotPointY)
    {
	//FIXME:
	return false;
    }

    public boolean onAreaQuery(AreaQuery query,
			       int hotPointX,
			       int hotPointY)
    {
	if (query == null)
	    return false;
	if (hotPointX < 0 || hotPointY < 0)
	    return false;
	if (query.getQueryCode() == AreaQuery.REGION && (query instanceof RegionQuery))
	    return onRegionQuery((RegionQuery)query, hotPointX, hotPointY);
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event,
				      int hotPointX, int hotPointY)
    {
	if (event == null)
	    return false;
	if (hotPointX < 0 || hotPointY < 0)
	    return false;
	switch(event.getCode())
	{
	case EnvironmentEvent.REGION_POINT:
	    return firstPoint(hotPointX, hotPointY);
	case EnvironmentEvent.DELETE:
	    return delete(hotPointX, hotPointY);
	case EnvironmentEvent.INSERT:
	    if (!(event instanceof InsertEvent))
		return false;
	    return insert((InsertEvent)event, hotPointX, hotPointY);
	default:
	    return false;
	}
    }

    private HeldData constructAllLinesHeldData()
    {
	if (lines == null)
	    return null;
	final LinkedList<String> res = new LinkedList<String>();
	final int count = lines.getLineCount();
	if (count < 1)
	    return null;
	for(int i = 0;i < count;++i)
	{
	    final String line = lines.getLine(i);
	    res.add(line != null?line:"");
	}
	return new HeldData(res.toArray(new String[res.size()]));
    }

    private HeldData constructLinesHeldData(int fromX, int fromY,
					    int toX, int toY)
    {
	if (lines == null)
	    return null;
	final int count = lines.getLineCount();
	if (count < 1)
	    return null;
	if (toY >= count)
	    return null;
	if (fromY == toY)
	{
	    final String line = lines.getLine(fromY);
	    if (line == null || line.isEmpty())
		return null;
	    final int fromPos = fromX < line.length()?fromX:line.length();
	    final int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		throw null;
	    return new HeldData(new String[]{line.substring(fromPos, toPos)});
	}
	final LinkedList<String> res = new LinkedList<String>();
	String line = lines.getLine(fromY);
	if (line == null)
	    line = "";
	res.add(line.substring(fromX < line.length()?fromX:line.length()));
	for(int i = fromY + 1;i < toY;++i)
	{
	    line = lines.getLine(i);
	    if (line == null)
		line = "";
	    res.add(line);
	}
	line = lines.getLine(toY);
	if (line == null)
	    line = "";
	res.add(line.substring(0, toX <line.length()?toX:line.length()));
	return new HeldData(res.toArray(new String[res.size()]));
    }
}
