/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

import org.luwrain.core.events.*;

public class EmbeddedSingleLineEdit implements SingleLineEditModel
{
    private EmbeddedEditLines lines;
    private HotPointInfo hotPointInfo;
    private int posX;
    private int posY;
    private SingleLineEdit edit;

    /**
     * @param lines The object to provide and to accept edited text
     * @param hotPointInfo The object to provide and to set real hot point position in area (without any shift)
     * @param posX The X position of this edit in the area
     * @param posY The Y position of this edit in the area
     */
    public EmbeddedSingleLineEdit(EmbeddedEditLines lines,
				  HotPointInfo hotPointInfo,
				  int posX,
				  int posY)
    {
	this.lines = lines;
	this.hotPointInfo = hotPointInfo;
	this.posX = posX;
	this.posY = posY;
	edit = new SingleLineEdit(this);
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
