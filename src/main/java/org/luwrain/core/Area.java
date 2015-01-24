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

package org.luwrain.core;

import org.luwrain.core.events.*;

//Cannot be composite;
//Real character length on the screen never taken into account;
//getLineCount() never returns 0, an empty area - the area  with single empty line;

public interface Area extends Lines
{
    //May never return 0, empty area means one empty line;
    //    int getLineCount();
    //    String getLine(int index);
    int getHotPointX();
    int getHotPointY();
    boolean onKeyboardEvent(KeyboardEvent event);
    boolean onEnvironmentEvent(EnvironmentEvent event);
    String getName();

    /*TODO:
     * Command[] getCommands();
     */

}
