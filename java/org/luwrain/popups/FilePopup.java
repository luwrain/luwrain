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

//FIXME:ControlEnvironment interface support;

package org.luwrain.popups;

import java.io.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class FilePopup extends ListPopup
{
    private File file;

    public FilePopup(Object instance,
			   String name,
			   String prefix,
			   File file)
    {
	super(instance, new FileListPopupModel(), name, prefix, file.getAbsolutePath());
	this.file = file;
    }

    public FilePopup(Object instance,
			   String name,
			   String prefix,
		     File file,
		     boolean noMultipleCopies)
    {
	super(instance, new FileListPopupModel(), name, prefix, file.getAbsolutePath(), noMultipleCopies);
	this.file = file;
    }

    public File getFile()
    {
	return new File(getText());
    }
}
