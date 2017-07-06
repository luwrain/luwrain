/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.base.Partition;

public class Popups
{
    static public final Set<Popup.Flags> DEFAULT_POPUP_FLAGS = EnumSet.noneOf(Popup.Flags.class);

    static public String simple(Luwrain luwrain,
				String name, String prefix,
				String text, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(text, "text");
	NullCheck.notNull(popupFlags, "popupFlags");
	final SimpleEditPopup popup = new SimpleEditPopup(luwrain, name, prefix, text, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.text ();
    }

    static public String simple(Luwrain luwrain,
				String name, String prefix,
				String text)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(text, "text");
	final SimpleEditPopup popup = new SimpleEditPopup(luwrain, name, prefix, text, DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.text ();
    }

    static public String editWithHistory(Luwrain luwrain,
				String name, String prefix, String text, 
				Set<String> history, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(text, "text");
	NullCheck.notNull(history, "history");
	NullCheck.notNull(popupFlags, "popupFlags");
	final EditListPopup popup = new EditListPopup(luwrain, 
							new EditListPopupUtils.FixedModel(history.toArray(new String[history.size()])),
name, prefix, text, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	history.add(popup.text());
	return popup.text ();
    }

    static public String editWithHistory(Luwrain luwrain,
				String name, String prefix, String text, 
				Set<String> history)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(text, "text");
	NullCheck.notNull(history, "history");
	return editWithHistory(luwrain, name, prefix, text, history, DEFAULT_POPUP_FLAGS);
    }


    static public Object fixedList(Luwrain luwrain,
				   String name, final Object[] items,
				   Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNullItems(items, "items");
	NullCheck.notNull(popupFlags, "popupFlags");
	final ListArea.Model model = new ListArea.Model(){
		@Override public int getItemCount() { return items.length; }
		@Override public Object getItem(int index) { return index < items.length?items[index]:null; }
		@Override public boolean toggleMark(int index) { return false; }
		@Override public void refresh() {}
	    };
	final ListArea.Params params = new ListArea.Params();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.name = name;
	params.model = model;
	params.appearance = new ListUtils.DefaultAppearance(params.environment, Suggestions.POPUP_LIST_ITEM);
	//	params.flags = ListArea.Params.loadPopupFlags(luwrain.getRegistry());
	params.flags = EnumSet.of(ListArea.Flags.EMPTY_LINE_TOP);
	final ListPopup popup = new ListPopup(luwrain, params, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.selected();
    }

    static public Object fixedList(Luwrain luwrain,
				   String name, Object[] items)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNullItems(items, "items");
	return fixedList(luwrain, name, items, DEFAULT_POPUP_FLAGS);
    }

    static public File path(Luwrain luwrain,
			    String name, String prefix,
			    Path startWith, Path defaultPath,
			    FilePopup.Acceptance acceptance, 
			    Set<FilePopup.Flags> filePopupFlags, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(acceptance, "acceptance");
	NullCheck.notNull(filePopupFlags, "filePopupFlags");
	NullCheck.notNull(popupFlags, "popupFlags");
	final FilePopup popup = new FilePopup(luwrain, name, prefix, acceptance,
					      startWith != null?startWith:luwrain.getPathProperty("luwrain.dir.userhome"),
					      defaultPath != null?defaultPath:luwrain.getPathProperty("luwrain.dir.userhome"),
					      filePopupFlags, popupFlags);
	luwrain.popup(popup);
	return popup.closing.cancelled()?null:popup.result();
    }

    static public File path(Luwrain luwrain,
			    String name, String prefix,
			    Path startWith, Path defaultPath, FilePopup.Acceptance acceptance)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(acceptance, "acceptance");
	return path(luwrain, name, prefix,
		    startWith, defaultPath, 
		    acceptance, loadFilePopupFlags(luwrain), DEFAULT_POPUP_FLAGS);
    }

    static public File path(Luwrain luwrain,
			    String name, String prefix,
			    Path startWith, FilePopup.Acceptance acceptance)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(acceptance, "acceptance");
	return path(luwrain, name, prefix,
		    startWith, startWith, 
		    acceptance, loadFilePopupFlags(luwrain), DEFAULT_POPUP_FLAGS);
    }

    static public File path(Luwrain luwrain,
			    String name, String prefix,
			    Path startWith)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	return path(luwrain, name, prefix,
		    startWith, startWith, 
		    (fileToCheck, announce)->{return true;}, loadFilePopupFlags(luwrain), DEFAULT_POPUP_FLAGS);
    }

    static public File directory(Luwrain luwrain, String name, String prefix, File startWith)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(prefix, "prefix");
	final File res = path(luwrain, name, prefix, startWith != null?startWith.toPath():null, (fileToCheck, announce)->{
		if (!fileToCheck.isDirectory())
		{
		    if (announce)
			luwrain.message("Указанный путь не является каталогом", Luwrain.MESSAGE_ERROR);
		    return false;
		}
		return true;
	    });
	if (res == null)
	    return null;
	return res;
    }


    static public Path commanderSingle(Luwrain luwrain, String name,
				       Path path, FilePopup.Acceptance acceptance,
				       CommanderArea.ClickHandler clickHandler, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(path, "path");
	NullCheck.notNull(popupFlags, "popupFlags");
	luwrain.message(name);
	final CommanderPopup popup = new CommanderPopup(luwrain, name, path, acceptance, clickHandler, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.result();
    }

    static public Path commanderSingle(Luwrain luwrain, String name,
				       Path path, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(path, "path");
	NullCheck.notNull(popupFlags, "popupFlags");
	return commanderSingle(luwrain, name, path, null, null, popupFlags);
    }

    static public Partition mountedPartitions(Luwrain luwrain, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(popupFlags, "popupFlags");
	final PartitionsPopup popup = new PartitionsPopup(luwrain, (PartitionsPopup.Control)luwrain.getSharedObject("luwrain.partitionspopupcontrol"),
							  luwrain.i18n().getStaticStr("PartitionsPopupName"), popupFlags, EnumSet.of(ListArea.Flags.EMPTY_LINE_TOP));
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return (Partition)popup.result().getObject();
    }

    static public Partition mountedPartitions(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	return mountedPartitions(luwrain, DEFAULT_POPUP_FLAGS);
    }

    static public String fixedEditList(Luwrain luwrain,
				       String name, String prefix, String text,
				       String[] items)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(text, "text");
	NullCheck.notNullItems(items, "items");
	final EditListPopup popup = new EditListPopup(luwrain, new EditListPopupUtils.FixedModel(items),
						      name, prefix, text, DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.text();
    }

    static public boolean confirmDefaultYes(Luwrain luwrain,
					    String name, String text)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(text, "text");
	final YesNoPopup popup = new YesNoPopup(luwrain, name, text, true, DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return false;
	return popup.result();
    }

    static public boolean confirmDefaultNo(Luwrain luwrain,
					   String name, String text)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(text, "text");
	final YesNoPopup popup = new YesNoPopup(luwrain, name, text, false, DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return false;
	return popup.result();
    }

    static public Set<FilePopup.Flags> loadFilePopupFlags(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Settings.UserInterface sett = Settings.createUserInterface(luwrain.getRegistry());
	if (sett.getFilePopupSkipHidden(false))
	    return EnumSet.of(FilePopup.Flags.SKIP_HIDDEN);
	return EnumSet.noneOf(FilePopup.Flags.class);
    }
}
