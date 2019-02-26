/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

final class CronWorker implements Worker
{
        private final Luwrain luwrain;
    private int count = 1;

    CronWorker(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public String getExtObjName()
    {
	return "luwrain.cron";
    }

    @Override public int getLaunchPeriod()
    {
	return 5;
    }

    @Override public int getFirstLaunchDelay()
    {
	return 10;
    }

    @Override public void run()
    {
	if ((count % 2) == 0)
	    luwrain.xRunHooks("luwrain.cron.sec10", new Object[0], Luwrain.HookStrategy.ALL);
	if ((count % 3) == 0)
	    luwrain.xRunHooks("luwrain.cron.sec15", new Object[0], Luwrain.HookStrategy.ALL);
	if ((count % 6) == 0)
	    luwrain.xRunHooks("luwrain.cron.sec30", new Object[0], Luwrain.HookStrategy.ALL);
	if ((count % 12) == 0)
	    luwrain.xRunHooks("luwrain.cron.min1", new Object[0], Luwrain.HookStrategy.ALL);
	++count;
    }
}
