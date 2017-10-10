/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls;

import org.luwrain.core.*;

public class TestingSingleLineEditModel implements SingleLineEdit.Model
{
    public String text = "";
    public int hotPoint = 0;

    public TestingSingleLineEditModel()
    {
    }

    public TestingSingleLineEditModel(String text)
    {
	NullCheck.notNull(text, "text");
	this.text = text;
    }

    @Override public String getLine()
    {
	return text;
    }

    @Override public void setLine(String text)
    {
	NullCheck.notNull(text, "text");
	this.text = text;
    }

    @Override public int getHotPointX()
    {
	return hotPoint;
    }

    @Override public void setHotPointX(int value)
    {
	if (value < 0)
	    throw new IllegalArgumentException("value (" + value + ") may not be negative");
	this.hotPoint = value;
    }

    @Override public String getTabSeq()
    {
	return "\t";
    }
}
