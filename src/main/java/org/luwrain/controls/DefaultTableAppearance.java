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

public class DefaultTableAppearance implements TableAppearance
{
    private ControlEnvironment environment = null;

    public DefaultTableAppearance(ControlEnvironment environment)
    {
	this.environment = environment;
    }

    public void introduceRow(TableModel model,
			     int index,
			     int flags)
    {
	if (model == null)
	    return;
	String value = "";
	for(int i = 0;i < model.getColCount();++i)
	{
	    String text = getCellText(model, i, index);
	    value += (text != null?text:"");
	}
	if (!value.trim().isEmpty())
	    environment.say(value); else
	    environment.hint(Hints.EMPTY_LINE);
    }

    public int getInitialHotPointX(TableModel model)
    {
	return 0;
    }

    public String getCellText(TableModel model, int col, int row)
    {
	if (model == null)
	    return "";
	Object cell = model.getCell(col, row);
	if (cell == null)
	    return null;
	String text = cell.toString();
	return text != null?text:"";
    }

    public String getRowPrefix(TableModel model, int index)
    {
	return "";
    }

    public int getColWidth(TableModel model, int  colIndex)
    {
	if (model == null)
	    return 0;
	int maxLen = 0;
	for(int i = 0;i < model.getRowCount();++i)
	{
	    final int len = getCellText(model, colIndex, i).length();
	    if (len > maxLen)
		maxLen = len;
	}
	return maxLen;
    }
}
