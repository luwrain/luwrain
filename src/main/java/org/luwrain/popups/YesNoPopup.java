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

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

public class YesNoPopup implements Popup, PopupClosingTranslator.Provider
{
    protected final Luwrain luwrain;
    protected final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final String name;
    protected final String text;
    protected boolean res;
    protected final boolean defaultRes;
    protected final Set<Popup.Flags> popupFlags;

    public YesNoPopup(Luwrain luwrain, String name, String text,
		      boolean defaultRes, Set<Popup.Flags> popupFlags)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(name, "name");
	NullCheck.notNull(text, "text");
	NullCheck.notNull(popupFlags, "popupFlags");
	this.luwrain = luwrain;
	this.name = name;
	this.text = text;
	this.defaultRes = defaultRes;
	this.res = defaultRes;
	this.popupFlags = popupFlags;
    }

    @Override public int getLineCount()
    {
	return 1;
    }

    @Override public String getLine(int index)
    {
	return index == 0?text:"";
    }

    @Override public int getHotPointX()
    {
	return text.length();
    }

    @Override public int getHotPointY()
    {
	return 0;
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onInputEvent(event))
	    return true;
	if (!event.isSpecial())
	{
	    final char c = event.getChar();
	    if (KeyboardEvent.getKeyboardLayout().onSameButton(c, 'y'))
	    {
		res = true;
		closing.doOk();
		return true;
	    }
	    if (KeyboardEvent.getKeyboardLayout().onSameButton(c, 'n'))
	    {
		res = false;
		closing.doOk();
		return true;
	    }
	    return false;
	}
	if (event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case ENTER:
	    closing.doOk();
	    return true;
	case ARROW_UP:
	case ARROW_DOWN:
	case ARROW_LEFT:
	case ARROW_RIGHT:
	    luwrain.speak(text);
	    return true;
	default:
	    return false;
	}
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return false;
	switch (event.getCode())
	{
	case CLIPBOARD_COPY:
	case CLIPBOARD_COPY_ALL:
	    luwrain.getClipboard().set(text);
	    return true;
	case INTRODUCE:
	    luwrain.silence();
	    luwrain.playSound(Sounds.INTRO_POPUP);
	    luwrain.speak(text);
	    return true;
	default:
	return closing.onSystemEvent(event);
	}
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	switch(query.getQueryCode())
	{
	case AreaQuery.REGION_TEXT:
	    {
	    if (!(query instanceof RegionTextQuery))
		return false;
	    final RegionTextQuery regionTextQuery = (RegionTextQuery)query;
	    regionTextQuery.answer(text);
	    return true;
	    }
	default:
	    return false;
	}
    }

    @Override public Action[] getAreaActions()
    {
	return new Action[0];
    }

    @Override public String getAreaName()
    {
	return name;
    }

    public boolean result()
    {
	return res;
    }

    @Override public boolean onOk()
    {
	return true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    @Override public Luwrain getLuwrainObject()
    {
	return luwrain;
    }

    @Override public boolean isPopupActive()
    {
	return closing.continueEventLoop();
    }

    public boolean wasCancelled()
    {
	return closing.cancelled();
    }

    @Override public Set<Popup.Flags> getPopupFlags()
    {
	return popupFlags;
    }
}
