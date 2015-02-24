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

package org.luwrain.controls;

public interface ListItemAppearance
{
    static final public int BRIEF = 1;
    static final public int FOR_CLIPBOARD = 2;

    void introduceItem(Object item, int flags);
    String getScreenAppearance(Object item, int flags);
    int getObservableLeftBound(Object item);
    int getObservableRightBound(Object item);
}
