/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core.extensions;

import org.luwrain.base.*;
import org.luwrain.core.*;

public class LoadedExtension
{
    public Extension ext;
    public Luwrain luwrain;
    public Command[] commands;
    public Shortcut[] shortcuts;
    public ExtensionObject[] extObjects;
    public UniRefProc[] uniRefProcs;
    public Worker[] workers;
    public org.luwrain.cpanel.Factory[] controlPanelFactories;
    public org.luwrain.speech.Factory[] speechFactories;
}
