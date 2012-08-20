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

public class NewsReaderApp implements Application, NewsReaderActions
{
    private Object instance = null;
    private NewsReaderStringConstructor stringConstructor = null;
    private GroupArea groups;
    private ArticleArea articles;
    private ViewArea view;

    public boolean onLaunch(Object instance)
    {
	/*
	Object o = Langs.requestStringConstructor("system-application");
	if (o == null)
	    return false;
	stringConstructor = (SystemAppStringConstructor)o;
	*/
	groups = new GroupArea(this, stringConstructor);
	articles = new ArticleArea(this, stringConstructor);
	view = new ViewArea(this, stringConstructor);
	this.instance = instance;
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(AreaLayout.LEFT_TOP_BOTTOM, groups, articles, view);
    }

    public void gotoGroups()
    {
	Environment.dispatcher().setActiveArea(instance, groups);
    }

    public void gotoArticles()
    {
	Environment.dispatcher().setActiveArea(instance, articles);
    }

    public void gotoView()
    {
	Environment.dispatcher().setActiveArea(instance, view);
    }
}
