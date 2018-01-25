/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

final class OpenedPopup
{
    final Application app;
    final int index;//Popup index in the corresponding application
    final Popup.Position position;
    final Base.PopupStopCondition stopCondition;
    final boolean noMultipleCopies;
    final boolean isWeak; 

    OpenedPopup(Application app,
		int index,
		Popup.Position position,
		Base.PopupStopCondition stopCondition,
		boolean noMultipleCopies,
		boolean isWeak)
    {
	//app may be null
	NullCheck.notNull(position, "position");
	NullCheck.notNull(stopCondition, "stopCondition");
	this.app = app;
	this.index = index;
	this.position = position;
	this.stopCondition = stopCondition;
	this.noMultipleCopies = noMultipleCopies;
	this.isWeak = isWeak;
    }
}
