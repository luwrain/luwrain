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

//FIXME:ControlEnvironment interface support;

package org.luwrain.popups;

import java.io.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class FilePopup extends ListPopup
{
    private File file;

    public FilePopup(Luwrain luwrain,
			   String name,
			   String prefix,
			   File file)
    {
	super(luwrain, new FileListPopupModel(), name, prefix, file.getAbsolutePath());
	this.file = file;
	if (file == null)
	    throw new NullPointerException("file may not be null");
    }

    public FilePopup(Luwrain luwrain,
			   String name,
			   String prefix,
		     File file,
		     boolean noMultipleCopies)
    {
	super(luwrain, new FileListPopupModel(), name, prefix, file.getAbsolutePath(), noMultipleCopies);
	this.file = file;
	if (file == null)
	    throw new NullPointerException("file may not be null");
    }

    public File getFile()
    {
	return new File(getText());
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	if (event.isCommand() &&
	    event.getCommand() == KeyboardEvent.ENTER &&
	    event.withControlOnly())
	    return openCommanderPopup();
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onOk()
    {
	luwrain.message("Облом", Luwrain.MESSAGE_ERROR);
	return false;
    }

    private boolean openCommanderPopup()
    {
	File file = getFile();
	if (file == null)
	    return false;
	if (!file.isDirectory())
	    file = file.getParentFile();
	if (file == null || !file.isDirectory())
	    return false;
	final File res = Popups.commanderSingle(luwrain, getName() + ": ", file, CommanderPopup.ACCEPT_ALL, 0);
	if (res != null)
	    setText(res.getAbsolutePath(), "");
	return true;
    }
}
