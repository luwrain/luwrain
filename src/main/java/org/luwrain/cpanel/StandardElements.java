/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.cpanel;

public class StandardElements
{
    static public final Element
	ROOT = new SimpleElement(null, SimpleElement.class.getName() + ":ROOT"),
	APPLICATIONS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":APPLICATIONS"),
	INPUT_OUTPUT = new SimpleElement(ROOT, SimpleElement.class.getName() + ":InputOutput"),
	KEYBOARD = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":KEYBOARD"),
	SOUND = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":SOUNDS"),
	BRAILLE = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":BRAILLE"),
	SPEECH = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":SPEECH"),
	NETWORK = new SimpleElement(ROOT, SimpleElement.class.getName() + ":NETWORD"),
	HARDWARE = new SimpleElement(ROOT, SimpleElement.class.getName() + ":HARDWARE"),
	UI = new SimpleElement(ROOT, SimpleElement.class.getName() + ":UI"),
	EXTENSIONS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":EXTENSIONS"),
	WORKERS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":WORKERS");
}
