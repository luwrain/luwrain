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

package org.luwrain.desktop;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class Appearance implements ListItemAppearance
{
    private Luwrain luwrain;
    private Strings strings;

    public Appearance(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
    }

    @Override public void introduceItem(Object item, int flags)
    {
	if (item == null)
	    return;
	//FIXME:
    }

    @Override public String getScreenAppearance(Object item, int flags)
    {
	if (item == null)
	    return "  ";
	//FIXME:
	return "fixme";
    }

    @Override public int getObservableLeftBound(Object item)
    {
	return 0;
    }

    @Override public int getObservableRightBound(Object item)
    {
	if (item == null)
	    return 0;
	return 0;
    }
}
