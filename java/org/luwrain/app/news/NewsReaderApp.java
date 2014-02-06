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

import java.net.URL;
import java.sql.SQLException;
import org.luwrain.core.*;
import org.luwrain.network.FeedReader;
import org.luwrain.pim.NewsArticle;
import org.luwrain.pim.*;

public class NewsReaderApp implements Application, NewsReaderActions
{
    private Object instance = null;
    private NewsReaderStringConstructor stringConstructor = null;
    private GroupModel groupModel;
    private GroupArea groupArea;
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
	stringConstructor = (NewsReaderStringConstructor)o;
	groupModel = new GroupModel();
	groupArea = new GroupArea(this, stringConstructor, groupModel);
	summaryArea = new SummaryArea(this, stringConstructor);
	viewArea = new ViewArea(this, stringConstructor);
	newsStoring = Luwrain.getPimManager().getNewsStoring();
	//FIXME:if (newsStoring == null)
	fillGroups();
	this.instance = instance;
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, viewArea);
    }

    public void closeNewsReader()
    {
	Luwrain.closeApplication(instance);
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

    private boolean 	fillGroups()
    {
	if (newsStoring == null)
	{
	    Log.error("news", "No news storing object");
	    groups = null;
	    groupModel.setItems(new StoredNewsGroup[0]);
	    Luwrain.onAreaNewContent(groupArea);
	    Luwrain.onAreaNewHotPoint(groupArea);
	    return false;
	}
	try {
	    groups = newsStoring.loadNewsGroups();
	    groupModel.setItems(groups);
	    Luwrain.onAreaNewContent(groupArea);
	    Luwrain.onAreaNewHotPoint(groupArea);
	    return true;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    Log.error("news", "could not construct list of groups:" + e.getMessage());
	    groups = null;
	    groupModel.setItems(new StoredNewsGroup[0]);
	    Luwrain.onAreaNewContent(groupArea);
	    Luwrain.onAreaNewHotPoint(groupArea);
	    return false;
	}
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
}
