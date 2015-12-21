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

import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.hardware.Partition;

public class Popups
{
    static public String simple(Luwrain luwrain,
				String name, String prefix,
				String text)
    {
	final SimpleEditPopup popup = new SimpleEditPopup(luwrain, name, prefix, text);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.text ();
    }

    static public Object fixedList(Luwrain luwrain,
			    String name, Object[] items,
int popupFlags)
    {
	final Object[] items2 = items;
	final ListArea.Model model = new ListArea.Model(){
		private Object[] items = items2;
		@Override public int getItemCount()
		{
		    return items.length;
		}
		@Override public Object getItem(int index)
		{
		    return index < items.length?items[index]:null;
		}
		@Override public boolean toggleMark(int index)
		{
		    return false;
		}
		@Override public void refresh()
		{
		}
	    };
	final ListPopup popup = new ListPopup(luwrain, name, model, new DefaultListItemAppearance(new DefaultControlEnvironment(luwrain)), popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.selected();
    }

    static public Path open(Luwrain luwrain)
    {
	return open(luwrain, null, null, null, null, null, makeFilePopupFlags(luwrain, 0));
    }

    static public Path open(Luwrain luwrain,
			    Path startWith, Path defPath)
    {
	return open(luwrain, null, null, startWith, defPath, null, makeFilePopupFlags(luwrain, 0));
    }

    static public Path open(Luwrain luwrain,
			    Path startWith, Path defPath,
			    int popupFlags)
    {
	return open(luwrain, null, null, startWith, defPath, null, makeFilePopupFlags(luwrain, popupFlags));
    }

    static public Path open(Luwrain luwrain,
			    Path startWith, Path defPath,
			    DefaultFileAcceptance.Type fileType, String[] fileExtensions,
			    int popupFlags)
    {
	return open(luwrain, null, null, startWith, defPath, new DefaultFileAcceptance(fileType, fileExtensions), makeFilePopupFlags(luwrain, popupFlags));
    }

    static public Path open(Luwrain luwrain,
			    String name, String prefix,
			    Path startWith, Path defPath,
			    FilePopup.Acceptance acceptance, int popupFlags)
    {
	org.luwrain.core.Strings strings = (org.luwrain.core.Strings)luwrain.i18n().getStrings("luwrain.environment");
	final String chosenName = (name != null && !name.trim().isEmpty())?name.trim():strings.openPopupName();
	final String chosenPrefix = (prefix != null && !prefix.trim().isEmpty())?prefix.trim():strings.openPopupPrefix();
	final Path chosenStartWith = startWith != null?startWith:luwrain.launchContext().userHomeDirAsPath();
	final Path chosenDefPath = defPath != null?defPath:luwrain.launchContext().userHomeDirAsPath();
	FilePopup popup = new FilePopup(luwrain, chosenName, chosenPrefix, acceptance,
					chosenStartWith, chosenDefPath, makeFilePopupFlags(luwrain, popupFlags));
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.result();
    }

    static public Path chooseFile(Luwrain luwrain,
			    String name, String prefix,
			    Path startWith, Path defPath,
			    DefaultFileAcceptance.Type fileType)
    {
	return chooseFile(luwrain, name, prefix, startWith, defPath, fileType, new String[0], 0);
    }


    static public Path chooseFile(Luwrain luwrain,
			    String name, String prefix,
			    Path startWith, Path defPath,
			    DefaultFileAcceptance.Type fileType, String[] fileExtensions,
			    int popupFlags)
    {
	final FilePopup popup = new FilePopup(luwrain, name, prefix, 
					      new DefaultFileAcceptance(fileType, fileExtensions), 
					      startWith, defPath, makeFilePopupFlags(luwrain, popupFlags));
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.result();
    }

    static public Path[] commanderMultiple(Luwrain luwrain, String name,
					   Path path, int flags,
					   int popupFlags)
    {
	final CommanderPopup popup = new CommanderPopup(luwrain, name, path, flags | CommanderPopup.ACCEPT_MULTIPLE_SELECTION, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.selected();
    }

    static public Path commanderSingle(Luwrain luwrain, String name,
				       Path path, int flags,
					   int popupFlags)
    {
	final CommanderPopup popup = new CommanderPopup(luwrain, name, path, flags, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	final Path[] res = popup.selected();
	if (res == null || res.length != 1)
	    return null;
	return res[0];
    }

    static public Partition mountedPartitions(Luwrain luwrain, int popupFlags)
    {
	final PartitionsPopup popup = new PartitionsPopup(luwrain, new DefaultPartitionsPopupControl(luwrain, luwrain.getHardware()),
							  "Выберите раздел:", popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	final Object result = popup.result().getObject();
	if (result == null)
	    return null;
	return (Partition)result;
    }

    static public File mountedPartitionsAsFile(Luwrain luwrain, int popupFlags)
    {
	final Partition result = mountedPartitions(luwrain, popupFlags);
	if (result == null)
	    return null;
	return result.file();
    }

    static private int makeFilePopupFlags(Luwrain luwrain, int orig)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Settings.UserInterface ui = Settings.createUserInterface(luwrain.getRegistry());
	if (ui.getFilePopupSkipHidden(false))
	return orig | FilePopup.SKIP_HIDDEN;
	return orig;
    }
}
