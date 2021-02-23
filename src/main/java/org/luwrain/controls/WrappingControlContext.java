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

//LWR_API 1.0

package org.luwrain.controls;

import java.io.File;

import org.luwrain.core.*;

public class WrappingControlContext implements ControlContext
{
    protected ControlContext context;

    public WrappingControlContext(ControlContext context)
    {
	this.context = context;
    }

    @Override public void say(String text)
    {
	context.say(text);
    }

    @Override public void say(String text, Sounds sound)
    {
	context.say(text, sound);
    }

    @Override public void sayStaticStr(org.luwrain.i18n.LangStatic id)
    {
	sayStaticStr(id);
    }

    @Override public void sayLetter(char letter)
    {
	context.sayLetter(letter);
    }

    @Override public void onAreaNewName(Area area)
    {
	context.onAreaNewName(area);
    }

    @Override public void onAreaNewContent(Area area)
    {
	context.onAreaNewContent(area);
    }

    @Override public void onAreaNewHotPoint(Area area)
    {
	context.onAreaNewHotPoint(area);
    }

    @Override public int getAreaVisibleWidth(Area area)
    {
	return context.getAreaVisibleWidth(area);
    }

    @Override public int getAreaVisibleHeight(Area area)
    {
	return context.getAreaVisibleHeight(area);
    }

    @Override public void popup(Popup popupObj)
    {
	context.popup(popupObj);
    }

    @Override public String staticStr(org.luwrain.i18n.LangStatic id)
    {
	return context.staticStr(id);
    }

    @Override public String getStaticStr(String id)
    {
	return getStaticStr(id);
    }

    @Override public void playSound(Sounds sound)
    {
	context.playSound(sound);
    }

    @Override public UniRefInfo getUniRefInfo(String uniRef)
    {
	return context.getUniRefInfo(uniRef);
    }

    @Override public void silence()
    {
	context.silence();
    }

    @Override public void setEventResponse(EventResponse eventResponse)
    {
	context.setEventResponse(eventResponse);
    }

    @Override public Clipboard getClipboard()
    {
	return context.getClipboard();
    }

    @Override public org.luwrain.i18n.I18n getI18n()
    {
	return context.getI18n();
    }

    @Override public int getScreenWidth()
    {
	return context.getScreenWidth();
    }

    @Override public int getScreenHeight()
    {
	return context.getScreenHeight();
    }

    @Override public void executeBkg(java.util.concurrent.FutureTask task)
    {
	context.executeBkg(task);
    }

    @Override public void onAreaNewBackgroundSound(Area area)
    {
	context.onAreaNewBackgroundSound(area);
    }

    @Override public     String getSpeakableText(String text, Luwrain.SpeakableTextType type)
    {
	return context.getSpeakableText(text, type);
    }

    @Override public boolean runHooks(String hookName, Object[] args, Luwrain.HookStrategy strategy)
    {
	return context.runHooks(hookName, args, strategy);
    }

    @Override public     void runHooks(String hookName, Luwrain.HookRunner runner)
    {
	context.runHooks(hookName, runner);
    }

    @Override public void message(String text, Luwrain.MessageType messageType)
    {
	context.message(text, messageType);
    }
}
