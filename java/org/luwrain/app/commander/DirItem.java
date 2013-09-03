/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.app.commander;

import java.io.*;
import org.luwrain.core.*;

public class DirItem
{
    public static final int REGULAR = 0;
    public static final int DIRECTORY = 1;

    public boolean selected = false;
    private int fileType = REGULAR;
    private File file;

    public DirItem(File file)
    {
	if (file == null)
	    return;
	this.file = file;
	selected = false;
	this.fileType = file.isDirectory()?DIRECTORY:REGULAR;
    }

    public int getType()
    {
	return fileType;
    }

    public File getFileObject()
    {
	return file;
    }

    public String getScreenTitle()
    {
	if (file == null)
	    return "";
	String value = file.getName();
	if (fileType == DIRECTORY)
	    value = "[" + value + "]";
	return selected?("*" + value):(" " + value);
    }

    public String getFileName()
    {
	if (file == null)
	    return "";
	return file.getName();
    }

}
