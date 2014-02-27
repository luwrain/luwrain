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

package org.luwrain.langs.en;

import java.io.*;
import java.util.*;
import org.luwrain.core.Langs;
import org.luwrain.app.commander.DirItem;
import org.luwrain.app.commander.PanelArea;

class CommanderStringConstructor implements org.luwrain.app.commander.StringConstructor
{
    public String appName()
    {
	return "Files commander";
    }

    public String leftPanelName(String path)
    {
	return "Left panel " + path;
    }

    public String rightPanelName(String path)
    {
	return "Right panel " + path;
    }

    public String tasksAreaName()
    {
	return "List of tasks";
    }

    public String noItemsAbove()
    {
	return "No items above";
    }

    public String noItemsBelow()
    {
	return "No items below";
    }

    public String inaccessibleDirectoryContent()
    {
	return "Directory content is inaccessible";
    }

    public String rootDirectory()
    {
	return "The root directory";
    }

    public String dirItemIntroduction(DirItem item, boolean brief)
    {
	if (item == null)
	    return "";
	String text = item.getFileName();
	if (text.isEmpty())
	    return Langs.staticValue(Langs.EMPTY_LINE);
	if (text.equals(PanelArea.PARENT_DIR))
	    return "Parent directory";
	if (!brief)
	{
	    if (item.getType() == DirItem.DIRECTORY)
	    {
		if (item.isSelected())
		    text = "Selected directory" + text; else
		    text = "Directory " + text;
	    } else
		if (item.selected)
		    text = "Selected file" + text;
	}
	return text;
    }

    public String done()
    {
	return "Done";
    }

    public String failed()
    {
	return "Failed";
    }

    public String copying(File[] files)
    {
	if (files == null)
	    return "";
	if (files.length == 1)
	    return "Copying " + files[0];
	return "Copying " + files + " item(s)";
    }

    public String copyPopupName()
    {
	return "Copy files and directories";
    }

    public String copyPopupPrefix(File[] files)
    {
	if (files == null || files.length < 1)
	    return "";
	if (files.length == 1)
	    return "Copy \"" + files[0].getName() + "\" to:";
	return "Copy " + files.length + " элемента(ов) to:";
    }
}
