/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

import java.io.*;

class OsCommand implements Command
{
    private String name = "";
    private String command = "";

    public OsCommand(String name, String command)
    {
	this.name = name;
	this.command = command;
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (command == null)
	    throw new NullPointerException("command may not be null");
	if (name.trim().isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
    }

    @Override public String getName()
    {
	return name;
    }

    @Override public void onCommand(Luwrain luwrain)
    {
	Process p;
	try {
		p = Runtime.getRuntime().exec(command);
		p.waitFor();
	    }
	    catch (IOException e)
	    {
		e.printStackTrace();
	    }
	    catch (InterruptedException e)
	    {
		e.printStackTrace();
	    }
	/*
	BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
	String line = "";
	while ((line = reader.readLine())!= null)
	{
	    sb.append(line + "\n");
	}
	*/
    }
}
