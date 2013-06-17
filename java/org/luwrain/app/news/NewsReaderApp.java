/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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
    private GroupArea groupArea;
    private SummaryArea summaryArea;
    private ViewArea viewArea;
    private NewsStoring newsStoring;
    private StoredNewsGroup[] newsGroups;

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("news-reader");
	if (o == null)
	    return false;
	stringConstructor = (NewsReaderStringConstructor)o;
	groupArea = new GroupArea(this, stringConstructor);
	summaryArea = new SummaryArea(this, stringConstructor);
	viewArea = new ViewArea(this, stringConstructor);
	newsStoring = PimManager.createNewsStoring();
	fillGroup();
	this.instance = instance;
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groupArea, summaryArea, viewArea);
    }

    public void openGroup(int index)
    {
	if (newsGroups == null || index >= newsGroups.length)
	    return;
	StoredNewsArticle articles[];
	try {
	    articles = newsStoring.loadNewsArticlesInGroupWithoutRead(newsGroups[index]);
	    if (articles == null || articles.length < 1)
		articles = newsStoring.loadNewsArticlesInGroup(newsGroups[index]);
	}
	catch (Exception e)
	{
	    //FIXME:Logging;
	    Dispatcher.message("FIXME:load error");
	    summaryArea.show(null);
	    return;
	}
    summaryArea.show(articles);
    gotoArticles();
    }

    public void gotoGroups()
    {
	Dispatcher.setActiveArea(instance, groupArea);
    }

    public void gotoArticles()
    {
	Dispatcher.setActiveArea(instance, summaryArea);
    }

    public void gotoView()
    {
	Dispatcher.setActiveArea(instance, viewArea);
    }

    private void 	fillGroup()
    {
	if (newsStoring == null)
	{
	    String content[] = new String[2];
	    content[0] = new String("FIXME:no connection");
	    content[1] = new String();
	    groupArea.setContent(content);
	    return;
	}
	try {
	    newsGroups = newsStoring.loadNewsGroups();
	    String content[] = new String[newsGroups.length + 1];
	    for(int i = 0;i < newsGroups.length;i++)
		content[i] = newsGroups[i].getName();
	    content[newsGroups.length] = new String();
	    groupArea.setContent(content);
	    return;
	}
	catch(Exception e)
	{
	    //FIXME:logging;
	    String content[] = new String[2];
	    content[0] = new String("FIXME:fetching error") + e.getMessage();
	    content[1] = new String();
	    groupArea.setContent(content);
	    return;
	}
    }

    public void closeNewsReader()
    {
	Dispatcher.closeApplication(instance);
    }
}
