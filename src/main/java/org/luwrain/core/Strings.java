/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import org.luwrain.hardware.Partition;

public interface Strings
{
    String noClipboardContent();
    String regionPointSet();
    String linesCopied(int linesNum);
    String linesInserted(int linesNum);
    String quitPopupName();
    String quitPopupText();
    String appLaunchNoEnoughMemory();
    String appLaunchUnexpectedError();
    String appCloseHasPopup();
    String noCommand();
    String startWorkFromMainMenu();
    String noLaunchedApps();
    String fontSize(int size);
    String openPopupName();
    String openPopupPrefix();
    String commandPopupName();
    String commandPopupPrefix();
    String appBlockedByPopup();
    String partitionTitle(Partition part);
    String uniRefPrefix(String uniRefType);
    String linesDeleted();
    String noReadingChannel();
}
