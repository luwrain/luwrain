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

import java.util.*;
import java.io.*;
import java.nio.file.*;
import java.nio.charset.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.util.RegistryAutoCheck;

class Base
{
    private Luwrain luwrain;
    private Strings strings;
    private UniRefList uniRefList;
    private Model model;
    private Appearance appearance;
    private final RegistryKeys registryKeys = new RegistryKeys();
    private String clickHereLine;

    boolean init(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	uniRefList = new UniRefList(luwrain);
	clickHereLine = strings.clickHereToCancelIntroduction(); 
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
	clickHereLine = strings.clickHereToCancelIntroduction(); 
	final RegistryAutoCheck check = new RegistryAutoCheck(luwrain.getRegistry());
	uniRefList.load();
	final String introductionFile = check.stringAny(registryKeys.desktopIntroductionFile(), "");
	if (!introductionFile.trim().isEmpty())
	{
	    final String[] introduction = new File(introductionFile).isAbsolute()?
	    readIntroduction(new File(introductionFile)):
	    readIntroduction(new File(luwrain.launchContext().dataDirAsFile(), introductionFile));
	    model.setIntroduction(introduction);
	    model.setClickHereLine(clickHereLine);
	}
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
	    luwrain.getRegistry().deleteValue(registryKeys.desktopIntroductionFile());
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

    static private String[] readIntroduction(File file)
    {
	NullCheck.notNull(file, "file");
	try {
	    final LinkedList<String> a = new LinkedList<String>();
	    final Path path = file.toPath();
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
