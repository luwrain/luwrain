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

//LWR_API 1.0

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

/**
 * {code Area} interface implementation with internal lines storing. This
 * area type has its own lines container based on 
 * {@link MutableLinesImpl} class. It is the minimal area implementation which
 * doesn't have any abstract methods. It is useful, if it is necessary to
 * have an area with some static content, available for changing through
 * the operations of {@link MutableLines} interface.
 */
public class SimpleArea extends NavigationArea implements MutableLines
{
    protected final ControlContext environment;
    protected String name = "";
    protected final MutableLinesImpl content = new MutableLinesImpl();
    protected boolean transOpened = false;

    public SimpleArea(ControlContext environment)
    {
	super(environment);
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
    }

    public SimpleArea(ControlContext environment, String name)
    {
	super(environment);
	this.environment = environment;
	this.name = name;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(name, "name");
    }

    public SimpleArea(ControlContext environment, String name,
		      String[] lines)
    {
	super(environment);
	this.environment = environment;
	this.name = name;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(name, "name");
	NullCheck.notNullItems(lines, "lines");
	content.setLines(lines);
    }

    @Override public void beginLinesTrans()
    {
	content.beginLinesTrans();
	transOpened = true;
    }

    @Override public void endLinesTrans()
    {
	transOpened = false;
	content.endLinesTrans();
	environment.onAreaNewContent(this);
    }

    @Override public int getLineCount()
    {
	final int value = content.getLineCount();
	return value > 0?value:1;
    }

    @Override public String getLine(int index)
    {
	if (index >= content.getLineCount())
	    return "";
	final String line = content.getLine(index);
	return line != null?line:"";
    }

    public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	content.setLines(lines);
	afterChange();
    }

    public String[] getLines()
    {
	return content.getLines();
    }

    public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	content.setLine(index, line);
	afterChange();
    }

    public void addLine(String line)
    {
	NullCheck.notNull(line, "line");
	content.addLine(line);
	afterChange();
    }

    //index is the position of newly inserted line
    public void insertLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	content.insertLine(index, line);
	afterChange();
    }

    public void removeLine(int index)
    {
	content.removeLine(index);
	afterChange();
    }

    @Override public void clear()
    {
	content.clear();
	afterChange();
    }

    @Override public LineMarks getLineMarks(int index)
    {
	return content.getLineMarks(index);
    }

    @Override public void setLineMarks(int index, LineMarks lineMarks)
    {
	NullCheck.notNull(lineMarks, "lineMarks");
	content.setLineMarks(index, lineMarks);
	afterChange();
    }

    @Override public String getAreaName()
    {
	return name;
    }

    public void setName(String name)
    {
	NullCheck.notNull(name, "name");
	this.name = name;
	environment.onAreaNewName(this);
    }

    public String getWholeText()
    {
	return content.getWholeText();
    }

    private void afterChange()
    {
	if (transOpened)
	    return;
	environment.onAreaNewContent(this);
    }
}
