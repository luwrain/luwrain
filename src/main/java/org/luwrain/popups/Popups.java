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

//LWR_API 1.0

package org.luwrain.popups;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public final class Popups
{
    static final String LOG_COMPONENT = "popups";
    static public final Set<Popup.Flags> DEFAULT_POPUP_FLAGS = EnumSet.noneOf(Popup.Flags.class);

    static public String simple(Luwrain luwrain,
				String name,
				String prefix,
				String text,
				StringAcceptance acceptance,
				Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(text, "text");
	NullCheck.notNull(popupFlags, "popupFlags");
	final SimpleEditPopup popup = new SimpleEditPopup(luwrain, name, prefix, text, acceptance, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.text ();
    }

    static public String simple(Luwrain luwrain,
				String name, String prefix,
				String text,
				StringAcceptance acceptance)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(text, "text");
	return simple(luwrain, name, prefix, text, acceptance, DEFAULT_POPUP_FLAGS);
    }

        static public String simple(Luwrain luwrain,
				String name, String prefix,
				String text)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(text, "text");
	return simple(luwrain, name, prefix, text, null, DEFAULT_POPUP_FLAGS);
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
		@Override public void refresh() {}
	    };
	final ListArea.Params params = new ListArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.name = name;
	params.model = model;
	params.appearance = new ListUtils.DefaultAppearance(params.context, Suggestions.POPUP_LIST_ITEM);
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

    static private File path(Luwrain luwrain,
			     String name, String prefix,
			     File startWith, File defaultPath,
			     FileAcceptance acceptance, 
			     Set<FilePopup.Flags> filePopupFlags, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(acceptance, "acceptance");
	NullCheck.notNull(filePopupFlags, "filePopupFlags");
	NullCheck.notNull(popupFlags, "popupFlags");
	final FilePopup popup = new FilePopup(luwrain, name, prefix, acceptance,
					      startWith != null?startWith:luwrain.getFileProperty("luwrain.dir.userhome"),
					      defaultPath != null?defaultPath:luwrain.getFileProperty("luwrain.dir.userhome"),
					      filePopupFlags, popupFlags);
	luwrain.popup(popup);
	return popup.closing.cancelled()?null:popup.result();
    }

    static public File path(Luwrain luwrain, String name, String prefix, File startWith, FileAcceptance acceptance)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(acceptance, "acceptance");
	return path(luwrain, name, prefix,
		    startWith, null, 
		    acceptance, loadFilePopupFlags(luwrain), DEFAULT_POPUP_FLAGS);
    }

    static public File path(Luwrain luwrain, String name, String prefix, FileAcceptance acceptance)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(prefix, "prefix");
	NullCheck.notNull(acceptance, "acceptance");
	return path(luwrain, name, prefix,
		    null, null,
		    acceptance, loadFilePopupFlags(luwrain), DEFAULT_POPUP_FLAGS);
    }

    static public File existingFile(Luwrain luwrain, String name, String prefix, File startWith, String[] extensions)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(prefix, "prefix");
	NullCheck.notNull(startWith, "startWith");
	NullCheck.notNullItems(extensions, "extensions");
	final CommanderArea.ClickHandler<File> clickHandler = (area, file, dir)->{
	    if (dir)
		return CommanderArea.ClickHandler.Result.OPEN_DIR;
	    return CommanderArea.ClickHandler.Result.REJECTED;
	};
	final FileAcceptance acceptance = (file, announcement)->{
	    return true;
	};
	final CommanderPopup popup = new CommanderPopup(luwrain, prefix,
							luwrain.getFileProperty("luwrain.dir.userhome"), acceptance, clickHandler, DEFAULT_POPUP_FLAGS){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if(event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case INSERT:
			    return mkdir(luwrain, opened());
			}
		    return super.onInputEvent(event);
		}
	    };
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.result();
    }

    static public File existingFile(Luwrain luwrain, String name, String prefix, String[] extensions)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(prefix, "prefix");
	NullCheck.notNullItems(extensions, "extensions");
	return existingFile(luwrain, name, prefix, luwrain.getFileProperty("luwrain.dir.userhome"), extensions);
    }

    static public File existingDir(Luwrain luwrain, String name, String prefix, File startWith)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(prefix, "prefix");
	final CommanderArea.ClickHandler<File> clickHandler = (area, file, dir)->{
	    if (dir)
		return CommanderArea.ClickHandler.Result.OPEN_DIR;
	    return CommanderArea.ClickHandler.Result.REJECTED;
	};
	final FileAcceptance acceptance = (file, announcement)->{
	    return true;
	};
	final CommanderPopup popup = new CommanderPopup(luwrain, prefix,
							luwrain.getFileProperty("luwrain.dir.userhome"), acceptance, clickHandler, DEFAULT_POPUP_FLAGS){
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if(event.isSpecial() && !event.isModified())
			switch(event.getSpecial())
			{
			case INSERT:
			    return mkdir(luwrain, opened());
			}
		    return super.onInputEvent(event);
		}
	    };
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.result();
    }

        static public File existingDir(Luwrain luwrain, String name, String prefix)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notEmpty(name, "name");
	NullCheck.notEmpty(prefix, "prefix");
	return existingDir(luwrain, name, prefix, null);
	    }

    static private boolean mkdir(Luwrain luwrain, File createIn)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(createIn, "createIn");
	final FilePopup newDirPopup = new FilePopup(luwrain, "Новый каталог", "Имя нового каталога:",
						    null, new File(""), createIn, loadFilePopupFlags(luwrain), DEFAULT_POPUP_FLAGS){
		@Override public boolean onOk()
		{
		    final File file = result();
		    if (file == null)
			return false;
		    if (file.mkdir())
			return true;
		    luwrain.message(luwrain.i18n().getStaticStr("UnableToCreateDir"), Luwrain.MessageType.ERROR);
		    return false;
		}
	    };
	luwrain.popup(newDirPopup);
	return true;
    }

    static public File disksVolumes(Luwrain luwrain, String name, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(popupFlags, "popupFlags");
	final DisksPopup popup = new DisksPopup(luwrain, name, popupFlags);
	luwrain.popup(popup);
	if (popup.wasCancelled())
	    return null;
	return popup.result();
    }

        static public File disksVolumes(Luwrain luwrain, String name)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	return disksVolumes(luwrain, name, DEFAULT_POPUP_FLAGS);
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
