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

public class DefaultControlContext implements ControlContext
{
    protected final Luwrain luwrain;

    public DefaultControlContext(Luwrain luwrain)
    {
	this.luwrain = luwrain;
    }

    @Override public void say(String text)
    {
	luwrain.speak(text);
    }

    @Override public void say(String text, Sounds sound)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(sound, "sound");
	luwrain.speak(text, sound);
    }


    @Override public void sayStaticStr(org.luwrain.i18n.LangStatic id)
    {
	NullCheck.notNull(id, "id");
	say(staticStr(id));
    }

    @Override public void sayLetter(char letter)
    {
	luwrain.speakLetter(letter);
    }

    @Override public void onAreaNewName(Area area)
    {
	luwrain.onAreaNewName(area);
    }

    @Override public void onAreaNewContent(Area area)
    {
	luwrain.onAreaNewContent(area);
    }

    @Override public void onAreaNewHotPoint(Area area)
    {
	luwrain.onAreaNewHotPoint(area);
    }

        @Override public int getAreaVisibleWidth(Area area)
    {
	return luwrain.getAreaVisibleWidth(area);
    }

    @Override public int getAreaVisibleHeight(Area area)
    {
	return luwrain.getAreaVisibleHeight(area);
    }

    /*
    @Override public void setClipboard(String[] value)
    {
	luwrain.setClipboard(value);
    }

    @Override public String[] getClipboard()
    {
	return luwrain.getClipboard();
    }
    */

    @Override public void popup(Popup popupObj)
    {
	luwrain.popup(popupObj);
    }

    /*
    @Override public LaunchContext launchContext()
    {
	return luwrain.launchContext();
    }
    **/

    @Override public String staticStr(org.luwrain.i18n.LangStatic id)
    {
	NullCheck.notNull(id, "id");
	return luwrain.i18n().staticStr(id);
    }

    @Override public String getStaticStr(String id)
    {
	NullCheck.notNull(id, "id");
	return luwrain.i18n().getStaticStr(id);
    }


    @Override public void playSound(Sounds sound)
    {
	NullCheck.notNull(sound, "sound");
	luwrain.playSound(sound);
    }

    @Override public UniRefInfo getUniRefInfo(String uniRef)
    {
	return luwrain.getUniRefInfo(uniRef);
    }

    @Override public void silence()
    {
	luwrain.silence();
    }

    @Override public void setEventResponse(EventResponse eventResponse)
    {
	NullCheck.notNull(eventResponse, "eventResponse");
	luwrain.setEventResponse(eventResponse);
    }

    @Override public Clipboard getClipboard()
    {
	return luwrain.getClipboard();
    }

    @Override public org.luwrain.i18n.I18n getI18n()
    {
	return luwrain.i18n();
    }

    @Override public int getScreenWidth()
    {
	return luwrain.getScreenWidth();
    }

    @Override public int getScreenHeight()
    {
	return luwrain.getScreenHeight();
    }

    @Override public void executeBkg(java.util.concurrent.FutureTask task)
    {
	luwrain.executeBkg(task);
    }

    @Override public void onAreaNewBackgroundSound(Area area)
    {
	NullCheck.notNull(area, "area");
	luwrain.onAreaNewBackgroundSound(area);
    }

        @Override public     String getSpokenText(String text, Luwrain.SpokenTextType type)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(type, "type");
	return luwrain.getSpokenText(text, type);
    }

            @Override public boolean runHooks(String hookName, Object[] args, Luwrain.HookStrategy strategy)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNullItems(args, "args");
	NullCheck.notNull(strategy, "strategy");
	return luwrain.xRunHooks(hookName, args, strategy);
    }

    @Override public     void runHooks(String hookName, Luwrain.HookRunner runner)
    {
	NullCheck.notEmpty(hookName, "hookName");
	luwrain.xRunHooks(hookName, runner);
    }
}
