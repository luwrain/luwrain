/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core;

public interface MutableLines extends Lines
{
    public interface Updating
    {
	public void update(MutableLines lines);
    }

    void update(Updating updating);
    String[] getLines();
    void setLines(String[] lines);
    void addLine(String line);
    void insertLine(int index, String line);
    void removeLine(int index);
    void setLine(int index, String line);
    void clear();
}
