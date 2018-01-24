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

import java.util.*;
import java.util.concurrent.*;

class WorkersTracking 
{
    static private final String LOG_COMPONENT = Core.LOG_COMPONENT;

        private final Map<String, Entry> workers = new HashMap();
    private volatile boolean continueWork = true;

    boolean runExplicitly(String workerName)
    {
	NullCheck.notEmpty(workerName, "workerName");
	if (!workers.containsKey(workerName))
	    return false;
	final Entry e = workers.get(workerName);
	synchronized (e) {
	    if (e.task != null && !task.isDone())
		return false;
	    e.task = new FutureTask(e.worker, null);
	    e.executor.execute(e.task);
	}
	return true;
    }

    void doWork(Worker[] workersList)
    {
	NullCheck.notNullItems(workersList, "workersList");
	workers.clear();
	for(Worker w: workersList)
	{
	    final String name = w.getExtObjName();
	    if (workers.containsKey(name))
	    {
		Log.warning(LOG_COMPONENT, "trying to register workers with the same name \'" + name + "\' twice, only the first one is accepted");
		continue;
	    }
	    workers.put(name, new Entry(name, w));
	}
	new Thread(()->{
		int counter = 0;
		while(continueWork)
		{
		    for(Map.Entry<String, Entry> entry: workers.entrySet())
		    {
			final Entry e = entry.getValue();
			synchronized(e) {
			    if (e.task != null && !e.task.isDone())
				continue;
			    final int delay = e.worker.getFirstLaunchDelay();
			    final int period =     e.worker.getLaunchPeriod();
			    if (delay < 0 || period <= 0)
				continue;
			    if (counter >= delay && (counter - delay) % period == 0)
			    {
				e.task = new FutureTask(e.worker, null);
				e.executor.execute(e.task);
			    }
			}
		    } //for(entries);
		    try {
			Thread.sleep(1000);
		    } catch (InterruptedException ie)
		    {
			Thread.currentThread().interrupt();
			return;
		    }
		    ++counter;
		}
	}, "luwrain.workers").start();
    }

    void finish()
    {
	continueWork = false;
    }

        static private class Entry 
    {
	final ExecutorService executor = Executors.newSingleThreadExecutor();
final String name;
final Worker worker;
	FutureTask task = null;

Entry(String name, Worker worker)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(worker, "worker");
	    this.name = name;
	    this.worker = worker;
	}
    }
}
