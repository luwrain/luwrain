/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.app.commander;

import java.io.*;
import org.luwrain.core.*;
import org.luwrain.popups.*;

public class CommanderApp implements Application, Actions
{
    private Object instance = null;
    private StringConstructor stringConstructor = null;
    private PanelArea leftPanel;
    private PanelArea rightPanel;
    private TasksArea tasks;

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("commander");
	if (o == null)
	    return false;
	stringConstructor = (StringConstructor)o;
	leftPanel = new PanelArea(this, stringConstructor, PanelArea.LEFT);
	rightPanel = new PanelArea(this, stringConstructor, PanelArea.RIGHT);
	tasks = new TasksArea(this, stringConstructor);
	this.instance = instance;
	return true;
    }

    public boolean copy(int panelSide)
    {
	File[] filesToCopy = null;
	File copyTo = null;
	if (panelSide == PanelArea.LEFT)
	{
	    filesToCopy = leftPanel.getSelected();
	    copyTo= rightPanel.getCurrentDir(); 
	} else
	if (panelSide == PanelArea.RIGHT)
	{
	    filesToCopy = rightPanel.getSelected();
	    copyTo= leftPanel.getCurrentDir(); 
	} else
	    return false;
	if (filesToCopy == null || filesToCopy.length < 1|| copyTo == null)
	    return false;
	FilePopup popup = new FilePopup(instance, stringConstructor.copyPopupName(),
					stringConstructor.copyPopupPrefix(filesToCopy), copyTo);
	Luwrain.popup(instance, popup, popup.closing);
	if (popup.closing.cancelled())
	    return true;
	copyTo = popup.getFile();

	Task task = new Task(stringConstructor.copying(filesToCopy));
	tasks.addTask(task);
	DirCopyOperation op = new DirCopyOperation(tasks, task, filesToCopy, copyTo);
	Thread t = new Thread(op);
	t.start();
	return true;
    }

    public void refresh()
    {
	leftPanel.refresh();
	rightPanel.refresh();
    }

    public void openFiles(String[] fileNames)
    {
	Log.debug("commander", "need to open " + fileNames.length + " files");
	if (fileNames != null && fileNames.length > 0)
	    Luwrain.open(fileNames);
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_RIGHT_BOTTOM, leftPanel, rightPanel, tasks);
    }

    public void gotoLeftPanel()
    {
	Luwrain.setActiveArea(instance, leftPanel);
    }

    public void gotoRightPanel()
    {
	Luwrain.setActiveArea(instance, rightPanel);
    }

    public void gotoTasks()
    {
	Luwrain.setActiveArea(instance, tasks);
    }

    public void close()
    {
	Luwrain.closeApp(instance);
    }
}
