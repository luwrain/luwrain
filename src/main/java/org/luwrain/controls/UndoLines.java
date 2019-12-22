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

package org.luwrain.controls;

import org.luwrain.core.*;

public class UndoLines implements MutableLines 
{

    @Override public int getLineCount()
    {
	return 0;
    }

    @Override public String getLine(int index)
    {
	return null;
    }
    
    @Override public void beginLinesTrans()
    {
    }
    
    @Override public void endLinesTrans()
    {
    }
    
    @Override public String[] getLines()
    {
	return null;
    }
    
    @Override public void setLines(String[] lines)
    {
    }
    
    @Override public void addLine(String line)
    {
    }
    
    @Override public void insertLine(int index, String line)
    {
    }
    
    @Override public void removeLine(int index)
    {
    }
    
    @Override public void setLine(int index, String line)
    {
    }
    
    @Override public void clear()
    {
    }

        @Override public LineMarks getLineMarks(int index)
    {
	return null;
    }
    
    @Override public void setLineMarks(int index, LineMarks lineMarks)
    {
    }

    
}
