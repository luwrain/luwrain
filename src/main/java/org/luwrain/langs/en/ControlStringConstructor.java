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

package org.luwrain.langs.en;

import java.util.*;

public class ControlStringConstructor implements org.luwrain.app.control.StringConstructor
{
    public String groupsAreaName()
    {
	return "Configuration groups";
    }

    public String optionsAreaName()
    {
	return "Changing options";
    }

    public String rootItemName()
    {
	return "Luwrain configuration";
    }

    public String interactionItemName()
    {
	return "Interaction";
    }

    public String mailItemName()
    {
	return "E-mail";
    }

    public String newsItemName()
    {
	return "News";
    }
}
