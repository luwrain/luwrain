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

package org.luwrain.app.jobs;

import java.util.*;
import java.io.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.base.*;
import org.luwrain.app.base.*;
import org.luwrain.core.JobsTracking.Entry;

final class MainLayout extends LayoutBase
{
    private final App app;
    final ListArea jobsArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	final ListArea.Params params = new ListArea.Params();
	params.context = getControlContext();
	params.model = new ListUtils.ListModel(app.jobs.entries);
	params.appearance = new Appearance();
	params.name = app.getStrings().appName();
	this.jobsArea = new ListArea(params);
	final Actions actions = actions();
	setAreaLayout(jobsArea, actions);
    }

    private final class Appearance implements ListArea.Appearance
    {
	@Override public void announceItem(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    if (!(item instanceof Entry))
	    {
		app.getLuwrain().setEventResponse(DefaultEventResponse.listItem(item.toString()));
		return;
	    }
	    final Entry entry = (Entry)item;
	    final Sounds sound;
	    if (entry.getStatus() == Job.Status.FINISHED)
		sound = entry.isFinishedSuccessfully()?Sounds.SELECTED:Sounds.ATTENTION; else
				sound = Sounds.LIST_ITEM;
	    app.getLuwrain().setEventResponse(DefaultEventResponse.listItem(sound, entry.getInstanceName(), null));
	}
	@Override public String getScreenAppearance(Object item, Set<Flags> flags)
	{
	    NullCheck.notNull(item, "item");
	    NullCheck.notNull(flags, "flags");
	    return item.toString();
	}
	@Override public int getObservableLeftBound(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return 0;
	}
	@Override public int getObservableRightBound(Object item)
	{
	    NullCheck.notNull(item, "item");
	    return getScreenAppearance(item, EnumSet.noneOf(Flags.class)).length();
	}
    }
}
