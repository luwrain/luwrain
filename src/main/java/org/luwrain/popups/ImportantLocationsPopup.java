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

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.os.Location;

public class ImportantLocationsPopup extends ListPopup
{
    public ImportantLocationsPopup(Luwrain luwrain,
				   int popupFlags)
    {
	super(luwrain,
	      luwrain.i18n().staticStr(LangStatic.POPUP_IMPORTANT_LOCATIONS_NAME),
	      new ImportantLocationsListModel(luwrain),
	      new ImportantLocationsAppearance(luwrain),
	      popupFlags);
	}

    public Location selectedLocation()
    {
	final Object o = selected();
	if (o == null || !(o instanceof Location))
	    return null;
	return (Location)o;
    }
}
