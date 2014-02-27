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

package org.luwrain.app.commander;

class Task
{
    public static final int LAUNCHED = 0;
    public static final int FAILED = 1;
    public static final int DONE = 2;

    public String title = "";
    public int state = LAUNCHED;
    public int percent = 0;

    public Task(String title)
    {
	this.title = title;
    }
}
