/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.cpanel;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public interface ControlPanel
{
    void close();
    void gotoSectionsTree();
    boolean onSystemEvent(SystemEvent event);
    boolean onInputEvent(InputEvent event);
    void refreshSectionsTree();
    Luwrain getCoreInterface();
}
