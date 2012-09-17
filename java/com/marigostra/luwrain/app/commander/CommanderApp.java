/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package com.marigostra.luwrain.app.commander;

import com.marigostra.luwrain.core.*;

public class CommanderApp implements Application, CommanderActions
{
    private Object instance = null;
    private CommanderStringConstructor stringConstructor = null;
    private PanelArea leftPanel;
    private PanelArea rightPanel;
    private TasksArea tasks;

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("commander");
	if (o == null)
	    return false;
	stringConstructor = (CommanderStringConstructor)o;
	leftPanel = new PanelArea(this, stringConstructor, PanelArea.LEFT);
	rightPanel = new PanelArea(this, stringConstructor, PanelArea.RIGHT);
	tasks = new TasksArea(this, stringConstructor);
	this.instance = instance;
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, leftPanel, rightPanel, tasks);//FIXME:Temporary layout type!!!
    }

    public void gotoLeftPanel()
    {
	Dispatcher.setActiveArea(instance, leftPanel);
    }

    public void gotoRightPanel()
    {
	Dispatcher.setActiveArea(instance, rightPanel);
    }

    public void gotoTasks()
    {
	Dispatcher.setActiveArea(instance, tasks);
    }

    public void closeCommander()
    {
	Dispatcher.closeApplication(instance);
    }
}
