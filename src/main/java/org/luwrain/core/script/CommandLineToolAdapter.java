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

package org.luwrain.core.script;

import java.io.*;
import java.util.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

class CommandLineToolAdapter implements CommandLineTool
{
    private final ScriptObjectMirror outputFunc;

    CommandLineToolAdapter(ScriptObjectMirror outputFunc)
    {
	NullCheck.notNull(outputFunc, "outputFunc");
	this.outputFunc = outputFunc;
    }

    
    @Override public Instance launch(Listener listener, String[] args)
    {
	//FIXME:
	return null;
    }

    @Override public Set<Flags> getToolFlags()
    {
	//FIXME:
	return null;
    }

    @Override public String getExtObjName()
    {
	//FIXME:
	return "";
    }

    private class Instance implements CommandLineTool.Instance
    {
	private final Listener listener;
	private final Process proc;
	private final Thread thread;
	private boolean finished = false;
	private int exitCode = 0;

	Instance(Listener listener, Process proc)
	{
	    NullCheck.notNull(listener, "listener");
	    NullCheck.notNull(proc, "proc");
	    this.listener = listener;
	    this.proc = proc;
	    this.thread = new Thread(()->readOutput());
	    this.thread.start();
	}

	@Override public void stop()
	{
	}

	@Override public String getInstanceName()
	{
	    return null;
	}

	@Override public Status getStatus()
	{
	    return null;
	}

	@Override public int getExitCode()
	{
	    return exitCode;
	}

	@Override public boolean isFinishedSuccessfully()
	{
	    return finished;
	}

	@Override public String getSingleLineState()
	{
	    return null;
	}

	@Override public String[] getMultilineState()
	{
	    return null;
	}

	@Override public String[] getNativeState()
	{
	    return null;
	}

	private void readOutput()
	{
	    try {
		final InputStream is = proc.getInputStream();
		final BufferedReader r = new BufferedReader(new InputStreamReader(is));
		try {
		    String line = null;
		    synchronized(this) {
			line = r.readLine();
		    }
		    while (line != null)
		    {
			onLine(line);
			synchronized(this) {
			    line = r.readLine();
			}
		    }
		    synchronized(this) {
			proc.waitFor();
			this.finished = true;
			this.exitCode = proc.exitValue();
			//FIXME:notification
		    }
		}
		finally {
		    r.close();
		    is.close();
		}
	    }
	    catch(InterruptedException e)
	    {
		Thread.currentThread().interrupt();
	    }
	    catch(IOException e)
	    {
		//FIXME:
	    }
	}

	private void onLine(String line)
	{
	}



	
    }
}
