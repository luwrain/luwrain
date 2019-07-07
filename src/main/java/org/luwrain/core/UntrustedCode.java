/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

public class UntrustedCode
{
    private final Object syncObj = new Object();
    private volatile boolean done = false;
    private Object res = null;
    private final Runnable runnable;

    public UntrustedCode(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	this.runnable = runnable;
    }

    void run()
    {
	if (runnable != null)
	    runnable.run();
	    done = true;
	synchronized (syncObj) 
	{
	    syncObj.notifyAll();
	}
    }

    public final void waitUntilDone() throws InterruptedException
    {
	if (done)
	    return;
	synchronized (syncObj) {
	    while (!done)
		syncObj.wait();
	}
    }

    public Object getResult() throws InterruptedException
    {
	waitUntilDone();
	return res;
    }




}
