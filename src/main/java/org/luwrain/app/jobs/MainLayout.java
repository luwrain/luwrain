/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.app.base.*;
import org.luwrain.core.JobsManager.Entry;

final class MainLayout extends LayoutBase
{
    private final App app;
    final ListArea<Entry> jobsArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.jobsArea = new ListArea<Entry>(listParams((params)->{
		    params.model = new ListUtils.ListModel<Entry>(app.jobs.entries);
		    params.appearance = new Appearance();
		    params.name = app.getStrings().appName();
		}));
	final Actions jobsActions = actions(
					    action("stop", app.getStrings().actionStop(), new InputEvent(InputEvent.Special.F5), this::actStop)
					    );
	setAreaLayout(jobsArea, jobsActions);
    }

    private boolean actStop()
    {
	final Object o = jobsArea.selected();
	if (o == null || !(o instanceof Entry))
	    return false;
	final Entry e = (Entry)o;
	if (e.getStatus() == JobLauncher.Status.FINISHED)
	    return false;
	e.stop();
	app.getLuwrain().playSound(Sounds.OK);
	return true;
	
    }

    private final class Appearance extends ListUtils.AbstractAppearance<Entry>
    {
	@Override public void announceItem(Entry entry, Set<Flags> flags)
	{
	    NullCheck.notNull(entry, "entry");
	    NullCheck.notNull(flags, "flags");
	    final Sounds sound;
	    if (entry.getStatus() == JobLauncher.Status.FINISHED)
		sound = entry.isFinishedSuccessfully()?Sounds.SELECTED:Sounds.ATTENTION; else
				sound = Sounds.LIST_ITEM;
	    app.setEventResponse(DefaultEventResponse.listItem(sound, entry.getInstanceName(), null));
	}
    }
}
