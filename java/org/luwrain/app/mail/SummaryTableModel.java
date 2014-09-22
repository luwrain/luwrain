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

class SummaryTableModel implements TableModel
{
    public int getRowCount()
    {
	return 3;
    }

    public int getColCount()
    {
	return 3;
    }

    public Object getCell(int col, int row)
    {
	return "Luwrain experimental cell";
    }

    public Object getRow(int index)
    {
	return "Row";
    }

    public Object getCol(int index)
    {
	return "Column";
    }

    public void refresh()
    {
    }
}
