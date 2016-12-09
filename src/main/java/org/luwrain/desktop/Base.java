/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.desktop;

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class Base
{
    private final Luwrain luwrain;
    private UniRefList uniRefList;
    final Model model;
    final Appearance appearance;
    private String clickHereLine = "#click here#";

    Base(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	this.uniRefList = new UniRefList(luwrain);
	this.model = new Model(luwrain, uniRefList);
	this.appearance = new Appearance(luwrain);
    }

    void load()
    {
	this.clickHereLine = luwrain.i18n().getStaticStr("DesktopClickHereToCancelIntroduction");
	uniRefList.load();
	final Settings.Desktop sett = Settings.createDesktop(luwrain.getRegistry());
	final String introductionFile = sett.getIntroductionFile("");
	if (!introductionFile.trim().isEmpty())
	{
	    final String[] introduction = new File(introductionFile).isAbsolute()?
	    readIntroduction(Paths.get(introductionFile)):
	    readIntroduction(luwrain.getPathProperty("luwrain.dir.data").resolve(introductionFile));
	    model.setIntroduction(introduction);
	    model.setClickHereLine(clickHereLine);
	}
	model.refresh();
    }

    boolean insert(int x, int y,
		   RegionContent data)
    {
	NullCheck.notNull(data, "data");
	if (data.isEmpty())
	    return false;
	uniRefList.add(y - model.getFirstUniRefPos(), data.strings());
	uniRefList.save();
	return true;
    }

    boolean delete(int x, int y)
    {
	if (y < model.getFirstUniRefPos())
	    return false;
	if (!uniRefList.delete(y - model.getFirstUniRefPos()))
	    return false;
	uniRefList.save();
	return true;
    }

    boolean onClick(int index, Object obj)
    {
	if (obj == null)
	    return false;
	if (obj.equals(clickHereLine))
	{
	    model.setIntroduction(null);
	    model.refresh();
	    final Settings.Desktop sett = Settings.createDesktop(luwrain.getRegistry());
	    sett.setIntroductionFile("");
	    return true;
	}
	if (obj instanceof UniRefInfo)
	{
	    final UniRefInfo uniRefInfo = (UniRefInfo)obj;
	    luwrain.openUniRef(uniRefInfo.value());
	    return true;
	}
	return false;
    }

    static private String[] readIntroduction(Path path)
    {
	NullCheck.notNull(path, "path");
	try {
	    final LinkedList<String> a = new LinkedList<String>();
	    try (Scanner scanner =  new Scanner(path, StandardCharsets.UTF_8.name()))
		{
		    while (scanner.hasNextLine())
			a.add(scanner.nextLine());
		}
	    return 	    a.toArray(new String[a.size()]);
	}
	catch (IOException e)
	{
	    e.printStackTrace();
	    return new String[0];
	}
    }
}
