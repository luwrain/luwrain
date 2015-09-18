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

public class EmbeddedMultilinedEdit implements MultilinedEditModel
{
    private ControlEnvironment environment;
    private MultilinedEditContent content;
    private HotPointInfo hotPointInfo;
    private int posX;
    private int posY;
    private MultilinedEdit edit;

    public EmbeddedMultilinedEdit(ControlEnvironment environment,
				  MultilinedEditContent content,
				  HotPointInfo hotPointInfo,
				  int posX,
				  int posY)
    {
	this.environment = environment;
	this.content = content;
	this.hotPointInfo = hotPointInfo;
	this.posX = posX;
	this.posY = posY;
	edit = new MultilinedEdit(environment, this);
    }

    public EmbeddedMultilinedEdit(ControlEnvironment environment,
				  MultilinedEditContent content,
				  HotPointInfo hotPointInfo,
				  int posY)
    {
	this.environment = environment;
	this.content = content;
	this.hotPointInfo = hotPointInfo;
	this.posX = 0;
	this.posY = posY;
	edit = new MultilinedEdit(environment, this);
    }

    public MultilinedEdit getEditObj()
    {
	return edit;
    }

    public boolean isPosCovered(int x, int y)
    {
	return y >= posY && x >= posX;
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

    @Override public String getLine(int index)
    {
	return content.getLine(index);
    }

    @Override public void setLine(int index, String text)
    {
	content.setLine(index, text);
    }

    @Override public int getLineCount()
    {
	return content.getLineCount();
    }

    @Override public void removeLine(int index)
    {
	content.removeLine(index);
    }

    @Override public void insertLine(int index, String text)
    {
	content.insertLine(index, text);
    }

    @Override public void addLine(String line)
    {
	content.addLine(line);
    }

    @Override public int getHotPointX()
    {
	int value = hotPointInfo.getHotPointX();
	return value >= posX?value - posX:0;
    }

    @Override public int getHotPointY()
    {
	int value = hotPointInfo.getHotPointY();
	return value >= posY?value - posY:0;
    }

    @Override public void setHotPoint(int x, int y)
    {
	hotPointInfo.setHotPointX(x + posX);
	hotPointInfo.setHotPointY(y + posY);
    }

    @Override public String getTabSeq()
    {
	return " ";
    }

    @Override public boolean beginEditTrans()
    {
	return content.beginEditTrans();
    }

    @Override public void endEditTrans()
    {
	content.endEditTrans();
    }
}
