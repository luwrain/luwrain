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

package org.luwrain.popups;

import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class FilePopup extends EditListPopup
{
    public interface Acceptance 
    {
	boolean pathAcceptable(Path path);
    }

    private File file;

    public FilePopup(Luwrain luwrain, String name,
		     String prefix, File file)
    {
	super(luwrain, new FileListPopupModel(Paths.get("/home/luwrain")), name, prefix, FileListPopupModel.getPathWithTrailingSlash(file.toPath()));
	this.file = file;
	NullCheck.notNull(file, "file");
    }

    public FilePopup(Luwrain luwrain, String name,
		     String prefix, File file,
		     int popupFlags)
    {
	super(luwrain, new FileListPopupModel(Paths.get("/home/luwrain")), name, prefix, FileListPopupModel.getPathWithTrailingSlash(file.toPath()), popupFlags);
	this.file = file;
	NullCheck.notNull(file, "file");
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
	final Path res = Popups.commanderSingle(luwrain, getAreaName() + ": ", file.toPath(), CommanderPopup.ACCEPT_ALL, 0);
	if (res != null)
	    setText(res.toAbsolutePath().toString(), "");
	return true;
    }
}
