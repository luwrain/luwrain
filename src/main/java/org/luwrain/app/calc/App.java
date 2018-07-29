/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.calc;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class App implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private EditArea editArea = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	this.strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain, strings);
	createArea();
	return new InitResult();
    }

    private void createArea()
    {
	final EditArea.Params params = new EditArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.name = strings.appName();
	this.editArea = new EditArea(params){
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch (event.getCode())
		    {
		    case OK:
			{
			    final StringBuilder b = new StringBuilder();
			    for(String s: getLines())
				b.append(s + " ");
			    try {
				base.calculate(new String(b));
							    return true;
			    }
			    catch(Exception e)
			    {
				luwrain.message(e.getClass().getName() + ":" + e.getMessage());
				return true;
			    }


			}
		    case CLOSE:
			closeApp();
			return true;
		    }
		    return false;
		}
	    };
    }

        @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(editArea);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }
}
