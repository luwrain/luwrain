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

package org.luwrain.app.term;

import org.luwrain.os.Terminal;
import org.luwrain.os.TerminalException;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.registry.Registry;

public class TerminalApp implements Application, Actions
{
    private final static String SHELL_COMMAND = "/bin/sh";//FIXME:System dependent, it is better to read it from the registry;

    private StringConstructor stringConstructor;
    private Object instance;
    private NavigateArea area;
    private Terminal terminal = new Terminal();
    private TerminalThread terminalThread;

    public TerminalApp()
    {
    }

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("term");
	if (o == null || !(o instanceof StringConstructor))
	    return false;
	stringConstructor = (StringConstructor)o;
	createArea();
	openTerminal();
	return true;
    }

    private void openTerminal()
    {
	try {
	    terminal.open(SHELL_COMMAND);
	}
	catch(TerminalException e)
	{
	    //FIXME:
	    e.printStackTrace();
	    return;//FIXME:
	}

	terminalThread = new TerminalThread(terminal, area);
	Thread t = new Thread(terminalThread);
	t.start();



    }

    private void closeTerminal()
    {
    }

    private void createArea()
    {
	final Actions a = this;
	final Terminal t = terminal;
	final StringConstructor s = stringConstructor;
	area = new NavigateArea(){
		private Actions actions = a;
		private StringConstructor stringConstructor = s;
		private Terminal terminal = t;
		@Override public int getLineCount()
		{
		    return terminal.getLineCount() >= 1?terminal.getLineCount():1;
		}
		@Override public String getLine(int index)
		{
		    if (index >= terminal.getLineCount())
			return "";
		    final String line = terminal.getLine(index);
		    return line != null?line:"";
		}
		@Override public boolean onKeyboardEvent(KeyboardEvent event)
		{
		    //FIXME:
		    return super.onKeyboardEvent(event);
		}
		@Override public boolean onEnvironmentEvent(EnvironmentEvent event)
		{
		    switch(event.getCode())
		    {
		    case EnvironmentEvent.CLOSE:
			actions.close();
			return true;
		    case EnvironmentEvent.INTRODUCE:
			Speech.say(stringConstructor.introduction()); 
			return true;
		    default:
			return super.onEnvironmentEvent(event);
		    }
		}
		@Override public String getName()
		{
		    return stringConstructor.areaName();
		}
	    };
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(area);
    }

    public void close()
    {
	closeTerminal();
	Luwrain.closeApp(instance);
    }
}
