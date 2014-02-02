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

package org.luwrain.app.notepad;

import java.io.File;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

class NotepadArea extends EditArea
{
    private NotepadActions actions;
    private NotepadStringConstructor stringConstructor;
    private String fileName;

    public NotepadArea(NotepadActions actions,
		       NotepadStringConstructor stringConstructor,
		       String fileName)
    {
	super(fileName);
	this.actions = actions;
	this.stringConstructor = stringConstructor;
	this.fileName = fileName;
    }

    public String getFileName()
    {
	return fileName != null?fileName:"";
    }

    public void setFileName(String fileName)
    {
	this.fileName = fileName;
	Luwrain.onAreaNewName(this);
    }

    public void onChange()
    {
	//FIXME:
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.CLOSE:
	    actions.closeNotepad();
	    return true;
	case EnvironmentEvent.INTRODUCE:
	    if (fileName != null && !fileName.trim().isEmpty())
	    {
		File f = new File(fileName);
		Speech.say(stringConstructor.introduction() + " " + f.getName()); 
	    } else
		Speech.say(stringConstructor.appName());
	    return true;
	case EnvironmentEvent.SAVE:
	    actions.save();
	    return true;
	default:
	    return false;
	}
    }

	public String getName()
	{
	    if (fileName == null || fileName.trim().isEmpty())
		return stringConstructor.appName();
	    File f = new File(fileName);
	    return f.getName();
	}
}
