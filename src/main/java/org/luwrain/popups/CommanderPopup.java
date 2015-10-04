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
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class CommanderPopup extends CommanderArea implements Popup, PopupClosingRequest
{
    public static final int ACCEPT_REGULAR_FILES = 1;
    public static final int ACCEPT_DIRECTORIES = 2;
    public static final int ACCEPT_ALL = ACCEPT_REGULAR_FILES | ACCEPT_DIRECTORIES;
    public static final int ACCEPT_MULTIPLE_SELECTION = 16;

    protected Luwrain luwrain;
    public final PopupClosing closing = new PopupClosing(this);
    private String name;
    private int flags;
    private int popupFlags;

    public CommanderPopup(Luwrain luwrain,
			  String name,
			  File file,
			  int flags,
			  int popupFlags)
    {
	super(new DefaultControlEnvironment(luwrain),
	      luwrain.os(),
	      file != null?file:luwrain.launchContext().userHomeDirAsFile(),
	      (flags & ACCEPT_MULTIPLE_SELECTION) != 0,
	      new NoHiddenCommanderFilter(),
	      new ByNameCommanderComparator());
	this.luwrain = luwrain;
	this.name = name;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (name == null)
	    throw new NullPointerException("name may not be null");
    }

@Override     public boolean onClick(File current, File[] selected)
    {
	return closing.doOk();
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (closing.onKeyboardEvent(event))
	    return true;
	if (!event.isCommand() && !event.isModified())
	    switch(event.getCharacter())
	    {
	    case '=':
		setFilter(new AllFilesCommanderFilter());
		refresh();
		return true;
	    case '-':
		setFilter(new NoHiddenCommanderFilter());
		refresh();
		return true;
	    default:
		return super.onKeyboardEvent(event);
	    }
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ENTER &&
	    event.withControlOnly())
	    return openImportantLocations();
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (closing.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public String getAreaName()
    {
	return name + super.getAreaName();
    }

    @Override public boolean onOk()
    {
	final File[] selected = selected();
	if (selected  == null || selected.length < 1)
	    return false;
	return true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    @Override public Luwrain getLuwrainObject()
    {
	return luwrain;
    }

    @Override public EventLoopStopCondition getStopCondition()
    {
	return closing;
    }

    @Override public boolean noMultipleCopies()
    {
	return (popupFlags & Popup.NO_MULTIPLE_COPIES) != 0;
    }

    @Override public boolean isWeakPopup()
    {
	return (popupFlags & Popup.WEAK) != 0;
    }

    private boolean openImportantLocations()
    {
	final File f = Popups.importantLocationsAsFile(luwrain, popupFlags);
	if (f == null)
	    return true;
	open(f, null);
	return true;
    }
}
