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
import org.luwrain.core.*;

class TerminalThread implements Runnable
{
    private final static int STEP_DELAY = 100;

    public boolean finished = false;
    public boolean shouldContinue = true;
    private Terminal terminal;
    private Area area;

    public TerminalThread(Terminal terminal, Area area)
    {
	this.terminal = terminal;
	this.area = area;
	shouldContinue = true;
	finished = false;
    }

    public void run()
    {
	finished = false;
	shouldContinue = true;
	while(shouldContinue)
	{
	    if (!terminal.collectData())
		break;
	    //FIXME:	    System.sleep(STEP_DELAY);
	}
	finished = true;
    }
}
