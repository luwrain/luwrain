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

import java.nio.file.*;

public interface OperatingSystem
{
    org.luwrain.core.InitResult init(PropertiesBase props);
    String escapeString(String style, String value);
    org.luwrain.core.Braille getBraille();
    void openFileInDesktop(Path path);
    org.luwrain.interaction.KeyboardHandler getCustomKeyboardHandler(String subsystem);
}
