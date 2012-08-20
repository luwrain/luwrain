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

package com.marigostra.luwrain.app;

import com.marigostra.luwrain.core.*;

interface MainMenuItem 
{
    String getText();
    boolean hasAction();
    void onAction();
}

class EmptyMainMenuItem implements MainMenuItem
{
    public String getText()
    {
	return new String();
    }

    public boolean hasAction()
    {
	return false;
    }

    public void onAction()
    {
    }
}

class TimeMainMenuItem implements MainMenuItem
{
    private SystemAppStringConstructor stringConstructor = null;

    public TimeMainMenuItem(SystemAppStringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
    }

    public String getText()
    {
	return stringConstructor.currentTime();
    }

    public boolean hasAction()
    {
	return false;
    }

    public void onAction()
    {
    }
}

class DayMainMenuItem implements MainMenuItem
{
    private SystemAppStringConstructor stringConstructor = null;

    public DayMainMenuItem(SystemAppStringConstructor stringConstructor)
    {
	this.stringConstructor = stringConstructor;
    }

    public String getText()
    {
	return stringConstructor.currentDay();
    }

    public boolean hasAction()
    {
	return false;
    }

    public void onAction()
    {
    }
}

class MainMenuArea extends SimpleArea
{
    private SystemAppStringConstructor stringConstructor;
    private MainMenuItem items[];

    public MainMenuArea(SystemAppStringConstructor stringConstructor)
    {
	super(stringConstructor.mainMenuTitle());
	this.stringConstructor = stringConstructor;
	items = new MainMenuItem[3];
	items[0] = new TimeMainMenuItem(stringConstructor);
	items[1] = new DayMainMenuItem(stringConstructor);
	items[2] = new EmptyMainMenuItem();
	setContent(prepareText());
    }

    public void onUserKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ENTER)
	    run(getHotPointY());
    }

    private String[] prepareText()
    {
	String res[] = new String[items.length];
	for(int i = 0;i < items.length;i++)
	    res[i] = new String(items[i].getText());
	return res;
    }

    private void run(int index)
    {
	Application app = new com.marigostra.luwrain.app.news.NewsReaderApp();
	Environment.dispatcher().launchApplication(app);
	/*FIXME:
	if (index >= items.length)
	    return;
	if (!items[index].hasAction())
	    return;
	Speech.say("Сейчас попробуем новости");
	*/
    }
}
