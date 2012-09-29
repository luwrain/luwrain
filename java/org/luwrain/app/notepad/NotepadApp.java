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

public class NotepadApp implements Application, NotepadActions
{
    private NotepadStringConstructor stringConstructor;
    private Object instance;
    private NotepadArea notepadArea;

    public NotepadApp()
    {
    }

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("notepad");
	if (o == null)
	    return false;
	stringConstructor = (NotepadStringConstructor)o;
	notepadArea = new NotepadArea(this, stringConstructor, stringConstructor.newFileName());
	this.instance = instance;
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(notepadArea);
    }

    public void closeNotepad()
    {
	Dispatcher.closeApplication(instance);
    }
}
