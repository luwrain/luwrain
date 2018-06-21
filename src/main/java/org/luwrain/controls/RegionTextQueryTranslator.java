/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

public class RegionTextQueryTranslator
{
    //FIXME:what to do on default action
    public enum Flags {ALLOWED_EMPTY, ALLOWED_WITHOUT_REGION_POINT};

    public interface Provider
    {
	String onRegionTextQuery(int fromX, int fromY, int toX, int toY);
    }

    protected final Provider provider;
    protected final AbstractRegionPoint regionPoint;
    protected final Set<Flags> flags;

    public RegionTextQueryTranslator(Provider provider, AbstractRegionPoint regionPoint, Set<Flags> flags)
    {
	NullCheck.notNull(provider, "provider");
	NullCheck.notNull(regionPoint, "regionPoint");
	NullCheck.notNull(flags, "flags");
	this.provider = provider;
	this.regionPoint = regionPoint;
	this.flags = flags;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event, int hotPointX, int hotPointY)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return false;
	if (hotPointX < 0 || hotPointY < 0)
	    throw new IllegalArgumentException("hotPointX and hotPointY must be greater or equal to zero (" + hotPointX + "," + hotPointY + ")");
	switch(event.getCode())
	{
	case REGION_POINT:
	    return regionPoint.onEnvironmentEvent(event, hotPointX, hotPointY);
	default:
	    return false;
	}
    }

    public boolean onAreaQuery(AreaQuery query, int hotPointX, int hotPointY)
    {
	NullCheck.notNull(query, "query");
	if (query.getQueryCode() != AreaQuery.REGION_TEXT || !(query instanceof RegionTextQuery))
	    return false;
	if (hotPointX < 0 || hotPointY < 0)
	    throw new IllegalArgumentException("hotPointX and hotPointY must be greater or equal to zero (" + hotPointX + "," + hotPointY + ")");
	return onRegionTextQuery((RegionTextQuery)query, hotPointX, hotPointY);
    }

    protected boolean onRegionTextQuery(RegionTextQuery query, int hotPointX, int hotPointY)
    {
	NullCheck.notNull(query, "query");
	if (!regionPoint.isInitialized())
	{
	    if (!flags.contains(Flags.ALLOWED_WITHOUT_REGION_POINT))
		return false;
	    final String text = provider.onRegionTextQuery(-1, -1, hotPointX, hotPointY);
if (text == null)
    return false;
query.answer(text);
return true;
	}
	final int x1;
	final int y1;
	final int x2;
	final int y2;
	if (regionPoint.getHotPointY() < hotPointY)
	{
	    x1 = regionPoint.getHotPointX();
	    y1 = regionPoint.getHotPointY();
	    x2 = hotPointX;
	    y2 = hotPointY;
	} else
	    if (regionPoint.getHotPointY() > hotPointY)
	    {
		x1 = hotPointX;
		y1 = hotPointY;
		x2 = regionPoint.getHotPointX();
		y2 = regionPoint.getHotPointY();
	    } else
	    {
		if (regionPoint.getHotPointX() <= hotPointX)
		{
		    x1 = regionPoint.getHotPointX();
		    y1 = regionPoint.getHotPointY();
		    x2 = hotPointX;
		    y2 = hotPointY;
		} else
		{
		    x1 = hotPointX;
		    y1 = hotPointY;
		    x2 = regionPoint.getHotPointX();
		    y2 = regionPoint.getHotPointY();
		}
	    }

	if ((x1 == x2) && (y1 == y2) && !flags.contains(Flags.ALLOWED_EMPTY))
	    return false;
	final String text = provider.onRegionTextQuery(x1, y1, x2, y2);
if (text == null)
    return false;
query.answer(text);
return true;
    }
}
