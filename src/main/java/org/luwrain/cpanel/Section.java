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

import java.util.*;
import org.luwrain.core.*;

public interface Section
{
    public enum Flags {
	HAS_INSERT,
	HAS_DELETE,
    };

    SectionArea getSectionArea(ControlPanel controlPanel);
    Element getElement();
    boolean canCloseSection(ControlPanel controlPanel);
    boolean onTreeInsert(ControlPanel controlPanel);
    boolean onTreeDelete(ControlPanel controlPanel);
    boolean isSectionEnabled();
    Set<Flags> getSectionFlags();
}
