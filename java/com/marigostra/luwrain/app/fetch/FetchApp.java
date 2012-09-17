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

package com.marigostra.luwrain.app.fetch;

import com.marigostra.luwrain.core.*;

public class FetchApp
{
    /*

    private void fetchArticles(StoredNewsGroup newsGroup)
    {
	String[] urls;
	try {
	    urls = newsStoring.loadNewsGroupSources(newsGroup);
	    if (urls == null)
		return;
	}
	catch (SQLException e)
	{
	    Speech.say(e.getMessage());//FIXME:
	    return;
	}
	for(int i = 0;i < urls.length;i++)
	{
	    NewsArticle articles[];
	    try {
		articles = FeedReader.readFeed(new java.net.URL(urls[i]));
	    }
	    catch (Exception e)
	    {
		Speech.say(e.getMessage());//FIXME:
		return;
	    }
	    int count = 0;
	    try {
		for(int k = 0;k < articles.length;k++)
		{
		    if (newsStoring.countArticlesByUriInGroup(newsGroup, articles[k].uri) > 0)
			continue;
		    newsStoring.saveNewsArticle(newsGroup, articles[k]);
		    count++;
		}
	    }
	    catch(SQLException e)
	    {
		Speech.say(e.getMessage());
		return;
	    }
	    Speech.say("Saved " + count + " articles");
	}
    }
    */
}
