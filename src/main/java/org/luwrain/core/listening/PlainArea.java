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


final class PlainArea implements ListenableArea
{
    private final Area area;

    PlainArea(Area area)
    {
	this.area = area;
    }

    @Override public ListeningInfo onListeningStart()
    {
	final int fromPosX = area.getHotPointX(); 
	final int fromPosY = area.getHotPointY();
	final StringBuilder b = new StringBuilder();
	final int count = area.getLineCount();
	if (fromPosY >= count)
	    return new ListeningInfo();
	int index = fromPosY;
	String line = area.getLine(index);
	if (line == null)
	    line = "";
	line = line.substring(Math.min(line.length(), fromPosX));
	int pos = 0;
	while(true)
	{
	    while (pos < line.length() && 
		   line.charAt(pos) != '.' && line.charAt(pos) != '!' && line.charAt(pos) != '?')
		++pos;
	    if (pos >= line.length())
	    {
		b.append(line).append(" ");
		pos = 0;
		++index;
		if (index >= count)
		    break;
		line = area.getLine(index);
		if (line == null)
		    line = "";
		continue;
	    }
	    b.append(line.substring(0, pos + 1));
	    break;
	} //while(true)
	int nextPosX = pos + 1;
	int nextPosY = index;
	if (nextPosX >= line.length())
	{
	    nextPosX = 0;
	    //We may be careless that nextPosY is greater than number of lines, a corresponding check will be performed on next step
	    ++nextPosY;
	}
	//If it is still a first line, we must restore a shift for the text prior to fromPosX
	if (nextPosY == fromPosY)
	    nextPosX += fromPosX;
	return new ListeningInfo(new String(b), nextPosX, nextPosY);
    }

    @Override public void onListeningFinish(ListeningInfo listeningInfo)
    {
	NullCheck.notNull(listeningInfo, "listeningInfo");
	area.onSystemEvent(new MoveHotPointEvent(listeningInfo.getPosX(), listeningInfo.getPosY(), false));
    }
}
