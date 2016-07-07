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

package org.luwrain.controls;

import java.io.File;

import org.luwrain.core.*;

public interface ControlEnvironment
{
    void say(String text);
    void sayStaticStr(LangStatic id);
    void sayLetter(char letter);
    void hint(String text); 
    void hint(String text, int code);
    void hint(int code);
    void hintStaticString(LangStatic id);
    void onAreaNewContent(Area area);
    void onAreaNewName(Area area);
    void onAreaNewHotPoint(Area area);
int getAreaVisibleHeight(Area area);
    void popup(Popup popupObj);
    String staticStr(LangStatic id);
    String getStaticStr(String id);
    void playSound(int code);
    UniRefInfo getUniRefInfo(String uniRef);
}
