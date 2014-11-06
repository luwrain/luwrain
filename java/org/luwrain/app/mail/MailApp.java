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

package org.luwrain.app.mail;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class MailApp implements Application, Actions
{
    private Luwrain luwrain;
    private Base base = new Base();
    private StringConstructor stringConstructor = null;

    private TreeArea foldersArea;
    private TableArea summaryArea;
    private MessageArea messageArea;

    public boolean onLaunch(Luwrain luwrain)
    {
	Object o = Langs.requestStringConstructor("mail-reader");
	if (o == null)
	    return false;
	stringConstructor = (StringConstructor)o;
	this.luwrain = luwrain;
	base.init(luwrain, stringConstructor);
	if (!base.isValid())
	{
	    luwrain.message(stringConstructor.noMailStoring());
	    return false;
	}
	createFoldersArea();
	createSummaryArea();
	createMessageArea();
	return true;
    }

    public void openFolder(Object folder)
    {
	if (folder == null || !base.isStoredMailGroup(folder))
	    return;
	if (base.openFolder(folder, summaryArea))
	    gotoSummary(); else
	    luwrain.message(stringConstructor.errorOpeningFolder());
    }

    private void createFoldersArea()
    {
	final Actions a = this;
	final StringConstructor s = stringConstructor;
	foldersArea = new TreeArea(new DefaultControlEnvironment(luwrain),
				   base.getFoldersModel(),
				   stringConstructor.foldersAreaName()){
		private StringConstructor stringConstructor = s;
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event.isCommand() &&
			!event.isModified() &&
			event.getCommand() == KeyboardEvent.TAB)
		    {
			actions.gotoSummary();
			return true;
		    }
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.close();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public void onClick(Object obj)
		{
		    if (obj != null)
			actions.openFolder(obj);
		}
	    };
    }

    private void createSummaryArea()
    {
	final Actions a = this;
	final StringConstructor s = stringConstructor;
	summaryArea = new TableArea(new DefaultControlEnvironment(luwrain),
				    base.getSummaryModel(),
				    stringConstructor.summaryAreaName(),
				    base.getSummaryAppearance(),
				    null) { //Click handler;
		private StringConstructor stringConstructor = s;
		private Actions actions = a;
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event.isCommand() &&
			!event.isModified() &&
			event.getCommand() == KeyboardEvent.TAB)
		    {
			actions.gotoMessage();
			return true;
		    }
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.close();
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public boolean onClick(TableModel model,
					      int col,
					      int row,
					      Object cell)
		{
		    //FIXME:
		    return false;
		}
	    };
    }

    private void createMessageArea()
    {
	messageArea = new MessageArea(luwrain, this, stringConstructor);
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, summaryArea, messageArea);
    }

    public void gotoFolders()
    {
	luwrain.setActiveArea(foldersArea);
    }

    public void gotoSummary()
    {
	luwrain.setActiveArea(summaryArea);
    }

    public void gotoMessage()
    {
	luwrain.setActiveArea(messageArea);
    }

    public void close()
    {
	luwrain.closeApp();
    }
}
