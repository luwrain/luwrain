/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

class OpenedPopup
{
    public Application app;
    public int index;//Popup index in the corresponding application
    public int position;
    public PopupEventLoopStopCondition stopCondition;
    public boolean noMultipleCopies;
    public boolean isWeak; 

    public OpenedPopup(Application app,
		       int index,
		       int position,
		       PopupEventLoopStopCondition stopCondition,
		       boolean noMultipleCopies,
		       boolean isWeak)
    {
	this.app = app;
	this.index = index;
	this.position = position;
	this.stopCondition = stopCondition;
	this.noMultipleCopies = noMultipleCopies;
	this.isWeak = isWeak;
    }
}
