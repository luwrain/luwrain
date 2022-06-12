/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.shell.desktop;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.io.json.*;

final class Appearance extends ListUtils.AbstractAppearance<DesktopItem>
{
    private final Luwrain luwrain;

    Appearance(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }

    @Override public void announceItem(DesktopItem item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	NullCheck.notNull(flags, "flags");
	    final UniRefInfo info = item.getUniRefInfo(luwrain);
	    UniRefUtils.defaultAnnouncement(new DefaultControlContext(luwrain), info, Sounds.DESKTOP_ITEM, Suggestions.CLICKABLE_LIST_ITEM);
    }

    @Override public String getScreenAppearance(DesktopItem item, Set<Flags> flags)
    {
	NullCheck.notNull(item, "item");
	    final UniRefInfo info = item.getUniRefInfo(luwrain);
	    return info.toString();
    }
}
