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
    private Luwrain luwrain;
    private StringConstructor stringConstructor = null;
    private PanelArea leftPanel;
    private PanelArea rightPanel;
    private TasksArea tasks;

    public boolean onLaunch(Luwrain luwrain)
    {
	Object o = Langs.requestStringConstructor("commander");
	if (o == null)
	    return false;
	this.luwrain = luwrain;
	stringConstructor = (StringConstructor)o;
	leftPanel = new PanelArea(luwrain, this, stringConstructor, PanelArea.LEFT);
	rightPanel = new PanelArea(luwrain, this, stringConstructor, PanelArea.RIGHT);
	tasks = new TasksArea(luwrain, this, stringConstructor);
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
	FilePopup popup = new FilePopup(luwrain, stringConstructor.copyPopupName(),
					stringConstructor.copyPopupPrefix(filesToCopy), copyTo);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return true;
	copyTo = popup.getFile();
	Operations.copy(luwrain, stringConstructor, tasks, filesToCopy, copyTo);
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
	FilePopup popup = new FilePopup(luwrain, stringConstructor.movePopupName(),
					stringConstructor.movePopupPrefix(filesToMove), moveTo);
	luwrain.popup(popup);
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
	FilePopup popup = new FilePopup(luwrain, stringConstructor.mkdirPopupName(),
					stringConstructor.mkdirPopupPrefix(), createIn);
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return true;
	return true;
    }

    public boolean delete(int panelSide)
    {
	File[] filesToDelete = panelSide == PanelArea.LEFT?leftPanel.getSelected():rightPanel.getSelected();
	if (filesToDelete == null || filesToDelete.length < 1)
	    return false;
	YesNoPopup popup = new YesNoPopup(luwrain, stringConstructor.delPopupName(),
					stringConstructor.delPopupPrefix(filesToDelete), false);
	luwrain.popup(popup);
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
	    luwrain.openFiles(fileNames);
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_RIGHT_BOTTOM, leftPanel, rightPanel, tasks);
    }

    public void gotoLeftPanel()
    {
	luwrain.setActiveArea(leftPanel);
    }

    public void gotoRightPanel()
    {
	luwrain.setActiveArea(rightPanel);
    }

    public void gotoTasks()
    {
	luwrain.setActiveArea(tasks);
    }

    public void close()
    {
	luwrain.closeApp();
    }
}
