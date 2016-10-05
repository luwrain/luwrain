/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

public class EmbeddedSingleLineEdit implements SingleLineEditModel
{
    protected final ControlEnvironment environment;
    protected final SingleLineEdit edit;
    protected final EmbeddedEditLines lines;
    protected final HotPointControl hotPointInfo;
    protected int posX;
    protected int posY;

    /**
     * @param environment The control environment for this edit
     * @param lines The object to provide and to accept edited text
     * @param hotPointInfo The object to provide and to set real hot point position in area (without any shift)
     * @param posX The X position of this edit in the area
     * @param posY The Y position of this edit in the area
     */
    public EmbeddedSingleLineEdit(ControlEnvironment environment, EmbeddedEditLines lines,
				  HotPointControl hotPointInfo, 
				  int posX, int posY)
    {
	this.environment = environment;
	this.lines = lines;
	this.hotPointInfo = hotPointInfo;
	this.posX = posX;
	this.posY = posY;
	edit = new SingleLineEdit(environment, this);
    }

    public SingleLineEdit getEditObj()
    {
	return edit;
    }

    public boolean isPosCovered(int x, int y)
    {
	return posY == y && x >= posX;
    }

    public void setNewPos(int x, int y)
    {
	posX = x;
	posY = y;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	return edit.onKeyboardEvent(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return edit.onEnvironmentEvent(event);
    }

    public boolean onAreaQuery(AreaQuery query)
    {
	return edit.onAreaQuery(query);
	}

    public String getLine()
    {
	return lines.getEmbeddedEditLine(posX, posY);
    }

    public void setLine(String text)
    {
	lines.setEmbeddedEditLine(posX, posY, text);
    }

    public int getHotPointX()
    {
	int value = hotPointInfo.getHotPointX();
	return value >= posX?value - posX:0;
    }

    public void setHotPointX(int value)
    {
	hotPointInfo.setHotPointX(value + posX);
    }

    public String getTabSeq()
    {
	return "\t";//FIXME:
    }
}
