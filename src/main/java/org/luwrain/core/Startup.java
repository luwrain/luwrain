/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

public final class Startup implements java.util.function.Consumer
{
    @Override public void accept(Object obj)
    {
	final Luwrain luwrain = (Luwrain)obj;
	greetingWorker(luwrain);
	luwrain.playSound(Sounds.STARTUP);
    }

    private void greetingWorker(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	final Settings.UserInterface sett = Settings.createUserInterface(luwrain.getRegistry());
	final String text = sett.getLaunchGreeting("");
	if (text.trim().isEmpty())
	    return;
	luwrain.registerExtObj(new Worker(){
		int count = 10;//TODO:to registry
		@Override public String getExtObjName()
		{
		    return "luwrain.greeting";
		}
		@Override public int getLaunchPeriod()
		{
		    return 30;
		}
		@Override public int getFirstLaunchDelay()
		{
		    return 15;
		}
		@Override public void run()
		{
		    if (!luwrain.getProperty("luwrain.startingmode").equals("1"))
			return;
		    if (count <= 0)
			return;
			luwrain.message(text, Luwrain.MessageType.ANNOUNCEMENT);
			--count;
		}
	    });
    }
}
