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

/**
 * The interface for obscuring table content structures. Implementing
 * this interface it is possible to fill instances of Table class with
 * exact needed data. The model isn't responsible for an appearance of a
 * whole table or its cells.All appearance details can be customized
 * through extending TableAppearance interface.
 */
public interface TableModel
{
    int getRowCount();
    int getColCount();
    Object getCell(int col, int row);
    Object getRow(int index);
    Object getCol(int index);
    void refresh();
}
