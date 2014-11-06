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

package org.luwrain.app.preview;

import org.luwrain.core.*;

public class PreviewApp implements Application, PreviewActions
{
    private Luwrain luwrain;
    private StringConstructor stringConstructor;
    private PreviewArea area;
    private String arg;

    public PreviewApp()
    {
    }

    public PreviewApp(String arg)
    {
	this.arg = arg;
    }

    public boolean onLaunch(Luwrain luwrain)
    {
	Object o = Langs.requestStringConstructor("preview");
	if (o == null)
	    return false;
	stringConstructor = (StringConstructor)o;
	this.luwrain = luwrain;
	area = new PreviewArea(luwrain, stringConstructor, this);
	if (arg != null)
	    if (!handleToPreview(arg))
	    {
		luwrain.message(stringConstructor.errorOpeningFile());
		return false;
	    }
	return true;
    }

    private boolean handleToPreview(String fileName)
    {
	Filter f = new FilterPoi();
	try {
	f.open(fileName);
	}
	catch(Exception e)
	{
	    e.printStackTrace();
	    Log.error("preview", fileName + ":" + e.getMessage());
	    return false;
	}
	area.setFilter(f);
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }

    public void closePreview()
    {
	luwrain.closeApp();
    }
}
