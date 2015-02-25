
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

    public static File open()
    {/*



    private File openPopupByApp(Application app,
			    String name,
			    String prefix,
			    File defaultValue)
    {
	if (app == null)
	    return null;
	final String chosenName = (name != null && !name.trim().isEmpty())?name.trim():strings.openPopupName();
	final String chosenPrefix = (prefix != null && !prefix.trim().isEmpty())?prefix.trim():strings.openPopupPrefix();
	File chosenDefaultValue = null;
	if (defaultValue == null)
		chosenDefaultValue = launchContext.userHomeDirAsFile(); else
	    chosenDefaultValue = defaultValue;
	FilePopup popup = new FilePopup(null, chosenName, chosenPrefix, chosenDefaultValue);
	goIntoPopup(app, popup, PopupManager.BOTTOM, popup.closing, true);
	if (popup.closing.cancelled())
	    return null;
	return popup.getFile();
    }
     */
	return null;
    }
}


