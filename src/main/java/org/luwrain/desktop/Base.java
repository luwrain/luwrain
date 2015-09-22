/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.desktop;

import java.io.File;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class Base
{
    private Luwrain luwrain;
    private Strings strings;
    private UniRefList uniRefList;
    private Model model;
    private Appearance appearance;

    boolean init(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	uniRefList = new UniRefList(luwrain);
	return true;
    }

    Model getModel()
    {
	if (model != null)
	    return model;
	model = new Model(luwrain, uniRefList);
	return model;
    }

    Appearance getAppearance()
    {
	if (appearance != null)
	    return appearance;
	appearance = new Appearance(luwrain, strings);
return appearance;
    }

    void setReady(String lang)
    {
	uniRefList.load();
	model.readIntroduction(new File(luwrain.launchContext().dataDirAsFile(), "DESKTOP." + lang + ".txt").getAbsolutePath());
	model.refresh();
    }

    boolean insert(int x, int y,
		   HeldData data)
    {
	NullCheck.notNull(data, "data");
	if (data.strings == null)
	    return false;
	uniRefList.add(y - model.getFirstUniRefPos(), data.strings);
	uniRefList.save();
	return true;
    }
}
