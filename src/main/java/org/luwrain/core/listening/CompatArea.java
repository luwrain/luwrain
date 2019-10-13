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

package org.luwrain.core.listening;

import org.luwrain .core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

final class CompatArea implements ListenableArea
{
    private final Area area;

    CompatArea(Area area)
    {
	NullCheck.notNull(area, "area");
	this.area = area;
    }

    @Override public ListeningInfo onListeningStart()
    {
	final BeginListeningQuery query = new BeginListeningQuery();
	if (!AreaQuery.ask(area, query))
	    return null;
	return new Info(query.getAnswer().getText(), query.getAnswer().getExtraInfo(), -1, -1);
    }

    @Override public void onListeningFinish(ListeningInfo listeningInfo)
    {
	NullCheck.notNull(listeningInfo, "listeningInfo");
	if (!(listeningInfo instanceof Info))
	    return;
	final Info info = (Info)listeningInfo;
	area.onSystemEvent(new ListeningFinishedEvent(info.extraData));
    }

    static private final class Info extends ListeningInfo
    {
	final Object extraData;
	Info(String text, Object extraData, int posX, int posY)
	{
	    super(text, posX, posY);
	    this.extraData = extraData;
	}
    }
}
