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

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.pim.*;
import org.luwrain.controls.*;

public class MailReaderApp implements Application, Actions
{
    private Object instance;
    private StringConstructor stringConstructor;
    private FoldersTreeModel foldersModel;
    private TreeArea foldersArea;
    private SummaryArea summaryArea;
    private MessageArea messageArea;
    MailStoring mailStoring;

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("mail-reader");
	if (o == null)
	    return false;
	stringConstructor = (StringConstructor)o;
	mailStoring = Luwrain.getPimManager().getMailStoring();
	if (mailStoring == null)
	{
	    Luwrain.message("No mail storing");//FIXME:string constructor; ;
	    return false;
	}
	foldersModel = new FoldersTreeModel(mailStoring, stringConstructor);
	createAreas();
	this.instance = instance;
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, foldersArea, summaryArea, messageArea);
    }

    public void openFolder(Object folder)
    {
	//	summaryArea.show(group);
    }

    public void gotoFolders()
    {
	Luwrain.setActiveArea(instance, foldersArea);
    }

    public void gotoSummary()
    {
	Luwrain.setActiveArea(instance, summaryArea);
    }

    public void gotoMessage()
    {
	Luwrain.setActiveArea(instance, messageArea);
    }

    public void close()
    {
	Luwrain.closeApp(instance);
    }

    private void createAreas()
    {
	final Actions a = this;
	final StringConstructor s = stringConstructor;
	foldersArea = new TreeArea(foldersModel, stringConstructor.foldersAreaName()){
		private StringConstructor stringConstructor = s;
		private Actions actions = a;
		public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event.isCommand() && !event.isModified() &&
			event.getCommand() == KeyboardEvent.TAB)
		    {
			actions.gotoSummary();
			return true;
		    }
		    return super.onKeyboardEvent(event);
		}
		public boolean onEnvironmentEvent(EnvironmentEvent event)
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
		public void onClick(Object obj)
		{
		    if (obj != null)
			actions.openFolder(obj);
		}
	    };

    }


}
