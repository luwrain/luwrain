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

public class DirItem
{
    public int state;
    public File file;//May be null, it means an empty item;

    public DirItem()
    {
    }

    public DirItem(File file)
    {
	state = 0;//FIXME:constant;
	this.file = file;
    }

    public String getName()
    {
	if (file == null)
	    return new String();
	return file.getName();
    }
}
