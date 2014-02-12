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

package org.luwrain.core;

import java.util.*;
import org.luwrain.core.events.*;

public class AppWrapperRegistry
{
    private Vector<AppWrapper> wrappers = new Vector<AppWrapper>();

    public void add(AppWrapper wrapper)
    {
	if (wrapper != null)
	    wrappers.add(wrapper);
    }

    public boolean launch(String name, String[] args)
    {
	if (name == null || name.trim().isEmpty())
	    return false;
	Iterator<AppWrapper> it = wrappers.iterator();
	while(it.hasNext())
	{
	    AppWrapper w = it.next();
	    if (!w.getName().equals(name))
		continue;
	    w.launch(args);
	    return true;
	}
	return false;
    }

    public void fillWithStandardWrappers()
    {
	//Preview;
	add(new AppWrapper() {
		public String getName()
		{
		    return "preview";
		}
		public void launch(String[] args)
		{
		    if (args == null || args.length < 1)
		    {
			Luwrain.launchApp(new org.luwrain.app.preview.PreviewApp());
			return;
		    }
		    for(int i = 0;i < args.length;i++)
			if (args[i] != null)
			    Luwrain.launchApp(new org.luwrain.app.preview.PreviewApp(args[i]));
		}
	    });

	//Notepad;
	add(new AppWrapper() {
		public String getName()
		{
		    return "notepad";
		}
		public void launch(String[] args)
		{
		    if (args == null || args.length < 1)
		    {
			Luwrain.launchApp(new org.luwrain.app.notepad.NotepadApp());
			return;
		    }
		    for(int i = 0;i < args.length;i++)
			if (args[i] != null)
			    Luwrain.launchApp(new org.luwrain.app.notepad.NotepadApp(args[i]));
		}
	    });

    }
}
