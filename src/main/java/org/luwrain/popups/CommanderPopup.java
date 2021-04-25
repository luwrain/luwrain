/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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
import org.luwrain.controls.CommanderArea.Filter;

public class CommanderPopup extends CommanderArea<File> implements CommanderArea.ClickHandler<File>, Popup, PopupClosingTranslator.Provider
{
    //    static public final Filter<File> FILTER_ALL = new CommanderUtilsFile.Filter(EnumSet.noneOf(CommanderUtilsFile.Filter.Flags.class));
    static public final Filter<File> FILTER_ALL = new CommanderUtils.AllEntriesFilter();
    static public final Filter<File> FILTER_NO_HIDDEN = new CommanderUtilsFile.Filter(EnumSet.of(CommanderUtilsFile.Filter.Flags.NO_HIDDEN));

    protected final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final Luwrain luwrain;
    protected final String name;
    protected final Filter<File> filter;
    protected final Set<Popup.Flags> popupFlags;
    protected File result;
    protected boolean filterCancelled;

    public CommanderPopup(Luwrain luwrain, String name,
			  File file, Filter<File> filter,
			  Set<Popup.Flags> popupFlags)
    {
	super(newParams(luwrain, filter != null?filter:FILTER_ALL));
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(file, "file");
	NullCheck.notNull(popupFlags, "popupFlags");
	this.luwrain = luwrain;
	this.name = name;
	this.popupFlags = popupFlags;
	this.filter = filter != null?filter:FILTER_ALL;
	this.filterCancelled = this.filter == FILTER_ALL;
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

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	switch(event.getCode())
	{
	case INTRODUCE:
	    luwrain.speak(getAreaName(), Sounds.INTRO_POPUP);
	    return true;
	    		    case ACTION:
			if (ActionEvent.isAction(event, "mkdir"))
			    return Popups.mkdir(luwrain, opened());
			if (ActionEvent.isAction(event, "cancel-filter"))
			{
			    setCommanderFilter(FILTER_ALL);
			    filterCancelled = true;
			    reread(true);
			    return true;
			}
			return false;
	case PROPERTIES:
	    final File f = Popups.disks(luwrain, "Выберите диск:");
	    if (f == null)
		return false;
	    open(f, null, false);
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

    		@Override public Action[] getAreaActions()
		{
		    final List<Action> res = new ArrayList();
		    if (!filterCancelled)
			res.add(new Action("cancel-filter", "Показать все файлы", new InputEvent(InputEvent.Special.F5)));
		    res.add(new Action("mkdir", "Создать каталог", new InputEvent(InputEvent.Special.INSERT)));
		    return res.toArray(new Action[res.size()]);
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

    public boolean isCancelled()
    {
	return closing.cancelled();
    }

    static private CommanderArea.Params<File> newParams(Luwrain luwrain, CommanderArea.Filter<File> filter)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(filter, "filter");
	final CommanderArea.Params<File> params = CommanderUtilsFile.createParams(new DefaultControlContext(luwrain));
	params.filter = filter;
	params.comparator = new CommanderUtils.ByNameComparator();
	return params;
    }
}
