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

package org.luwrain.app.news;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.pim.*;

public class NewsReaderApp implements Application, Actions
{
    private Object instance;
    private StringConstructor stringConstructor = null;
    private GroupModel groupModel;
    private ListArea groupArea;
    private SummaryArea summaryArea;
    private ViewArea viewArea;
    private NewsStoring newsStoring;
    private StoredNewsGroup[] groups;

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("news-reader");
	if (o == null)
	{
	    Log.error("news", "no string constructor for news reader");
	    return false;
	}
	stringConstructor = (StringConstructor)o;
	newsStoring = Luwrain.getPimManager().getNewsStoring();
	if (newsStoring == null)
	{
	    Luwrain.message("No news storing");//FIXME:
	    return false;
	}
	createAreas();
	this.instance = instance;
	return true;
    }

    private void createAreas()
    {
	final Actions a = this;
	final StringConstructor s = stringConstructor;
	groupModel = new GroupModel(newsStoring);
	groupArea = new ListArea(groupModel) {
		private StringConstructor stringConstructor = s;
		private Actions actions = a;
		public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    if (event.isCommand() && !event.isModified())
			switch(event.getCommand())
			{
			case KeyboardEvent.TAB:
			    actions.gotoArticles();
			    return true;
			case KeyboardEvent.ENTER:
			    if (getSelectedIndex() >= 0)
				actions.openGroup(getSelectedIndex());
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
		public String getName()
		{
		    return stringConstructor.groupAreaName();
		}
	    };
	summaryArea = new SummaryArea(this, stringConstructor);
	viewArea = new ViewArea(this, stringConstructor);
    }

    public void openGroup(int index)
    {
	if (groups == null || 
index < 0 ||
	    index >= groups.length)
	{
	    Log.warning("news", "trying to open non-existing group with index " + index + " or groups list is not prepared");
	    return;
	}
	Luwrain.message("go");
	StoredNewsArticle articles[];
	try {
	    articles = newsStoring.loadNewsArticlesInGroupWithoutRead(groups[index]);
	    if (articles == null || articles.length < 1)
		articles = newsStoring.loadNewsArticlesInGroup(groups[index]);
	}
	catch (Exception e)
	{
	    e.printStackTrace();
	    Log.error("news", "could not get list of articles in group:" + groups[index].getName());
	    Luwrain.message(stringConstructor.errorReadingArticles());
	    summaryArea.show(null);
	    return;
	}
    summaryArea.show(articles);
    gotoArticles();
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, viewArea);
    }

    public void close()
    {
	Luwrain.closeApp(instance);
    }

    public void gotoGroups()
    {
	Luwrain.setActiveArea(instance, groupArea);
    }

    public void gotoArticles()
    {
	Luwrain.setActiveArea(instance, summaryArea);
    }

    public void gotoView()
    {
	Luwrain.setActiveArea(instance, viewArea);
    }
}
