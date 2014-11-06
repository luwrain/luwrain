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

//FIXME:Notification about article being read

package org.luwrain.app.news;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.pim.*;

class SummaryArea implements Area, CopyCutRequest
{
    private static final int INITIAL_HOT_POINT_X = 2;

    private Luwrain luwrain;
    private StringConstructor stringConstructor;
    private Actions actions;
    private StoredNewsArticle articles[];
    private int hotPointX = 0;
    private int hotPointY = 0;
    private CopyCutInfo copyCutInfo;

    public SummaryArea(Luwrain luwrain,
		       Actions actions,
		       StringConstructor stringConstructor)
    {
	this.luwrain = luwrain;
	this.stringConstructor = stringConstructor;
	this.actions = actions;
	this.copyCutInfo = new CopyCutInfo(this);
    }

    public void show(StoredNewsArticle articles[])
    {
	if (articles == null || articles.length < 1)
	{
	    this.articles = null;
	    hotPointX = 0;
	    hotPointY = 0;
	    luwrain.onAreaNewContent(this);
	    luwrain.onAreaNewHotPoint(this);
	    return;
	}
	this.articles = articles;
	hotPointX = INITIAL_HOT_POINT_X;
	hotPointY = 0;
	luwrain.onAreaNewContent(this);
	luwrain.onAreaNewHotPoint(this);
    }

    public int getLineCount()
    {
	if (articles == null || articles.length < 1)
	    return 1;
	return articles.length + 1;
    }

    public String getLine(int index)
    {
	if (articles == null || articles.length < 1)
	    return index == 0?stringConstructor.noSummaryItems():"";
	return 	 index < articles.length?constructStringForScreen(articles[index]):"";
    }

    public int getHotPointX()
    {
	return hotPointX >= 0?hotPointX:0;
    }

    public int getHotPointY()
    {
	return hotPointY >= 0?hotPointY:0;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoView();
	    return true;
	}

	if (event.isCommand() && event.getCommand() == KeyboardEvent.BACKSPACE && !event.isModified())
	{
	    actions.gotoGroups();
	    return true;
	}


