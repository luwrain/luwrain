/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

import org.luwrain.core.events.*;

public class EditArea extends SimpleArea
{
    private MultilinedEdit edit = null;
    private boolean modified = false;

    public EditArea()
    {
	createEdit();
    }

    public EditArea(String name)
    {
	super(name);
	createEdit();
    }

    public EditArea(String name, String[] content)
    {
	super(name, content);
	createEdit();
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (super.onKeyboardEvent(event))
	    return true;
	modified = false;
	if (edit.onKeyboardEvent(event))
	{
	    if (modified)
		onChange();
	    modified = false;
	    return true;
	}
	modified = false;
	return false;
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
	edit = new MultilinedEdit(new MultilinedEditModel(){
		private EditArea area = thisArea;
		public String getLine(int index)
		{
		    return area.getLine(index);
		}
		public void setLine(int index, String text)
		{
		    area.setLine(index, text);
		    area.modified = true;
		}
		public int getLineCount()
		{
		    return area.getLineCount();
		}
		public int getHotPointX()
		{
		    return area.getHotPointX();
		}
		public int getHotPointY()
		{
		    return area.getHotPointY();
		}
		public void setHotPoint(int x, int y)
		{
		    area.setHotPoint(x, y);
		}
		public void removeLine(int index)
		{
		    area.removeLine(index);
		    area.modified = true;
		}
    public void insertLine(int index, String text)
		{
		    area.insertLine(index, text);
		    area.modified = true;
		}
		public String getTabSeq()
		{
		    return area.getTabSeq();
		}
	    });
    }
}
