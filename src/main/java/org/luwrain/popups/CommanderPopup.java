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
import org.luwrain.io.*;

public class CommanderPopup extends CommanderArea<File> implements CommanderArea.ClickHandler<File>, Popup, PopupClosingTranslator.Provider
{
    protected final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final Luwrain luwrain;
    protected final String name;
    protected final Set<Popup.Flags> popupFlags;
    protected File result;

    public CommanderPopup(Luwrain luwrain, String name, File file,
			  CommanderArea.Filter<File> filter, Set<Popup.Flags> popupFlags)
    {
	super(constructParams(luwrain, filter));
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(file, "file");
	NullCheck.notNull(popupFlags, "popupFlags");
	this.luwrain = luwrain;
	this.name = name;
	this.popupFlags = popupFlags;
	setClickHandler(this);
	setLoadingResultHandler((location, data, selectedIndex, announce)->{
		luwrain.runUiSafely(()->acceptNewLocation(location, data, selectedIndex, announce));
	    });
	open(file, null, false);
    }

    @Override public CommanderArea.ClickHandler.Result onCommanderClick(CommanderArea area, File file, boolean dir)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(file, "file");
	if (dir)
	    return ClickHandler.Result.OPEN_DIR;
	result = file;
	return closing.doOk()?ClickHandler.Result.OK:ClickHandler.Result.REJECTED;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	switch(event.getCode())
	{
	case INTRODUCE:
	    //	    luwrain.silence();
	    luwrain.speak(getAreaName(), Sounds.INTRO_POPUP);
	    return true;
	default:
	    if (closing.onSystemEvent(event))
		return true;
	    return super.onSystemEvent(event);
	}
    }

    @Override public String getAreaName()
    {
	return name + super.getAreaName();
    }

    @Override public boolean onOk()
    {
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

    static private CommanderArea.Params<File> constructParams(Luwrain luwrain, CommanderArea.Filter<File> filter)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final CommanderArea.Params<File> params = CommanderUtilsFile.createParams(new DefaultControlContext(luwrain));
	params.filter = filter != null?filter:new CommanderUtils.AllEntriesFilter();
	params.comparator = new CommanderUtils.ByNameComparator();
	return params;
    }
}
