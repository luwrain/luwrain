/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

import org.luwrain.core.*;
import org.luwrain.core.events.*;

class NotepadArea extends EditArea
{
    private NotepadActions actions;
    private NotepadStringConstructor stringConstructor;

    public NotepadArea(NotepadActions actions, NotepadStringConstructor stringConstructor, String name)
    {
	super(name);
	this.actions = actions;
	this.stringConstructor = stringConstructor;
    }

    public void onChange()
    {

    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeNotepad();
	    return true;
	}
	return false;
    }
}
