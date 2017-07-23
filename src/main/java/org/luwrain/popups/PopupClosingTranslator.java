/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.popups;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class PopupClosingTranslator //implements EventLoopStopCondition
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

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.isSpecial() && event.getSpecial() == KeyboardEvent.Special.ESCAPE)
	{
	    doCancel();
	    return true;
	}
	return false;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.Code.CANCEL || event.getCode() == EnvironmentEvent.Code.CLOSE)
	{
	    doCancel();
	    return true;
	}
	if (event.getCode() == EnvironmentEvent.Code.OK)
	{
	    doOk();
	    return true;
	}
	return false;
    }
}