	if (!event.isCommand() && !event.isModified() &&
	    event.getCharacter() == ' ')
	    return onSpace(event);
	if (!event.isCommand())
	    return false;
	switch(event.getCommand())
	{
	case KeyboardEvent.ENTER:
	    return onEnter(event);
	case KeyboardEvent.ARROW_DOWN:
	    return onArrowDown(event);
	case KeyboardEvent.ARROW_UP:
	    return onArrowUp(event);
	case KeyboardEvent.ARROW_RIGHT:
	    return onArrowRight(event);
	case KeyboardEvent.ARROW_LEFT:
	    return onArrowLeft(event);
	    //	case KeyboardEvent.ALTERNATIVE_ARROW_DOWN:
	    //	case KeyboardEvent.ALTERNATIVE_ARROW_DOWN:
	    //FIXME:KeyboardEvent.INSERT:
	    //FIXME:KeyboardEvent.HOME:
	    //FIXME:KeyboardEvent.END:
	    //FIXME:KeyboardEvent.PAGE_DOWN:
	    //FIXME:KeyboardEvent.PAGE_DUP:
	default:
	    return false;
	}
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.CLOSE:
	    actions.close();
	    return true;
	case EnvironmentEvent.INTRODUCE:
	    Speech.say(stringConstructor.appName() + " " + stringConstructor.summaryAreaName());
	    return true;
	case EnvironmentEvent.COPY_CUT_POINT:
	    return copyCutInfo.doCopyCutPoint(hotPointX, hotPointY);
	case EnvironmentEvent.COPY:
	    return copyCutInfo.doCopy(hotPointX, hotPointY);
	    //FIXME:case EnvironmentEvent.REFRESH:
	default:
	    return false;
	}
    }

    public String getName()
    {
	return stringConstructor.summaryAreaName();
    }

    private boolean onEnter(KeyboardEvent event)
    {
	if (articles == null || articles.length < 1)
	{
	    Speech.say(stringConstructor.noSummaryItems(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= articles.length)
	    return false;
	StoredNewsArticle a = articles[hotPointY];
	if (a.getState() != NewsArticle.NEW)
	{
	    actions.showArticle(a);
	    return true;//FIXME:View anyway;
	}
	try {
	    a.setState(NewsArticle.READ);
	}
	catch (Exception e)
	{
	    Log.error("news", "problem during updating news article state:" + e.getMessage());
	    e.printStackTrace();
	    luwrain.message(stringConstructor.errorUpdatingArticleState());
	    return true;
	}
	luwrain.onAreaNewContent(this);
	actions.showArticle(a);
	return true;
    }

    private boolean onSpace(KeyboardEvent event)
    {
	if (articles == null || articles.length < 1)
	{
	    Speech.say(stringConstructor.noSummaryItems(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= articles.length)
	    return false;
	StoredNewsArticle a = articles[hotPointY];
	if (a.getState() != NewsArticle.NEW)
	    return true;//FIXME:View anyway;
	try {
	    a.setState(NewsArticle.READ);
	}
	catch (Exception e)
	{
	    Log.error("news", "problem during updating news article state:" + e.getMessage());
	    e.printStackTrace();
	    luwrain.message(stringConstructor.errorUpdatingArticleState());
	    return true;
	}
	luwrain.onAreaNewContent(this);
	return true;
    }


    private boolean onArrowDown(KeyboardEvent event)
    {
	if (articles == null || articles.length < 1)
	{
	    Speech.say(stringConstructor.noSummaryItems(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= articles.length)
	{
	    Speech.say(stringConstructor.noSummaryItemsBelow(), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY++;
	if (hotPointY >= articles.length)
	{
	    hotPointX = 0;
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    luwrain.onAreaNewHotPoint(this);
	    return true;
	}
	Speech.say(constructStringForSpeech(articles[hotPointY]));
	hotPointX = INITIAL_HOT_POINT_X;
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onArrowUp(KeyboardEvent event)
    {
	if (articles == null || articles.length < 1)
	{
	    Speech.say(stringConstructor.noSummaryItems(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY == 0)
	{
	    Speech.say(stringConstructor.noSummaryItemsAbove(), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY--;
	hotPointX = INITIAL_HOT_POINT_X;
	Speech.say(constructStringForSpeech(articles[hotPointY]));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAlternativeArrowDown(KeyboardEvent event)
    {
	if (articles == null || articles.length < 1)
	{
	    Speech.say(stringConstructor.noSummaryItems(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= articles.length)
	{
	    Speech.say(stringConstructor.noSummaryItemsBelow(), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY++;
	if (hotPointY >= articles.length)
	{
	    hotPointX = 0;
	    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
	    luwrain.onAreaNewHotPoint(this);
	    return true;
	}
	Speech.say(constructString(articles[hotPointY]));
	hotPointX = INITIAL_HOT_POINT_X;
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onAlternativeArrowUp(KeyboardEvent event)
    {
	if (articles == null || articles.length < 1)
	{
	    Speech.say(stringConstructor.noSummaryItems(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY == 0)
	{
	    Speech.say(stringConstructor.noSummaryItemsAbove(), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointY--;
	hotPointX = INITIAL_HOT_POINT_X;
	Speech.say(constructString(articles[hotPointY]));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onArrowRight(KeyboardEvent event)
    {
	if (articles == null || articles.length < 1)
	{
	    Speech.say(stringConstructor.noSummaryItems(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= articles.length)
	    return false;
	if (hotPointX < INITIAL_HOT_POINT_X)
	    hotPointX = INITIAL_HOT_POINT_X;
	final String line = constructString(articles[hotPointY]);
	if (hotPointX - INITIAL_HOT_POINT_X < line.length())
	    hotPointX++;
	if (hotPointX - INITIAL_HOT_POINT_X >= line.length())
	    Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH); else
	    Speech.sayLetter(line.charAt(hotPointX - INITIAL_HOT_POINT_X));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private boolean onArrowLeft(KeyboardEvent event)
    {
	if (articles == null || articles.length < 1)
	{
	    Speech.say(stringConstructor.noSummaryItems(), Speech.PITCH_HIGH);
	    return true;
	}
	if (hotPointY >= articles.length)
	    return false;
	if (hotPointX <= INITIAL_HOT_POINT_X)
	{
	    Speech.say(Langs.staticValue(Langs.BEGIN_OF_LINE), Speech.PITCH_HIGH);
	    return true;
	}
	hotPointX--;
	final String line = constructString(articles[hotPointY]);
	Speech.sayLetter(line.charAt(hotPointX - INITIAL_HOT_POINT_X));
	luwrain.onAreaNewHotPoint(this);
	return true;
    }

    private String constructString(StoredNewsArticle article)
    {
	if (article == null)
	    return "";
	return article.getTitle();
    }

    private String constructStringForScreen(StoredNewsArticle article)
    {
	if (article == null)
	    return "";
	String line = constructString(article);
	switch(article.getState())
	{
	case NewsArticle.NEW:
	    return " [" + line + "]";
	case NewsArticle.MARKED:
	    return "* " + line;
	default:
	    return "  " + line;
	}
    }

    private String constructStringForSpeech(StoredNewsArticle article)
    {
	if (article == null)
	    return "";
	String line = constructString(article);
	switch(article.getState())
	{
	case NewsArticle.READ:
	    return stringConstructor.readPrefix() + " " + line;
	case NewsArticle.MARKED:
	    return stringConstructor.markedPrefix() + " " + line;
	default:
	    return line;
	}
    }

    public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	if (articles == null || articles.length <  1)
	    return false;
	int fromPos = fromY < articles.length?fromY:articles.length;
	int toPos = toY < articles.length?toY:articles.length;
	if (fromPos >= toPos)
	    return false;
	Vector<String> res = new Vector<String>();
	for(int i = fromPos;i < toPos;++i)
	    res.add(constructStringForScreen(articles[i]));
	luwrain.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }

}
