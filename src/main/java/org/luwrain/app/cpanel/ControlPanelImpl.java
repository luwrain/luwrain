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

package org.luwrain.app.cpanel;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

class ControlPanelImpl implements org.luwrain.cpanel.ControlPanel
{
    private Luwrain luwrain;
    private Actions actions;

    ControlPanelImpl(Luwrain luwrain, Actions actions)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(actions, "actions");
	this.luwrain = luwrain;
	this.actions = actions;
    }

    @Override public void close()
    {
	actions.closeApp();
    }

    @Override public void gotoSectionsTree()
    {
	actions.gotoSections();
    }

    @Override public void refreshSectionsTree()
    {
	actions.refreshSectionsTree();
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	return false;
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return false;
    }

    @Override public Luwrain getCoreInterface()
    {
	return luwrain;
    }
}
