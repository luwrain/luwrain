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
	leftPanel = new PanelArea(instance, this, stringConstructor, PanelArea.LEFT);
	rightPanel = new PanelArea(instance, this, stringConstructor, PanelArea.RIGHT);
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
	Luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return true;
	copyTo = popup.getFile();
	Operations.copy(stringConstructor, tasks, filesToCopy, copyTo);
	return true;
    }

    public boolean move(int panelSide)
    {
	File[] filesToMove = null;
	File moveTo = null;
	if (panelSide == PanelArea.LEFT)
	{
	    filesToMove = leftPanel.getSelected();
	    moveTo= rightPanel.getCurrentDir(); 
	} else
	if (panelSide == PanelArea.RIGHT)
	{
	    filesToMove = rightPanel.getSelected();
	    moveTo= leftPanel.getCurrentDir(); 
	} else
	    return false;
	if (filesToMove == null || filesToMove.length < 1|| moveTo == null)
	    return false;
	FilePopup popup = new FilePopup(instance, stringConstructor.movePopupName(),
					stringConstructor.movePopupPrefix(filesToMove), moveTo);
	Luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return true;
	moveTo = popup.getFile();
	return true;
    }

    public boolean mkdir(int panelSide)
    {
	File createIn = panelSide == PanelArea.LEFT?leftPanel.getCurrentDir():rightPanel.getCurrentDir();
	if (createIn == null)
	    return false;
	FilePopup popup = new FilePopup(instance, stringConstructor.mkdirPopupName(),
					stringConstructor.mkdirPopupPrefix(), createIn);
	Luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return true;
	return true;
    }

    public boolean delete(int panelSide)
    {
	File[] filesToDelete = panelSide == PanelArea.LEFT?leftPanel.getSelected():rightPanel.getSelected();
	if (filesToDelete == null || filesToDelete.length < 1)
	    return false;
	YesNoPopup popup = new YesNoPopup(instance, stringConstructor.delPopupName(),
					stringConstructor.delPopupPrefix(filesToDelete), false);
	Luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return true;
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
	    Luwrain.openFiles(fileNames);
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
