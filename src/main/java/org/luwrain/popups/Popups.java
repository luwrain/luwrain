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

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.hardware.Partition;

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

    public static File file(Luwrain luwrain,
			    String name,
			    String prefix,
			    File startWith,
			    int acceptingFlags,
			    int popupFlags)
    {
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (prefix == null)
	    throw new NullPointerException("prefix may not be null");
	if (startWith == null)
	    throw new NullPointerException("startWith may not be null");
	FilePopup popup = new FilePopup(luwrain, name, prefix, startWith, popupFlags);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.getFile();
    }

    public static String simple(Luwrain luwrain,
				String name,
				String prefix,
String text)
    {
	SimpleEditPopup popup = new SimpleEditPopup(luwrain, name, prefix, text);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return null;
	return popup.text ();
    }

    static public Object fixedList(Luwrain luwrain,
			    String name,
			    Object[] items,
int popupFlags)
    {
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (items == null)
	    throw new NullPointerException("items may not be null");
	if (items.length < 1)
	    throw new IllegalArgumentException("items may not be empty");
	for(int i = 0;i < items.length;++i)
	    if (items[i] == null)
	    throw new NullPointerException("items[" + i + "] may not be null");
	final Object[] items2 = items;
	final ListModel model = new ListModel(){
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
}
