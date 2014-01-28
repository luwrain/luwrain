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

package org.luwrain.app.message;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class MessageApp implements Application, MessageActions
{
    private MessageStringConstructor stringConstructor = null;
    private Object instance;
    private MessageArea messageArea;

    public MessageApp()
    {
    }

    public boolean onLaunch(Object instance)
    {
	Object o = Langs.requestStringConstructor("message");
	if (o == null)
	    return false;
	stringConstructor = (MessageStringConstructor)o;
	messageArea = new MessageArea(this, stringConstructor);
	this.instance = instance;
	return true;
    }

    public AreaLayout getAreasToShow()
    {
	return new AreaLayout(messageArea);
    }

    public void closeMessage()
    {
	Dispatcher.closeApplication(instance);
    }
}
