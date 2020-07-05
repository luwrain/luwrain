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
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class ListPopup extends ListPopupBase
{
    protected Object result;

    public ListPopup(Luwrain luwrain, ListArea.Params params,
		     Set<Popup.Flags> popupFlags)
    {
	super(luwrain, params, popupFlags);
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	{
case ENTER:
return closing.doOk();
 }
return super.onInputEvent(event);
    }

    @Override public boolean onOk()
    {
	result = selected();
	return result != null;
    }

    @Override public boolean onCancel()
    {
	return true;
    }
}
