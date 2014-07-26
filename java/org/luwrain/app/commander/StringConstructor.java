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

package org.luwrain.app.commander;

import java.io.*;

public interface StringConstructor
{
    String appName();
    String leftPanelName(String path);
    String rightPanelName(String path);
    String tasksAreaName();
    String noItemsAbove();
    String noItemsBelow();
    String inaccessibleDirectoryContent();
    String rootDirectory();
    String dirItemIntroduction(DirItem item, boolean brief);
    String copying(File[] files);
    String done();
    String failed();
    String copyPopupName();
    String copyPopupPrefix(File[] files);
    String movePopupName();
    String movePopupPrefix(File[] files);
    String mkdirPopupName();
    String mkdirPopupPrefix();
    String delPopupName();
    String delPopupPrefix(File[] files);
}
