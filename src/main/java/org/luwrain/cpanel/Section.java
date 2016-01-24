/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.cpanel;

import org.luwrain.core.*;

public interface Section
{
    static public final int FLAG_HAS_INSERT = 1;
    static public final int FLAG_HAS_DELETE = 2;

    int getDesiredRoot();
    Section[] getChildSections();
    Area getSectionArea(Environment environment);
    //Must issue all necessary error message;
    boolean canCloseSection(Environment environment);
    boolean onTreeInsert(Environment environment);
    boolean onTreeDelete(Environment environment);
    boolean isSectionEnabled();

    //After this method call getSectionArea() may return another area, between calls it is undesirable
    //Usually it is called before opening
    void refreshArea(Environment environment);
    void refreshChildSubsections();
    int getSectionFlags();
}
