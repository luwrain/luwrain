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

public class SystemApp implements Application
{
    private Object instance = null;
    private SystemAppStringConstructor stringConstructor = null;
    private MainMenuArea mainMenu = null;

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("system-application");
	if (o == null)
	    return false;
	stringConstructor = (SystemAppStringConstructor)o;
	mainMenu = new MainMenuArea(stringConstructor);
	this.instance = instance;
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(mainMenu);
    }
}
