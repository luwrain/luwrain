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

package org.luwrain.core;

public interface Extension
{
    String init(Luwrain luwrain);
    Command[] getCommands(Luwrain luwrain);
    ExtensionObject[] getExtObjects(Luwrain luwrain);
    void i18nExtension(Luwrain luwrain, org.luwrain.i18n.I18nExtension i18nExt);
    org.luwrain.cpanel.Factory[] getControlPanelFactories(Luwrain luwrain);
    UniRefProc[] getUniRefProcs(Luwrain luwrain);
    void close();
}
