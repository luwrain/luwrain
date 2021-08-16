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

package org.luwrain.controls;

import java.io.File;

import org.luwrain.core.*;
import org.luwrain.i18n.*;

public class TestingControlContext implements ControlContext
{
    public final Clipboard clipboard = new Clipboard();
    public String spoken = "";

    @Override public void say(String text)
    {
	spoken = text;
    }

    @Override public void say(String text, Sounds sound)
    {
    }


    @Override public void sayLetter(char letter)
    {
	spoken = "";
	spoken += letter;
    }

    @Override public void silence()
    {
    }

    @Override public void onAreaNewName(Area area)
    {
    }

    @Override public void onAreaNewContent(Area area)
    {
    }

    @Override public void onAreaNewHotPoint(Area area)
    {
    }

    @Override public int getAreaVisibleHeight(Area area)
    {
	return 25;
    }

    @Override public void popup(Popup popupObj)
    {
    }

@Override public UniRefInfo getUniRefInfo(String uniRef)
    {
	return null;
    }

    @Override public I18n getI18n()
    {
	return null;
    }

    @Override public Clipboard getClipboard()
    {
	return clipboard;
    }

    @Override public void setEventResponse(EventResponse eventResponse)
    {
    }

    @Override public void playSound(Sounds sounds)
    {
    }

    @Override public String getStaticStr(String id)
    {
	return "";
    }

    @Override public String staticStr(LangStatic st)
    {
	return "";
    }

    @Override public int getScreenHeight()
    {
	return 25;
    }

    @Override public int getScreenWidth()
    {
	return 80;
    }

    @Override public void executeBkg(java.util.concurrent.FutureTask task)
    {
	NullCheck.notNull(task, "task");
    }

    @Override public void onAreaNewBackgroundSound(Area area)
    {
    }

    @Override public boolean runHooks(String hookName, Luwrain.HookRunner runner) { return false; }
    //    @Override public void runHooks(String hookName, HookContainer.HookRunner runner) {}
    @Override public boolean runHooks(String hookName, Object[] args, Luwrain.HookStrategy strategy)
    {
	return false;
    }

    @Override public String getSpeakableText(String text, Luwrain.SpeakableTextType type)
    {
	return text;
    }

    @Override public int getAreaVisibleWidth(Area area) { return 80; }
    @Override public void sayStaticStr(LangStatic str) {}
    @Override public void message(String text, Luwrain.MessageType messageType) {}
    }
