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

package org.luwrain.core.sysapp.mainmenu;

import org.luwrain.core.*; 
import org.luwrain.mmedia.EnvironmentSounds;

class EmptyItem implements Item
{
    @Override public String getText()
    {
	return "";
    }

    @Override public void introduce()
    {
	Speech.silence();
	EnvironmentSounds.play(EnvironmentSounds.MAIN_MENU_EMPTY_LINE);
    }

    @Override public boolean isAction()
    {
	return false;
    }

    @Override public void doAction()
    {
    }
}
