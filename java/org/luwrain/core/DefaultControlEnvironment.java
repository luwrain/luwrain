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

package org.luwrain.core;

public class DefaultControlEnvironment implements ControlEnvironment
{
    public void say(String text, int pitch)
    {
	Speech.say(text, pitch);
    }

    public void say(String text)
    {
	Speech.say(text);
    }

    public void sayLetter(char letter)
    {
	Speech.sayLetter(letter);
    }

    public void onAreaNewContent(Area area)
    {
	Luwrain.onAreaNewContent(area);
    }

    public void onAreaNewHotPoint(Area area)
    {
	Luwrain.onAreaNewHotPoint(area);
    }
}
