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

package com.marigostra.luwrain.app.news;

import com.marigostra.luwrain.core.*;
import com.marigostra.luwrain.core.events.*;
import com.marigostra.luwrain.pim.*;

public class SummaryArea implements Area
{
    private NewsReaderStringConstructor stringConstructor;
    private NewsReaderActions actions;
    private StoredNewsArticle articles[];
    private int hotPointX = 0;
    private int hotPointY = 0;

    public SummaryArea(NewsReaderActions actions, NewsReaderStringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
	this.actions = actions;
    }

    public int getLineCount()
    {
	if (articles == null || articles.length < 1)
	    return 2;
	return articles.length + 1;
    }

    public String getLine(int index)
    {
	if (articles == null || articles.length < 1)
	{
	    if (index == 0)
		return "FIXME:no content";
	    return new String();
	}
	if (index < articles.length)
	    return constructArticleLineForEnvironment(articles[index]);
	return new String();
    }

    public int getHotPointX()
    {
	if (hotPointX < 0)//Actually never happens;
	    return 0;
	return hotPointX;
    }

    public int getHotPointY()
    {
	if (hotPointY < 0)//Actually never happens;
	    return 0;
	return hotPointY;
    }

    public void setHotPoint(int x, int y)
    {
	//FIXME:
    }

    public void onKeyboardEvent(KeyboardEvent event)
    {
	//FIXME:left right movement;
	//FIXME:marking articles with !;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB)
	{
	    actions.gotoView();
	    return;
	}
	if (!event.isCommand())
	    return;
	if (articles == null || articles.length < 1)
	{
	    Speech.say("FIXME:no content");
	    return;
	}
	final int cmd = event.getCommand();
	if (cmd == KeyboardEvent.ENTER)
	{
	    if (hotPointY >= articles.length)
		return;
	    StoredNewsArticle a = articles[hotPointY];
	    if (a.getState() != NewsArticle.NEW)
		return;//FIXME:View anyway;
	    try {
		a.setState(NewsArticle.READ);
	    }
	    catch (Exception e)
	    {
		//FIXME:Logging;
		Dispatcher.message("FIXME:Error updating");
		return;
	    }
	    Dispatcher.onAreaNewContent(this);
	    return;
	}
	if (cmd == KeyboardEvent.ARROW_DOWN)
	{
	    if (hotPointY >= articles.length)
	    {
		Speech.say("FIXME:No more lines");
		return;
	    }
	    hotPointY++;
	    if (hotPointY == articles.length)
	    {
		hotPointX = 0;
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE));
		return;
	    }
	    Speech.say(constructArticleLineForSpeech(articles[hotPointY]));
	    hotPointX = 2;
	    return;
	}
	if (cmd == KeyboardEvent.ARROW_UP)
	{
	    if (hotPointY == 0)
	    {
		Speech.say("FIXME:No line");
		return;
	    }
	    hotPointY--;
	    hotPointX = 2;
	    Speech.say(constructArticleLineForSpeech(articles[hotPointY]));
	    return;
	}
    }

    public void onEnvironmentEvent(EnvironmentEvent event)
    {
	//FIXME:
    }

    public void setArticles(StoredNewsArticle articles[])
    {
	if (articles == null || articles.length < 1)
	{
	    this.articles = null;
	    hotPointX = 0;
	    hotPointY = 0;
	    Dispatcher.onAreaNewContent(this);
	    Environment.onAreaNewHotPoint(this);
	    return;
	}
	this.articles = articles;
	hotPointX = 2;
	hotPointY = 0;
	Dispatcher.onAreaNewContent(this);
	Environment.onAreaNewHotPoint(this);
    }

    public String getName()
    {
	return stringConstructor.summaryAreaName();
    }

private String constructArticleLine(StoredNewsArticle article)
    {
	if (article == null)
	    return new String();
	return article.getTitle() + ": " + article.getSourceTitle();
    }

private String constructArticleLineForEnvironment(StoredNewsArticle article)
    {
	String line = constructArticleLine(article);
	switch(article.getState())
	{
	case NewsArticle.NEW:
	    return " [" + line + "]";
	case NewsArticle.MARKED:
	    return "! " + line;
	default:
	    return "  " + line;
	}
    }

private String constructArticleLineForSpeech(StoredNewsArticle article)
    {
	String line = constructArticleLine(article);
	switch(article.getState())
	{
	case NewsArticle.READ:
	    return stringConstructor.readPrefix() + line;
	case NewsArticle.MARKED:
	    return stringConstructor.markedPrefix() + line;
	default:
	    return line;
	}
    }
}
