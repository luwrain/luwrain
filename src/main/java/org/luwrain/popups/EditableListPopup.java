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

package org.luwrain.popups;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.util.*;

public class EditableListPopup extends EditableListArea implements Popup, PopupClosingTranslator.Provider
{
    protected final PopupClosingTranslator closing = new PopupClosingTranslator(this);
    protected final Luwrain luwrain;
    protected final Set<Popup.Flags> popupFlags;

    public EditableListPopup(Luwrain luwrain, EditableListArea.Params params, Set<Popup.Flags> popupFlags)
    {
	super(params);
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(popupFlags, "popupFlags");
	this.luwrain = luwrain;
	this.popupFlags = popupFlags;
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (closing.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != SystemEvent.Type.REGULAR)
	    return super.onSystemEvent(event);
	if (closing.onSystemEvent(event))
	    return true;
	switch(event.getCode())
	{
	case INTRODUCE:
	    luwrain.silence();
	    luwrain.playSound(Sounds.INTRO_POPUP);
	    luwrain.speak(getAreaName());
	    return true;
	default:
	return super.onSystemEvent(event);
	}
    }

    @Override public boolean onOk()
    {
	return true;
    }

    @Override public boolean onCancel()
    {
	return true;
    }

    public Object[] result()
    {
	final int count = editableListModel.getItemCount();
	if (count < 1)
	    return new Object[0];
	final List<Object> res = new ArrayList<>();
	for(int i = 0;i < count;++i)
	    res.add(editableListModel.getItem(i));
	return res.toArray(new Object[res.size()]);
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
