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

package org.luwrain.core;

import java.util.*;

class LaunchedAppBase
{
    final List<Area> popups = new Vector();
    final List<OpenedArea> popupWrappings = new Vector();

    //Returns the index of the new popup
    int addPopup(Area popup)
    {
	NullCheck.notNull(popup, "popup");
	popups.add(popup);
	final OpenedArea wrapping = new OpenedArea(popup);
	popupWrappings.add(wrapping);
	return popups.size() - 1;
    }

    void closeLastPopup()
    {
	popupWrappings.remove(popupWrappings.size() - 1);
	popups.remove(popups.size() - 1);
    }

    Area getNativeAreaOfPopup(int index)
    {
	if (index < 0 || index >= popupWrappings.size())
	    throw new IllegalArgumentException("index (" + index + ") must be non-negative and less than " + popupWrappings.size());
	return popupWrappings.get(index).area;
    }

    Area getEffectiveAreaOfPopup(int index)
    {
		if (index < 0 || index >= popupWrappings.size())
	    throw new IllegalArgumentException("index (" + index + ") must be non-negative and less than " + popupWrappings.size());
			return popupWrappings.get(index).getEffectiveArea();
    }

    /**
     * Looks for the effective area for the specified one. Provided reference
     * may designate the required effective area, pointing either to the
     * natural area, either to the security wrapper or to the review
     * wrapper. This method may return the provided reference itself (e.g. if
     * provided reference points to the security wrapper and there is no a
     * review wrapper).
     *
     * @param area The area designating a cell in application layout by the natural area itself or by any of its wrappers
     * @return The effective area which corresponds to the requested cell in the application layout
    */
    Area getCorrespondingEffectiveArea(Area area)
    {
	NullCheck.notNull(area, "area");
	for(OpenedArea w: popupWrappings)
	    if (w.containsArea(area))
		return w.getEffectiveArea();
	return null;
    }

    /**
     * Returns the area wrapping object for the required area. Provided
     * reference designates a cell in the application layout, pointing either
     * to the natural area, either to the security wrapper or to the review
     * wrapper.
     *
     * @param area The area designating a cell in application layout by the natural area itself or by any of its wrappers
     * @return The area wrapping which corresponds to  the requested cell of the application layout
     */
    OpenedArea getAreaWrapping(Area area)
    {
	NullCheck.notNull(area, "area");
	for(OpenedArea w: popupWrappings)
	    if (w.containsArea(area))
		return w;
	return null;
    }

        void sendBroadcastEvent(org.luwrain.core.events.SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	for(Area area: popups)
	    area.onSystemEvent(event);
    }
}
