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

public class CommanderPopup extends CommanderArea implements Popup, PopupClosingRequest
{
    public static final int ACCEPT_REGULAR_FILES = 1;
    public static final int ACCEPT_DIRECTORIES = 2;
    public static final int ACCEPT_ALL = ACCEPT_REGULAR_FILES | ACCEPT_DIRECTORIES;
    public static final int ACCEPT_MULTIPLE_SELECTION = 16;

    protected Luwrain luwrain;
    public final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected String name;
    protected int flags;
    protected Set<Popup.Flags> popupFlags;

    public CommanderPopup(Luwrain luwrain, String name,
			  Path path, int flags,
			  Set<Popup.Flags> popupFlags)
    {
	super(constructParams(), null);
	this.luwrain = luwrain;
	this.name = name;
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
    }

    public boolean onCommanderClick(Path current, Path[] selected)
    {
	return closing.doOk();
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (closing.onKeyboardEvent(event))
	    return true;
	if (!event.isSpecial() && !event.isModified())
	    switch(event.getChar())
	    {
	    case '=':
		setFilter(new CommanderUtils.AllFilesFilter());
		refresh();
		return true;
	    case '-':
		setFilter(new CommanderUtils.NoHiddenFilter());
		refresh();
		return true;
	    default:
		return super.onKeyboardEvent(event);
	    }
	if (event.isSpecial() &&
	    event.getSpecial() == KeyboardEvent.Special.ENTER &&
	    event.withShiftOnly())
	    return openMountedPartitions();
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
return marked().length > 0 || selectedPath() != null;
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

    private boolean openMountedPartitions()
    {
	final File f = Popups.mountedPartitionsAsFile(luwrain, EnumSet.noneOf(Popup.Flags.class));
	if (f == null)
	    return true;
	open(f.toPath(), null);
	return true;
    }

    static private CommanderParams constructParams()
    {
	return null;
    }
}
