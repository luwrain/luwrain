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

package org.luwrain.core;

public interface ControlEnvironment
{
    void say(String text);
    void sayLetter(char letter);
    void hint(String text); 
    void hintStaticString(int id);
    void onAreaNewContent(Area area);
    void onAreaNewName(Area area);
    void onAreaNewHotPoint(Area area);
int getAreaVisibleHeight(Area area);
    String langStaticString(int id);
    void popup(Popup popupObj);
}
