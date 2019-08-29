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

//LWR_API 1.0

package org.luwrain.cpanel;

public class StandardElements
{
    static public final Element ROOT = new SimpleElement(null, SimpleElement.class.getName() + ":ROOT");
    static public final Element APPLICATIONS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":APPLICATIONS");
    static public final Element INPUT_OUTPUT = new SimpleElement(ROOT, SimpleElement.class.getName() + ":InputOutput");
    static public final Element KEYBOARD = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":KEYBOARD");
    static public final Element SOUND = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":SOUNDS");
    static public final Element BRAILLE = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":BRAILLE");
    static public final Element SPEECH = new SimpleElement(INPUT_OUTPUT, SimpleElement.class.getName() + ":SPEECH");
    static public final Element NETWORK = new SimpleElement(ROOT, SimpleElement.class.getName() + ":NETWORD");
    static public final Element HARDWARE = new SimpleElement(ROOT, SimpleElement.class.getName() + ":HARDWARE");
    static public final Element UI = new SimpleElement(ROOT, SimpleElement.class.getName() + ":UI");
    static public final Element EXTENSIONS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":EXTENSIONS");
    static public final Element WORKERS = new SimpleElement(ROOT, SimpleElement.class.getName() + ":WORKERS");
}
