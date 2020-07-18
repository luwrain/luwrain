/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.core.*;
import org.luwrain.core.events.*;

/**
 * Unifies all actions which could result in closing of a popup. There
 * are several actions which mean that the popup must be closed (escape
 * button, closing environment event, accepting a result etc) at that
 * some of the actions mean closing normally and another mean cancelling
 * the popup. This class encapsulates the usual popup behaviour of popup
 * closing, processing various types of events. The popup itself is
 * accessed through {@code Provider} interface and is allowed to accept
 * or reject the recognized actions.
 */
public class PopupClosingTranslator
{
    public interface Provider
    {
	boolean onOk();
	boolean onCancel();
    }

    protected final Provider provider;
    protected boolean shouldContinue = true; 
    protected boolean cancelled = true;

    public PopupClosingTranslator(Provider provider)
    {
	NullCheck.notNull(provider, "provider");
	this.provider = provider;
    }

    public boolean doOk()
    {
	if (!provider.onOk())
	    return false;
	cancelled = false;
	shouldContinue = false;
	return true;
    }

    public void doCancel()
    {
	if (!provider.onCancel())
	    return;
	cancelled = true;
	shouldContinue = false;
    }

    public boolean cancelled()
    {
	return cancelled;
    }

    public boolean continueEventLoop()
    {
	return shouldContinue;
    }

    public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && event.getSpecial() == InputEvent.Special.ESCAPE)
	{
	    doCancel();
	    return true;
	}
	return false;
    }

    public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case CANCEL:
	case CLOSE:
	    doCancel();
	    return true;
	case OK:
	    doOk();
	    return true;
	default:
	return false;
	}
    }
}
