/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

class WorkerThread implements Runnable
{
    private boolean running = false;
    private Worker worker;

    public WorkerThread(Worker worker)
    {
	this.worker = worker;
	if (worker == null)
	    throw new NullPointerException("worker may not be null");
    }

    public void run()
    {
	running = true;
	try {
	    worker.work();
	}
	catch (Throwable e)
	{
	    e.printStackTrace();
	}
	running = false;
    }

    public boolean isRunning()
    {
	return running;
    }
}
