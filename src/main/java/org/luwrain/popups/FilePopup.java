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

//FIXME:ControlEnvironment interface support;

package org.luwrain.popups;

import java.io.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class FilePopup extends EditListPopup
{
    public static final int ANY = 0;
    public static final int DIRECTORY = 1;

    private File file;

    public FilePopup(Luwrain luwrain,
			   String name,
			   String prefix,
			   File file)
    {
	super(luwrain, new FileListPopupModel(), name, prefix, FileListPopupModel.getPathWithTrailingSlash(file));
	this.file = file;
	if (file == null)
	    throw new NullPointerException("file may not be null");
    }

    public FilePopup(Luwrain luwrain,
			   String name,
			   String prefix,
		     File file,
		     int popupFlags)
    {
	super(luwrain, new FileListPopupModel(), name, prefix, FileListPopupModel.getPathWithTrailingSlash(file), popupFlags);
	this.file = file;
	if (file == null)
	    throw new NullPointerException("file may not be null");
    }

    public File getFile()
    {
	return new File(text());
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isCommand() && event.withShiftOnly())
	    switch(event.getCommand())
	    {
	    case 	    KeyboardEvent.ENTER:
		return openCommanderPopup();
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onOk()
    {
	return true;
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
	final File res = Popups.commanderSingle(luwrain, getAreaName() + ": ", file, CommanderPopup.ACCEPT_ALL, 0);
	if (res != null)
	    setText(res.getAbsolutePath(), "");
	return true;
    }
}
