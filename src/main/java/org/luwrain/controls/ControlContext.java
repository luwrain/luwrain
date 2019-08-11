/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.controls;

import java.io.File;

import org.luwrain.core.*;

public interface ControlContext
{
    void say(String text);
    void say(String text, Sounds sound);
    void sayStaticStr(LangStatic id);
    void sayLetter(char letter);
    void onAreaNewContent(Area area);
    void onAreaNewName(Area area);
    void onAreaNewHotPoint(Area area);
int getAreaVisibleHeight(Area area);
    int getAreaVisibleWidth(Area area);
    void popup(Popup popupObj);
    String staticStr(LangStatic id);
    String getStaticStr(String id);
    void playSound(Sounds sound);
    UniRefInfo getUniRefInfo(String uniRef);
    void silence();
    void setEventResponse(EventResponse eventResponse);
    Clipboard getClipboard();
    I18n getI18n();
    int getScreenWidth();
    int getScreenHeight();
    void executeBkg(java.util.concurrent.FutureTask task);
    void onAreaNewBackgroundSound(Area area);
    String getSpokenText(String text, Luwrain.SpokenTextType type);
        boolean runHooks(String hookName, Object[] args, Luwrain.HookStrategy strategy);
    void runHooks(String hookName, Luwrain.HookRunner runner);
}
