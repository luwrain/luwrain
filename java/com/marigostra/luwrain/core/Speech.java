/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.core;

public class Speech
{
    static private SpeechBackEnd backend = null;

    static public void setBackEnd(SpeechBackEnd b)
    {
	backend = b;
    }

    public static void say(String text)
    {
	if (backend != null)
	    backend.say(text);
    }

    public static void sayLetter(char letter)
    {
	if (backend == null)
	    return;
	if (letter == ' ')
	    backend.say(Langs.staticValue(Langs.SPACE));
	backend.sayLetter(letter);
    }
}
