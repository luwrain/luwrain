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

package org.luwrain.popups;

import java.io.*;

import org.luwrain.core.*;
import org.luwrain.os.Location;

public class Popups
{
    static public File[] commanderMultiple(Luwrain luwrain,
					   String name,
					   File file,
					   int flags,
					   int popupFlags)
    {
	CommanderPopup popup = new CommanderPopup(luwrain, name, file, flags | CommanderPopup.ACCEPT_MULTIPLE_SELECTION, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.selected();
    }

    static public File commanderSingle(Luwrain luwrain,
					   String name,
					   File file,
					   int flags,
					   int popupFlags)
    {
	CommanderPopup popup = new CommanderPopup(luwrain, name, file, flags, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	final File[] res = popup.selected();
	if (res == null || res.length != 1)
	    return null;
	return res[0];
    }

    static public Location importantLocations(Luwrain luwrain, int popupFlags)
    {
	ImportantLocationsPopup popup = new ImportantLocationsPopup(luwrain, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	final Location l = popup.selectedLocation();
	return l;
    }

    static public File importantLocationsAsFile(Luwrain luwrain, int popupFlags)
    {
	ImportantLocationsPopup popup = new ImportantLocationsPopup(luwrain, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	final Location l = popup.selectedLocation();
	return l != null?l.file():null;
    }

    public static File open(Luwrain luwrain, int popupFlags)
    {
	return open(luwrain, null, null, null, popupFlags);
    }

    public static File open(Luwrain luwrain,
			    File startWith,
			    int popupFlags)
    {
	return open(luwrain, null, null, startWith, popupFlags);
    }

    public static File open(Luwrain luwrain,
			    String name,
			    String prefix,
			    File startWith,
			    int popupFlags)
    {
	org.luwrain.core.Strings strings = (org.luwrain.core.Strings)luwrain.i18n().getStrings("luwrain.environment");
	final String chosenName = (name != null && !name.trim().isEmpty())?name.trim():strings.openPopupName();
	final String chosenPrefix = (prefix != null && !prefix.trim().isEmpty())?prefix.trim():strings.openPopupPrefix();
	final File chosenStartWith = startWith != null?startWith:luwrain.launchContext().userHomeDirAsFile();
	FilePopup popup = new FilePopup(luwrain, chosenName, chosenPrefix, chosenStartWith, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.getFile();
    }
}
