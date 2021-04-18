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
	params.model = new ListUtils.FixedModel();
	params.appearance = new ListUtils.DefaultAppearance(getControlContext());
	params.name = app.getStrings().appName();
	this.jobsArea = new ListArea(params);
	final Actions actions = actions();
	setAreaLayout(jobsArea, actions);
    }
}
