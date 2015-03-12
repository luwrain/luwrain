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

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class SimpleArea extends NavigateArea implements MutableLines
{
    private ControlEnvironment environment;
    private String name = "";
    private DefaultMultilinedEditContent content = new DefaultMultilinedEditContent();

    public SimpleArea(ControlEnvironment environment)
    {
	super(environment);
	this.environment = environment;
    }

    public SimpleArea(ControlEnvironment environment, String name)
    {
	super(environment);
	this.environment = environment;
	this.name = name != null?name:"";
    }

    public SimpleArea(ControlEnvironment environment,
		      String name,
		      String[] lines)
    {
	super(environment);
	this.environment = environment;
	this.name = name != null?name:"";
	content.setLines(lines);
    }

    @Override public int getLineCount()
    {
	final int value = content.getLineCount();
	return value > 0?value:1;
    }

    @Override public String getLine(int index)
    {
	final String line = content.getLine(index);
	return line != null?line:"";
    }

    public void setContent(String[] lines)
    {
	content.setLines(lines != null?lines:new String[0]);
	environment.onAreaNewContent(this);
	//	fixHotPoint();
    }

    public String[] getContent()
    {
	return content.getLines();
    }

    public void setLine(int index, String line)
    {
	content.setLine(index, line);
	environment.onAreaNewContent(this);
	//	fixHotPoint();
    }

    public void addLine(String line)
    {
	content.addLine(line);
	    environment.onAreaNewContent(this);
	    //	    fixHotPoint();
    }

    //index is the position of newly inserted line
    public void insertLine(int index, String line)
    {
	content.insertLine(index, line);
	environment.onAreaNewContent(this);
	//	fixHotPoint();
    }

    public void removeLine(int index)
    {
	content.removeLine(index);
	environment.onAreaNewContent(this);
	//	fixHotPoint();
    }

    public void clear()
    {
	content.clear();
	environment.onAreaNewContent(this);
	//	fixHotPoint();
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return super.onEnvironmentEvent(event);
    }

    @Override public String getName()
    {
	return name != null?name:"";
    }

public void setName(String name)
{
    this.name = name != null?name:"";
    environment.onAreaNewName(this);
}
}
