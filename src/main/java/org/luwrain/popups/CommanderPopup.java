/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.popups;

import java.util.*;
import java.io.*;
//import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.io.*;

public class CommanderPopup extends CommanderArea<File> implements CommanderArea.ClickHandler<File>, Popup, PopupClosingTranslator.Provider
{
    protected final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final Luwrain luwrain;
    protected final String name;
    protected final FilePopup.Acceptance acceptance;
    protected final Set<Popup.Flags> popupFlags;
    protected File result;

    public CommanderPopup(Luwrain luwrain, String name, File file,
			  FilePopup.Acceptance acceptance, CommanderArea.ClickHandler<File> clickHandler, Set<Popup.Flags> popupFlags)
    {
	super(constructParams(luwrain));
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(file, "file");
	NullCheck.notNull(popupFlags, "popupFlags");
	this.luwrain = luwrain;
	this.name = name;
	this.popupFlags = popupFlags;
	this.acceptance = acceptance;
	setClickHandler(clickHandler != null?clickHandler:this);
setLoadingResultHandler((location, data, selectedIndex, announce)->{
		luwrain.runInMainThread(()->acceptNewLocation(location, data, selectedIndex, announce));
	    });
	open(file);
    }

    @Override public CommanderArea.ClickHandler.Result onCommanderClick(CommanderArea area, File file, boolean dir)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(file, "file");
	if (dir)
	    return ClickHandler.Result.OPEN_DIR;
	result = file;
	closing.doOk();
	return ClickHandler.Result.OK;
    }

    public File result()
    {
	return result;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onKeyboardEvent(event))
	    return true;
	if (!event.isSpecial() && !event.isModified())
	    switch(event.getChar())
	    {
	    case '=':
		//		setCommanderFilter(new CommanderUtils.AllFilesFilter());
		refresh();
		return true;
	    case '-':
		//		setCommanderFilter(new CommanderUtils.NoHiddenFilter());
		refresh();
		return true;
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onEnvironmentEvent(event);
	switch(event.getCode())
	{
	case PROPERTIES:
	    openMountedPartitions();
	    return true;
	case OK:
	    if (getSelectedEntry() != null)
		result = getSelectedEntry();
	    closing.doOk();
	    return true;
	default:
	    if (closing.onEnvironmentEvent(event))
		return true;
	    return super.onEnvironmentEvent(event);
	}
    }

    @Override public String getAreaName()
    {
	return name + super.getAreaName();
    }

    @Override public boolean onOk()
    {
	if (result() == null)
	    return false;
	return acceptance != null?acceptance.isPathAcceptable(result(), true):true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    @Override public Luwrain getLuwrainObject()
    {
	return luwrain;
    }

    @Override public boolean isPopupActive()
    {
	return closing.continueEventLoop();
    }

    @Override public Set<Popup.Flags> getPopupFlags()
    {
	return popupFlags;
    }

    public boolean wasCancelled()
    {
	return closing.cancelled();
    }

    protected void openMountedPartitions()
    {
	final org.luwrain.base.Partition part = Popups.mountedPartitions(luwrain, popupFlags);
	if (part == null)
	    return;
	open(part.getPartFile(), null);
    }

    static private CommanderArea.Params<File> constructParams(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final CommanderArea.Params<File> params = CommanderUtilsFile.createParams(new DefaultControlEnvironment(luwrain));
	params.filter = new CommanderUtilsFile.AllEntriesFilter();
	params.comparator = new CommanderUtilsFile.ByNameComparator();
	return params;
    }
}
