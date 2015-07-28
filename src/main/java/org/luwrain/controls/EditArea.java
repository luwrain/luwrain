/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

public class EditArea extends SimpleArea
{
    private ControlEnvironment environment;
    private MultilinedEdit edit;
    private boolean modified = false;

    public EditArea(ControlEnvironment environment)
    {
	super(environment);
	this.environment = environment;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	createEdit();
    }

    public EditArea(ControlEnvironment environment, String name)
    {
	super(environment, name);
	this.environment = environment;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	createEdit();
    }

    public EditArea(ControlEnvironment environment,
		    String name,
		    String[] content)
    {
	super(environment, name, content);
	this.environment = environment;
	if (environment == null)
	    throw new NullPointerException("environment may not be empty");
	createEdit();
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	modified = false;
	if (edit.onKeyboardEvent(event))
	{
	    if (modified)
		onChange();
	    return true;
	}
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	modified = false;
	if (edit.onEnvironmentEvent(event))
	{
	    if (modified)
		onChange();
	    return true;
	}
	return super.onEnvironmentEvent(event);
    }

    public void onChange()
    {
	//Nothing here;
    }

    public String getTabSeq()
    {
	return "\t";
    }

    private void createEdit()
    {
	final EditArea thisArea = this;
	edit = new MultilinedEdit(environment, new MultilinedEditModel(){
		private EditArea area = thisArea;
		@Override public String getLine(int index)
		{
		    return area.getLine(index);
		}
		@Override public void setLine(int index, String text)
		{
		    area.setLine(index, text);
		    area.modified = true;
		}
		@Override public int getLineCount()
		{
		    return area.getLineCount();
		}
		@Override public int getHotPointX()
		{
		    return area.getHotPointX();
		}
		@Override public int getHotPointY()
		{
		    return area.getHotPointY();
		}
		@Override public void setHotPoint(int x, int y)
		{
		    area.setHotPoint(x, y);
		}
		@Override public void removeLine(int index)
		{
		    area.removeLine(index);
		    area.modified = true;
		}
    @Override public void insertLine(int index, String text)
		{
		    area.insertLine(index, text);
		    area.modified = true;
		}
		@Override public void addLine(String text)
		{
		    area.addLine(text);
		    area.modified = true;
		}
		@Override public String getTabSeq()
		{
		    return area.getTabSeq();
		}
		@Override public boolean beginEditTrans()
		{
		    return true;
		}
		@Override public void endEditTrans()
		{
		}
	    });
    }
}
