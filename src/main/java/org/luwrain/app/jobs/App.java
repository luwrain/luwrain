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

public final class App extends AppBase<Strings>
{
    final JobsTracking jobs;
    private MainLayout mainLayout = null;

    public App(JobsTracking jobs)
    {
    super(Strings.NAME, Strings.class);
    NullCheck.notNull(jobs, "jobs");
    this.jobs = jobs;
    }

@Override protected AreaLayout onAppInit()
{
    this.mainLayout = new MainLayout(this);
return this.mainLayout.getAreaLayout();
}

    @Override public boolean onEscape(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	closeApp();
	return true;
    }

}
