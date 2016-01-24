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

package org.luwrain.core;

public class PopupEventLoopStopCondition implements EventLoopStopCondition
{
    private EventLoopStopCondition popup;
    private boolean cancelled;

public PopupEventLoopStopCondition(EventLoopStopCondition popup)
    {
	this.popup = popup;
	this.cancelled = false;
    }

    public boolean continueEventLoop()
    {
	return !cancelled && InitialEventLoopStopCondition.shouldContinue && popup.continueEventLoop();
    }

    public void cancel()
    {
	cancelled = true;
    }
}
