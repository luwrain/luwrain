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

package org.luwrain.app.system;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

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
	items = new MainMenuItem[9];
	items[0] = new TimeMainMenuItem(stringConstructor);
	items[1] = new DayMainMenuItem(stringConstructor);
	items[2] = new EmptyMainMenuItem();
	items[3] = new ActionMainMenuItem("commander", "Обзор файлов и папок");
	items[4] = new ActionMainMenuItem("mail", "Электронная почта");
	items[5] = new ActionMainMenuItem("news", "Новости");
	items[6] = new ActionMainMenuItem("notepad", "Блокнот");
	items[7] = new ActionMainMenuItem("message", "Сообщение");
	items[8] = new EmptyMainMenuItem();
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

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (super.onKeyboardEvent(event))
	    return true;

	//Enter;
	if (event.isCommand() && event.getCommand() == KeyboardEvent.ENTER && !event.isModified())
	{
	    run(getHotPointY());
	    return true;
	}
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CANCEL || event.getCode() == EnvironmentEvent.CLOSE)
	{
	    cancel();
	    return true;
	}
	return false;
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
