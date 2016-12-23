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
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
//import org.luwrain.util.*;

public class CommanderPopup extends CommanderArea implements CommanderArea.ClickHandler, Popup, PopupClosingRequest
{
    protected final Luwrain luwrain;
    public final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final String name;
    protected final FilePopup.Acceptance acceptance;
    protected final Set<Popup.Flags> popupFlags;
    protected Path result;

    public CommanderPopup(Luwrain luwrain, String name,
			  Path path, FilePopup.Acceptance acceptance,
			  CommanderArea.ClickHandler clickHandler, Set<Popup.Flags> popupFlags)
    {
	super(constructParams(luwrain), path);
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(popupFlags, "popupFlags");
	this.luwrain = luwrain;
	this.name = name;
	this.popupFlags = popupFlags;
	this.acceptance = acceptance;
	setClickHandler(clickHandler != null?clickHandler:this);
    }

    @Override public ClickHandler.Result onCommanderClick(CommanderArea area, Path path, boolean dir)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(path, "path");
	if (dir)
	    return ClickHandler.Result.OPEN_DIR;
	result = path;
	closing.doOk();
	return ClickHandler.Result.OK;
    }

    public Path result()
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
		setCommanderFilter(new CommanderUtils.AllFilesFilter());
		refresh();
		return true;
	    case '-':
		setCommanderFilter(new CommanderUtils.NoHiddenFilter());
		refresh();
		return true;
	    }
	if (event.isSpecial() && event.withAltOnly())
	    switch(event.getSpecial())
	    {
	    case ENTER:
		openMountedPartitions();
		return true;
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(event.getCode())
	{
	case OK:
	    result = opened();
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
	return acceptance != null?acceptance.pathAcceptable(result()):true;
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

    @Override public Set<Popup.Flags> getPopupFlags()
    {
	return popupFlags;
    }

    private void openMountedPartitions()
    {
	final org.luwrain.hardware.Partition part = Popups.mountedPartitions(luwrain, popupFlags);
	if (part == null)
	    return;
	open(part.file().toPath(), null);
    }

    static private CommanderArea.Params constructParams(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final CommanderArea.Params params = new CommanderArea.Params();
	params.environment = new DefaultControlEnvironment(luwrain);
	params.appearance = new CommanderUtils.DefaultAppearance(params.environment);
	//    public CommanderArea.ClickHandler clickHandler;
	params.selecting = false;
	params.filter = new CommanderUtils.NoHiddenFilter();
	params.comparator = new CommanderUtils.ByNameComparator();
	return params;
    }
}
