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

public class ViewArea extends SimpleArea
{
    private NewsReaderStringConstructor stringConstructor;
    private NewsReaderActions actions;

    public ViewArea(NewsReaderActions actions, NewsReaderStringConstructor stringConstructor)
    {
	super("FIXME:view");
	this.actions =  actions;
	this.stringConstructor = stringConstructor;
	setContent(prepareText());
    }

    public void onUserKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.TAB)
	{
	    actions.gotoGroups();
	    return;
	}
    }

    private String[] prepareText()
    {
	String res[] = new String[3];
	for(int i = 0;i < 3;i++)
	{
	    res[i] = new String("Text ");
	    res[i] += (i + 1);
	}
	return res;
    }
}
