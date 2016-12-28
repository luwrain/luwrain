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

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EditArea extends SimpleArea
{
    protected final ControlEnvironment environment;
    protected final MultilineEdit edit;
    protected final ChangeListener listener;

    public EditArea(ControlEnvironment environment, String name,
		    String[] content, ChangeListener listener)
    {
	super(environment, name, content);
	NullCheck.notNull(environment, "environment");
	this.environment = environment;
	this.listener = listener;
	edit = new MultilineEdit(environment, new MultilineEditModelChangeListener(new MultilineEditModelTranslator(super.content, this)){
		@Override public void onMultilineEditChange()
		{
		    if (listener != null)
			listener.onEditChange();
		}
	    });
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (edit.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (edit.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (edit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

    protected String getTabSeq()
    {
	return "\t";
    }

    public interface ChangeListener
    {
	void onEditChange();
    }
}
