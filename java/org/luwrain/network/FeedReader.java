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

package org.luwrain.network;

import java.net.URL;
import java.util.*;
import java.io.*;
import com.sun.syndication.feed.synd.*;
import com.sun.syndication.io.*;
import org.luwrain.pim.NewsArticle;

public class FeedReader
{
    public static NewsArticle[] readFeed(URL url) throws IOException, FeedException
    {
	Vector<NewsArticle> articles = new Vector<NewsArticle>();
	XmlReader reader = null;
	try {
	    reader = new XmlReader(url);
	    SyndFeed feed = new SyndFeedInput().build(reader);
	    for (Iterator i = feed.getEntries().iterator(); i.hasNext();)
	    {
                SyndEntry entry = (SyndEntry) i.next();
		NewsArticle article = new NewsArticle();
		if (feed.getTitle() != null)
		    article.sourceTitle = feed.getTitle();
		if (entry.getTitle() != null)
		    article.title = entry.getTitle();
		if (entry.getUri() != null)
		    article.uri = entry.getUri();
		if (entry.getLink() != null)
		    article.url = entry.getLink();
		if (entry.getPublishedDate() != null)
		    article.publishedDate = entry.getPublishedDate();
		if (entry.getUpdatedDate() != null)
		    article.updatedDate = entry.getUpdatedDate();
		if (entry.getAuthor() != null)
		    article.author = entry.getAuthor();
		//FIXME:category;
		article.content = "";
		List contents = entry.getContents();
		if (contents.size() > 0)
		{
		    for(Object o: contents)
			if (o instanceof SyndContentImpl)
			{
			    SyndContentImpl content = (SyndContentImpl)o;
			    article.content += content.getValue();
			}
		} else
		{
		    SyndContent content = entry.getDescription();
		    article.content = content.getValue();
		}


		articles.add(article);
	    }
	}
	finally {
	    if (reader != null)
		reader.close();
	}
	NewsArticle res[] = new NewsArticle[articles.size()];
	Iterator<NewsArticle> it = articles.iterator();
	int k = 0;
	while (it.hasNext())
	    res[k++] = it.next();
	return res;
    }
}
