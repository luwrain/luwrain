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

package org.luwrain.app.fetch;

import java.net.*;
import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.pim.*;
import org.luwrain.network.FeedReader;

public class FetchThread implements Runnable
{
    public boolean done = false;
    private FetchStringConstructor stringConstructor;
    private Area messageArea;

    public FetchThread(FetchStringConstructor stringConstructor, Area messageArea)
    {
	this.stringConstructor = stringConstructor;
	this.messageArea = messageArea;
    }

    public void run()
    {
	done = false;
	fetchNews();
	done = true;
    }

    private void fetchNews()
    {
	try {
	    NewsStoring newsStoring = PimManager.createNewsStoring();//FIXME:In independent connection;
	    if (newsStoring == null)
	    {
		Log.error("fetch", "No news storing object");
		message(stringConstructor.noNewsGroupsData());
		return;
	    }
	    StoredNewsGroup[] groups = newsStoring.loadNewsGroups();
	    for(int i = 0;i < groups.length;i++)
	    {
		Vector<NewsArticle> freshNews = new Vector<NewsArticle>();
		int totalCount = 0;
		String[] urls = groups[i].getUrls();
		for (int k = 0;k < urls.length;k++)
		{
		    NewsArticle[] articles = FeedReader.readFeed(new URL(urls[k]));
		    totalCount += articles.length;
		    for(int z = 0;z < articles.length;z++)
			if (newsStoring. countArticlesByUriInGroup(groups[i], articles[z].uri) == 0)
			    freshNews.add(articles[z]);
		}
		message(stringConstructor.newsGroupFetched(groups[i].getName(), freshNews.size(), totalCount));
		for(int k = 0;k < freshNews.size();k++)
		    newsStoring.saveNewsArticle(groups[i], freshNews.get(k));
	    }
	    message(stringConstructor.newsFetchingCompleted());
	    return;
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    Log.error("fetch", "could not fetch news articles:" + e.getMessage());
	    message(stringConstructor.newsFetchingError());
	}
    }

    /*
    private void fetchMail()
    {
	MailStoring mailStoring = PimManager.createMailStoring();
	if (mailStoring == null)
	{
	    addLine("FIXME:No database connection");
	    return;
	}
	final SimpleArea a = this;
	Properties p = new Properties();
	Session session = Session.getInstance(p, null);
	MailFetching mailFetching = new MailFetching(session, new FetchProgressListener(){
		SimpleArea thisArea = a;
		public void onProgressLine(String line)
		{
		    thisArea.addLine(line);
		    Speech.say(line);
		}
	    }, stringConstructor, false, mailStoring);
	try {
	    StoredMailAccount accounts[] = mailStoring.loadMailAccounts();
	    for(int i = 0;i < accounts.length;i++)
	    {
		if (!accounts[i].getProtocol().equals("pop3"))
		    continue;
		addLine(stringConstructor.readingMailFromAccount(accounts[i].getName()));
		mailFetching.fetchPop3(new URLName("pop3", accounts[i].getHost(), accounts[i].getPort(), accounts[i].getFile(), accounts[i].getLogin(), accounts[i].getPasswd()));
	    }
	}
	catch(Exception e)
	{
	    addLine("Error: " + e.getMessage());
	    e.printStackTrace();
	}
    }
    */

    private void message(String text)
    {
	if (text != null && !text.trim().isEmpty())
	    Dispatcher.enqueueEvent(new MessageLineEvent(messageArea, text));
    }
}
