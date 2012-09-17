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
import com.marigostra.luwrain.core.events.*;

interface MainMenuItem 
{
    String getText();
    boolean hasAction();
    String getAction();
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

    public String getAction()
    {
	return "";
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

    public String getAction()
    {
	return "";
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

    public String getAction()
    {
	return "";
    }
}

class ActionMainMenuItem implements MainMenuItem
{
    private String action = null;
    private String title = null;

    public ActionMainMenuItem(String action, String title)
    {
	this.action = action;
	this.title = title;
    }

    public String getText()
    {
	return title;
    }

    public boolean hasAction()
    {
	return true;
    }

    public String getAction()
    {
	return action;
    }
}

public class MainMenuArea extends SimpleArea implements EventLoopStopCondition
{
    private boolean shouldContinue = true; 
    private SystemAppStringConstructor stringConstructor;
    private MainMenuItem items[];
    private String selectedAction = "";
    private boolean cancelled = true;

    public MainMenuArea(SystemAppStringConstructor stringConstructor)
    {
	super(stringConstructor.mainMenuTitle());
	this.stringConstructor = stringConstructor;
	items = new MainMenuItem[8];
	items[0] = new TimeMainMenuItem(stringConstructor);
	items[1] = new DayMainMenuItem(stringConstructor);
	items[2] = new EmptyMainMenuItem();
	items[3] = new ActionMainMenuItem("commander", "Обзор файлов и папок");
	items[4] = new ActionMainMenuItem("mail", "Электронная почта");
	items[5] = new ActionMainMenuItem("news", "Новости");
	items[6] = new ActionMainMenuItem("notepad", "Блокнот");
	items[7] = new EmptyMainMenuItem();
	setContent(prepareText());
    }

    public boolean continueEventLoop()
    {
	return shouldContinue;
    }

    public void cancel()
    {
	cancelled = true;
	shouldContinue = false;
    }

    public String getSelectedAction()
    {
	return selectedAction;
    }

    public boolean wasCancelled()
    {
	return cancelled;
    }

    public void onUserKeyboardEvent(KeyboardEvent event)
    {
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ENTER)
	    run(getHotPointY());
    }

    public void onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CANCEL || event.getCode() == EnvironmentEvent.CLOSE)
	{
	    cancel();
	    return;
	}
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
	if (items == null)
	    return;
	if (index >= items.length)
	    return;
	if (!items[index].hasAction())
	    return;
	selectedAction = items[index].getAction();
	cancelled = false;
	shouldContinue = false;
    }
}
