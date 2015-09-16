/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

class WorkerManager
{
    class Entry 
    {
	public String name = "";
	public Worker worker;
	public WorkerThread workerThread;

	public Entry(String name,
		     Worker worker)
	{
	    this.name = name;
	    this.worker = worker;
	    if (name == null)
		throw new NullPointerException("name may not be null");
	    if (name.trim().isEmpty())
		throw new IllegalArgumentException("name may not be empty");
	    if (worker == null)
		throw new NullPointerException("worker may not be null");
	    this.workerThread = new WorkerThread(worker);
	}
    }

    private TreeMap<String, Entry> workers = new TreeMap<String, Entry>();

    public boolean add(Worker worker)
    {
	if (worker == null)
	    throw new NullPointerException("worker may not be null");
	final String name = worker.getWorkerName();
	if (name == null || name.trim().isEmpty())
	    return false;
	if (workers.containsKey(name))
	    return false;
	workers.put(name, new Entry(name, worker));
	return true;
    }
}
