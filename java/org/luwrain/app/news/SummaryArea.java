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
import org.luwrain.pim.*;

public class SummaryArea implements Area
{
    private StringConstructor stringConstructor;
    private Actions actions;
    private StoredNewsArticle articles[];
    private int hotPointX = 0;
    private int hotPointY = 0;

    public SummaryArea(Actions actions, StringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
	this.actions = actions;
    }

    public void show(StoredNewsArticle articles[])
    {
	if (articles == null || articles.length < 1)
	{
	    this.articles = null;
	    hotPointX = 0;
	    hotPointY = 0;
	    Luwrain.onAreaNewContent(this);
	    Luwrain.onAreaNewHotPoint(this);
	    return;
	}
	this.articles = articles;
	hotPointX = 2;
	hotPointY = 0;
	Luwrain.onAreaNewContent(this);
	Luwrain.onAreaNewHotPoint(this);
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
	    return stringConstructor.noSummaryItems();
	if (index < articles.length)
	    return constructLineForScreen(articles[index]);
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

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	//FIXME:left right home end;
	//FIXME:marking articles with insert;

	//Tab;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB && !event.isModified())
	{
	    actions.gotoView();
	    return true;
	}

	if (!event.isCommand())
	    return false;
	if (articles == null || articles.length < 1)
	{
	    Speech.say(stringConstructor.noSummaryItems(), Speech.PITCH_HIGH);
	    return true;
	}
	final int cmd = event.getCommand();

	//Enter;
	if (cmd == KeyboardEvent.ENTER && !event.isModified())
	{
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
		//FIXME:Logging;
		Luwrain.message("FIXME:Error updating");
		return true;
	    }
	    Luwrain.onAreaNewContent(this);
	    return true;
	}

	//Arrow down;
	if (cmd == KeyboardEvent.ARROW_DOWN && !event.isModified())
	{
	    if (hotPointY >= articles.length)
	    {
		Speech.say(stringConstructor.noSummaryItemsBelow(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY++;
	    if (hotPointY == articles.length)
	    {
		hotPointX = 0;
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		Luwrain.onAreaNewHotPoint(this);
		return true;
	    }
	    Speech.say(constructLineForSpeech(articles[hotPointY]));
	    hotPointX = 2;
		Luwrain.onAreaNewHotPoint(this);
	    return true;
	}

	//Arrow up;
	if (cmd == KeyboardEvent.ARROW_UP && !event.isModified())
	{
	    if (hotPointY == 0)
	    {
		Speech.say(stringConstructor.noSummaryItemsAbove(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY--;
	    hotPointX = 2;
	    Speech.say(constructLineForSpeech(articles[hotPointY]));
	    Luwrain.onAreaNewHotPoint(this);
	    return true;
	}

	//Ctrl + arrow down;
	if (cmd == KeyboardEvent.ARROW_DOWN && event.withControl())//FIXME:only;
	{
	    if (hotPointY >= articles.length)
	    {
		Speech.say(stringConstructor.noSummaryItemsBelow(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY++;
	    if (hotPointY == articles.length)
	    {
		hotPointX = 0;
		Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		Luwrain.onAreaNewHotPoint(this);
		return true;
	    }
	    Speech.say(constructLine(articles[hotPointY]));
	    hotPointX = 2;
		Luwrain.onAreaNewHotPoint(this);
	    return true;
	}

	//Ctrl + arrow up;
	if (cmd == KeyboardEvent.ARROW_UP && event.withControl())//FIXME:only;
	{
	    if (hotPointY == 0)
	    {
		Speech.say(stringConstructor.noSummaryItemsAbove(), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointY--;
	    hotPointX = 2;
	    Speech.say(constructLine(articles[hotPointY]));
	    Luwrain.onAreaNewHotPoint(this);
	    return true;
	}

	//Arrow right;
	if (cmd == KeyboardEvent.ARROW_RIGHT && !event.isModified())
	{
	    if (hotPointY >= articles.length)
		return false;
	    if (hotPointX < 2)
		hotPointX = 2;
	    String line = constructLine(articles[hotPointY]);
	    if (hotPointX - 2 < line.length())
		hotPointX++;
	    if (hotPointX - 2 >= line.length())
		Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH); else
		Speech.sayLetter(line.charAt(hotPointX - 2));
	    Luwrain.onAreaNewHotPoint(this);
	    return true;
	}

	//Arrow left;
	if (cmd == KeyboardEvent.ARROW_LEFT && !event.isModified())
	{
	    if (hotPointY >= articles.length)
		return false;
	    if (hotPointX <= 2)
	    {
		Speech.say(Langs.staticValue(Langs.BEGIN_OF_LINE), Speech.PITCH_HIGH);
		return true;
	    }
	    hotPointX--;
	    String line = constructLine(articles[hotPointY]);
	    Speech.sayLetter(line.charAt(hotPointX - 2));
	    Luwrain.onAreaNewHotPoint(this);
	    return true;
	}



	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	//FIXME:refresh;
	switch(event.getCode())
	{
	case EnvironmentEvent.CLOSE:
	    actions.close();
	    return true;
	case EnvironmentEvent.INTRODUCE:
	    Speech.say(stringConstructor.appName() + " " + stringConstructor.summaryAreaName());
	    return true;
	default:
	    return false;
	}
    }

    public String getName()
    {
	return stringConstructor.summaryAreaName();
    }

private String constructLine(StoredNewsArticle article)
    {
	if (article == null)
	    return new String();
	return article.getTitle() + ": " + article.getSourceTitle();
    }

private String constructLineForScreen(StoredNewsArticle article)
    {
	String line = constructLine(article);
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

private String constructLineForSpeech(StoredNewsArticle article)
    {
	String line = constructLine(article);
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
}
