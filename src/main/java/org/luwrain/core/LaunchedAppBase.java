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

package org.luwrain.core;

import java.util.*;
import org.luwrain.util.*;

class LaunchedAppBase
{
    static protected class AreaWrapping
    {
	public Area origArea = null;
	public Area securityWrapper = null;
	public Area reviewWrapper = null;

	public boolean containsArea(Area area)
	{
	    if (area == null)
		return false;
	    return origArea == area ||
	    securityWrapper == area ||
	    reviewWrapper == area;
	}

	public Area getEffectiveArea()
	{
	    if (reviewWrapper != null)
		return reviewWrapper;
	    if (securityWrapper != null)
		return securityWrapper;
	    Log.warning("core", "there is no security wrapper for the area " + origArea.getClass().getName());
	    return origArea;
	}
    }

    final public Vector<Area> popups = new Vector<Area>();
    final public Vector<AreaWrapping> popupWrappings = new Vector<AreaWrapping>();

    //Returns the index of the new popup;
    public int addPopup(Area popup)
    {
	NullCheck.notNull(popup, "popup");
	popups.add(popup);
	final AreaWrapping wrapping = new AreaWrapping();
	wrapping.origArea = popup;
	wrapping.securityWrapper = new SecurityAreaWrapper(popup);
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

    public Area getCorrespondingEffectiveArea(Area area)
    {
	NullCheck.notNull(area, "area");
	for(AreaWrapping w: popupWrappings)
	    if (w.containsArea(area))
		return w.getEffectiveArea();
	return null;
    }
}
