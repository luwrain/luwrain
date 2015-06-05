/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.desktop;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class App implements Application, Actions
{
    private Luwrain luwrain;
    private Strings strings;

    private Model model;
    private Appearance appearance;
    private ListArea area;

    @Override public boolean onLaunch(Luwrain luwrain)
    {
	/*
	Object o = luwrain.i18n().getStrings("luwrain.news");
	if (o == null || !(o instanceof Strings))
	    return false;
	strings = (Strings)o;
	this.luwrain = luwrain;
	o =  luwrain.getSharedObject("luwrain.pim.news");
	if (o == null || !(o instanceof NewsStoring))
	    return false;
	newsStoring = (NewsStoring)o;
	createModels();
	createAreas();
	return true;
	*/
	return false;
    }

    private void createModels()
    {
	model = new Model();
    }

    private void createArea()
    {
	final Actions a = this;
	final Strings s = strings;

	final ListClickHandler handler = new ListClickHandler(){
		private Actions actions = a;
		@Override public boolean onListClick(ListArea area,
						     int index,
						     Object item)
		{
//FIXME:
		    return false;
		}
	    };

	      area = new ListArea(new DefaultControlEnvironment(luwrain),
					model,
				  appearance,
					handler,
					"desktop") {
		      private Strings strings = s;
		      private Actions actions = a;
		      @Override public boolean onKeyboardEvent(KeyboardEvent event)
		      {
if (event == null)
throw new NullPointerException("event may not be null");
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
if (event == null)
throw new NullPointerException("event may not be null");
return super.onEnvironmentEvent(event);
		}
	    };
    }

    @Override public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }

    @Override public String getAppName()
    {
	return "desktop";//FIXME:
    }
}
