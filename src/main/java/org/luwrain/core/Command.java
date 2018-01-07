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

/**
 * Context-independent action in the system. Command represents some
 * operations associated with some name (command name). Such operations
 * can be performed anywhere in the system and usually serve as handlers
 * for hotkeys.
 *
 * @see Shortcut
 */
public interface Command extends ExportedObject
{
    String getName();
    void onCommand(Luwrain luwrain);
}
