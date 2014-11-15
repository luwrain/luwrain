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

package org.luwrain.core.sysapp;

import org.luwrain.core.*;
import org.luwrain.core.sysapp.mainmenu.MainMenu;

public class SystemApp implements Application
{
    private Luwrain luwrain;
    private StringConstructor stringConstructor;
    private org.luwrain.core.sysapp.mainmenu.Builder mainMenuBuilder;

    public SystemApp(Luwrain luwrain)
    {
	this.luwrain = luwrain;
	Object o = Langs.requestStringConstructor("system-application");
	stringConstructor = (StringConstructor)o;
	mainMenuBuilder = new org.luwrain.core.sysapp.mainmenu.Builder(stringConstructor);
    }

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	//Actually never called;
	return true;
    }

    @Override public AreaLayout getAreasToShow()
    {
	return null;
    }

    public MainMenu createMainMenu(String[] actionsList)
    {
	return new MainMenu(new DefaultControlEnvironment(luwrain), stringConstructor, mainMenuBuilder.buildItems(actionsList));
    }

    public StringConstructor stringConstructor()
    {
	return stringConstructor;
    }
}
