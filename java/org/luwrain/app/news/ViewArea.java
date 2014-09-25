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

class ViewArea extends NavigateArea
{
    private StringConstructor stringConstructor;
    private Actions actions;

    private StoredNewsArticle article;
    private String[] text;

    public ViewArea(Actions actions, StringConstructor stringConstructor)
    {
	this.actions =  actions;
	this.stringConstructor = stringConstructor;
    }

    public void show(StoredNewsArticle article)
    {
	this.article = article;
	prepareText();
	setHotPoint(0, 0);
	Luwrain.onAreaNewContent(this);
	Luwrain.onAreaNewHotPoint(this);//Maybe needless;
    }

    public int getLineCount()
    {
	if (article == null)
	    return 1;
	if (text == null)
	    return 4;
	return text.length + 4;
    }

    public String getLine(int index)
    {
	if (article == null)
	    return "";
	if (text != null && index < text.length)
	    return text[index];
	int num = index - (text != null?text.length:0);
	switch(num)
	{
	case 1:
	    return article.getUrl();
	case 2:
	    return article.getPublishedDate().toString();
	default:
	    return "";
	}
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && !event.isModified() &&
						    event.getCommand() == KeyboardEvent.TAB)
	    {
		actions.gotoGroups();
		return true;
	    }

	if (event.isCommand() && !event.isModified() &&
						    event.getCommand() == KeyboardEvent.BACKSPACE)
	    {
		actions.gotoArticles();
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
	case EnvironmentEvent.INTRODUCE:
	    Speech.say(stringConstructor.appName() + " " + stringConstructor.viewAreaName());
	    return true;
	default:
	    return super.onEnvironmentEvent(event);
	}
    }

    public String getName()
    {
	return stringConstructor.viewAreaName(); 
    }

    private void prepareText()
    {
	if (article == null)
	{
	    text = null;
	    return;
	}
	NewsContentParser parser = new NewsContentParser();
	parser.parse(article.getContent());
	text = parser.getLines();
    }

}
