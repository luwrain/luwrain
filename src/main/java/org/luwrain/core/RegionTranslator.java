/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.queries.*;
import org.luwrain.core.events.*;

public class RegionTranslator
{
    private RegionProvider provider;

    private int fromX = -1;
    private int fromY  = -1;

    public RegionTranslator(RegionProvider provider)
    {
	NullCheck.notNull(provider, "provider");
	this.provider = provider;
    }

    public boolean onAreaQuery(AreaQuery query,
			       int hotPointX, int hotPointY)
    {
	NullCheck.notNull(query, "query");
	if (hotPointX < 0 || hotPointY < 0)
	    return false;
	if (query.getQueryCode() == AreaQuery.CUT && (query instanceof CutQuery))
	    return onCutQuery((CutQuery)query, hotPointX, hotPointY);
	if (query.getQueryCode() == AreaQuery.REGION && (query instanceof RegionQuery))
	    return onRegionQuery((RegionQuery)query, hotPointX, hotPointY);
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event,
				      int hotPointX, int hotPointY)
    {
	NullCheck.notNull(event, "event");
	if (hotPointX < 0 || hotPointY < 0)
	    return false;
	switch(event.getCode())
	{
	case REGION_POINT:
	    return firstPoint(hotPointX, hotPointY);
	case DELETE:
	    return delete(hotPointX, hotPointY);
	case INSERT:
	    if (!(event instanceof InsertEvent))
		return false;
	    return insert((InsertEvent)event, hotPointX, hotPointY);
	default:
	    return false;
	}
    }

    private boolean firstPoint(int hotPointX, int hotPointY)
    {
	fromX = hotPointX;
	fromY = hotPointY;
	return true;
    }

    private boolean onRegionQuery(RegionQuery query,
				 int hotPointX, int hotPointY)
    {
	if (fromX < 0 || fromY < 0)
	{
	    final RegionContent res = provider.getWholeRegion();
	    if (res == null)
		return false;
	    query.answer(res);
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
			final RegionContent res = provider.getWholeRegion();
			if (res == null)
			    return false;
			query.answer(res);
			return true;
		    }
	    }
	final RegionContent res = provider.getRegion(x1, y1, x2, y2);
	if (res == null)
	    return false;
	query.answer(res);
	return true;
    }

    private boolean onCutQuery(CutQuery query, 
			       int hotPointX, int hotPointY)
    {
	if (!onRegionQuery(query, hotPointX, hotPointY))
	    return false;
	return delete(hotPointX, hotPointY);
    }

    private boolean delete(int hotPointX, int hotPointY)
    {
	if (fromX < 0 || fromY < 0)
	    return provider.deleteWholeRegion();
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

    private boolean insert(InsertEvent event,
			  int hotPointX, int hotPointY)
    {
	if (hotPointX < 0 || hotPointY < 0)
	    return false;
	return provider.insertRegion(hotPointX, hotPointY, event.getData());
    }
}
