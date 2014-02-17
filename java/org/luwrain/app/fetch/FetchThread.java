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
import org.luwrain.network.*;

class FetchThread implements Runnable
{
    public boolean done = false;
    private StringConstructor stringConstructor;
    private Area messageArea;

    public FetchThread(StringConstructor stringConstructor, Area messageArea)
    {
	this.stringConstructor = stringConstructor;
	this.messageArea = messageArea;
    }

    private void fetchMail()
    {
	MailStoring mailStoring = Luwrain.getPimManager().getMailStoring();//FIXME:Must be with new connection since in separate thread;
	if (mailStoring == null)
	{
	    message(stringConstructor.noMailStoring());
	    return;
	}
	StoredMailAccount[] accounts;
	try {
	    accounts = mailStoring.loadMailAccounts();
	}
	catch(Exception e)
	{
	    message(stringConstructor.mailAccountsProblem());
	    Log.error("fetch", "the problem  while getting a list of mail accounts:" + e.getMessage());
	    e.printStackTrace();
	    return;
	}
	if (accounts == null || accounts.length < 1)
	{
	    message(stringConstructor.noMailAccounts());
	    return;
	}
	for(StoredMailAccount account: accounts)
	{
	    try {
		fetchMailFromAccount(mailStoring, account);
	    }
	    catch (Exception e)
	    {
		message(stringConstructor.mailErrorWithAccount(account.getName()));
		Log.error("fetch", "the problem while fetching mail from " + account.getName() + ":" + e.getMessage());
		e.printStackTrace();
	    }
	}
    }

    private void fetchMailFromAccount(MailStoring mailStoring, StoredMailAccount account) throws Exception
    {
	if (mailStoring == null || account == null)
	    return;
	StoredMailGroup mailGroup = mailStoring.loadGroupByUri("mailgrp:2");//FIXME:
	if (mailGroup == null)
	    return;
	message(stringConstructor.readingMailFromAccount(account.getName()));
	IncomingMailConsumer consumer = new IncomingMailConsumer(mailStoring, mailGroup);
	if (account.getProtocol() == MailAccount.POP3_SSL)
	{
	    PopSslFetch popSslFetch = new PopSslFetch(consumer);
	    popSslFetch.fetch(account.getHost(), account.getPort(), account.getLogin(), account.getPasswd());//FIXME:Password;;
	} else
	    Log.warning("fetch", "unknown protocol of incoming account" + account.getProtocol());
	message(stringConstructor.fetchedMailMessages(consumer.getCount()));
    }

    private void fetchNews()
    {
	NewsStoring newsStoring = Luwrain.getPimManager().getNewsStoring();//FIXME:In independent connection;
	if (newsStoring == null)
	{
	    Log.error("fetch", "No news storing object");
	    message(stringConstructor.noNewsGroupsData());
	    return;
	}
	StoredNewsGroup[] groups;
	try {
	    groups = newsStoring.loadNewsGroups();
	}
	catch (Exception e)
	{
	    message(stringConstructor.newsGroupsError());
	    Log.error("fetch", "the problem while getting list of news groups:" + e.getMessage());
	    e.printStackTrace();
	    return;
	}
	if (groups == null || groups.length < 1)
	{
	    message(stringConstructor.noNewsGroups());
	    return;
	}
	for(StoredNewsGroup g: groups)
	{
	    try {
		fetchNewsGroup(newsStoring, g);
	    }
	    catch (Exception e)
	    {
		message(stringConstructor.newsFetchingError(g.getName()));
		Log.error("fetch", "the problem while fetching and saving news in group \'" + g.getName() + "\':" + e.getMessage());
		e.printStackTrace();
	    }
	}
    }

    private void 		fetchNewsGroup(NewsStoring newsStoring, StoredNewsGroup group) throws Exception
    {
	Vector<NewsArticle> freshNews = new Vector<NewsArticle>();
	int totalCount = 0;
	String[] urls = group.getUrls();
	for (int k = 0;k < urls.length;k++)
	{
	    NewsArticle[] articles = FeedReader.readFeed(new URL(urls[k]));
	    totalCount += articles.length;
	    for(NewsArticle a: articles)
		if (newsStoring. countArticlesByUriInGroup(group, a.uri) == 0)
		    freshNews.add(a);
	}
	message(stringConstructor.newsGroupFetched(group.getName(), freshNews.size(), totalCount));
	for(int k = 0;k < freshNews.size();k++)
	    newsStoring.saveNewsArticle(group, freshNews.get(k));
    }

    public void run()
    {
	done = false;
	fetchMail();
	fetchNews();
	message(stringConstructor.fetchingCompleted());
	done = true;
    }

    private void message(String text)
    {
	if (text != null && !text.trim().isEmpty())
	    Luwrain.enqueueEvent(new MessageLineEvent(messageArea, text));
    }
}
