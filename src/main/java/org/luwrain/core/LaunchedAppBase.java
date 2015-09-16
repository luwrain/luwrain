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

package org.luwrain.core;

import java.util.*;
import org.luwrain.util.*;

class LaunchedAppBase
{
    static class AreaWrapping implements AreaWrappingBase
    {
	Area origArea = null;
	Area securityWrapper = null;
	Area reviewWrapper = null;

	AreaWrapping(Area area)
	{
	    origArea = area;
	    securityWrapper = new SecurityAreaWrapper(origArea);
	}

	boolean containsArea(Area area)
	{
	    if (area == null)
		return false;
	    return origArea == area ||
	    securityWrapper == area ||
	    reviewWrapper == area;
	}

	Area getEffectiveArea()
	{
	    if (reviewWrapper != null)
		return reviewWrapper;
	    if (securityWrapper != null)
		return securityWrapper;
	    Log.warning("core", "there is no security wrapper for the area " + origArea.getClass().getName());
	    return origArea;
	}

	@Override public void resetReviewWrapper()
	{
	    reviewWrapper = null;
	}
    }

    final Vector<Area> popups = new Vector<Area>();
    final Vector<AreaWrapping> popupWrappings = new Vector<AreaWrapping>();

    //Returns the index of the new popup;
    int addPopup(Area popup)
    {
	NullCheck.notNull(popup, "popup");
	popups.add(popup);
	final AreaWrapping wrapping = new AreaWrapping(popup);
	popupWrappings.add(wrapping);
	return popups.size() - 1;
    }

    public void closeLastPopup()
    {
	popupWrappings.remove(popupWrappings.size() - 1);
	popups.remove(popups.size() - 1);
    }

    public Area getEffectiveAreaOfPopup(int index)
    {
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
    public Area getCorrespondingEffectiveArea(Area area)
    {
	NullCheck.notNull(area, "area");
	for(AreaWrapping w: popupWrappings)
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
    public AreaWrapping getAreaWrapping(Area area)
    {
	NullCheck.notNull(area, "area");
	for(AreaWrapping w: popupWrappings)
	    if (w.containsArea(area))
		return w;
	return null;
    }
}
