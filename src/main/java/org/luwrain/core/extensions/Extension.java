/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.core.extensions;

import org.luwrain.core.*;

public interface Extension
{
    String init(Luwrain luwrain);
    Command[] getCommands(Luwrain luwrain);
    Shortcut[] getShortcuts(Luwrain luwrain);
    Worker[] getWorkers(Luwrain luwrain);
    SharedObject[] getSharedObjects(Luwrain luwrain);
    void i18nExtension(Luwrain luwrain, I18nExtension i18nExt);
    org.luwrain.mainmenu.Item[] getMainMenuItems(Luwrain luwrain);
    //FIXME:Control panel  entries;
    //FIXME:Identifiers;

    void close();
}