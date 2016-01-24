/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.tests;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class TestingMultilinedEditModel extends DefaultMultilinedEditContent implements MultilinedEditModel
{
    public int hotPointX = 0;
    public int hotPointY = 0;

    @Override public int getHotPointX()
    {
	return hotPointX;
    }

    @Override public int getHotPointY()
    {
	return hotPointY;
    }

    @Override public void setHotPoint(int x, int y)
    {
	hotPointX = x;
	hotPointY = y;
    }

    @Override public String getTabSeq()
    {
	return "\t";
    }
}
