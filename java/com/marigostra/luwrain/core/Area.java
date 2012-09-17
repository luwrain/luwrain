/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.core;

import com.marigostra.luwrain.core.events.*;

public interface Area
{
    //May never return 0, empty area means one empty line;
    int getLineCount();
    String getLine(int index);
    int getHotPointX();
    int getHotPointY();
    //Never announce new hot point position;
    //    void newHotPointRequest(int x, int y);
    void onKeyboardEvent(KeyboardEvent event);
    void onEnvironmentEvent(EnvironmentEvent event);
    String getName();

    /* For future including:
     * void describe(int describeWhat) (General area purpose, area parameters, the object under hot point);
     * void describeAtPoint(int x, int y);
     * Command[] getCommands();
     */

}
