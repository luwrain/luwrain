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

package org.luwrain.tests;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class TestingNavigateArea extends NavigateArea
{
    public TestingNavigateArea(ControlEnvironment environment)
    {
	super(environment);
    }

    @Override public int getLineCount()
    {
	return 5;
    }

    @Override public String getLine(int index)
    {
	switch(index)
	{
	case 0:
	    return "1234567890";
	case 1:
	    return "qwertyuiop";
	case 2:
	    return "asdfghjkl";
case 3:
    return "zxcvbnm";
	case 4:
	    return "~!@#$%^&*()_+";
	default:
	    return "";
	}
    }

    @Override public String getName()
    {
	return "testing navigate area";
    }
}
