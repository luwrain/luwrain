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

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EditArea extends SimpleArea
{
    private ControlEnvironment environment;
    private MultilineEdit edit;
    private boolean modified = false;

    public EditArea(ControlEnvironment environment)
    {
	super(environment);
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
    }

    public EditArea(ControlEnvironment environment, String name)
    {
	super(environment, name);
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
	createEdit();
    }

    public EditArea(ControlEnvironment environment, String name,
		    String[] content)
    {
	super(environment, name, content);
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
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
	NullCheck.notNull(event, "event");
	modified = false;
	if (edit.onEnvironmentEvent(event))
	{
	    if (modified)
		onChange();
	    return true;
	}
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (edit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

    public void onChange()
    {
	//Nothing here;
    }

    protected String getTabSeq()
    {
	return "\t";
    }

    private void createEdit()
    {
	final EditArea thisArea = this;
	edit = new MultilineEdit(environment, new MultilineEditModelsTranslator(createModel()));
    }

    private MultilineEditLowLevelModel createModel()
    {
	final EditArea area = this;
	return new MultilineEditLowLevelModel(){
	    final MutableLines lines = area.getEditContent();
	    @Override public String getLine(int index)
	    {
		return lines.getLine(index);
	    }
	    @Override public void setLine(int index, String text)
	    {
		lines.setLine(index, text);
		area.modified = true;
	    }
	    @Override public int getLineCount()
	    {
		return lines.getLineCount();
	    }
	    @Override public int getHotPointX()
	    {
		return 0;
	    }
	    @Override public int getHotPointY()
	    {
		return 0;
	    }
	    @Override public void setHotPointX(int value)
	    {
	    }
	    @Override public void setHotPointY(int y)
	    {
	    }
	    @Override public void removeLine(int index)
	    {
		lines.removeLine(index);
		area.modified = true;
	    }
	    @Override public void insertLine(int index, String text)
	    {
		lines.insertLine(index, text);
		area.modified = true;
	    }
	    @Override public void addLine(String text)
	    {
		lines.addLine(text);
		area.modified = true;
	    }
	    @Override public String getTabSeq()
	    {
		return area.getTabSeq();
	    }
	    @Override public void beginEditTrans()
	    {
		//		    hotPointX = area.getHotPointX();
		//		    hotPointY = area.getHotPointY();
		//		    return true;
	    }
	    @Override public void endEditTrans()
	    {
	    }
	};
    }
}
