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

package org.luwrain.app.mail;

import org.luwrain.core.*;
import org.luwrain.controls.*;

class SummaryTableAppearance implements TableAppearance
{
    public void introduceRow(TableModel model,
			     int index,
			     int flags)
    {
	Speech.say("row " + index);
    }

    public int getInitialHotPointX(TableModel model)
    {
	return 2;
    }

    public String getCellText(TableModel model,
		       int col,
		       int row)
    {
	if (model == null)
	    return "#NO MODEL#";
	Object cell = model.getCell(col, row);
	return cell != null?cell.toString():"";
    }

    public String getRowPrefix(TableModel model, int index)
    {
	return "  ";
    }

    public int getColWidth(TableModel model, int  colIndex)
    {
	return 10;
    }
}
