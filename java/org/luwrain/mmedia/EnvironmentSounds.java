/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.mmedia;

import java.util.*;

public class EnvironmentSounds
{
    public static final int EVENT_NOT_PROCESSED = 0;
    public static final int MAIN_MENU = 1;
    public static final int MAIN_MENU_ITEM = 2;

    private static Vector<String> soundFiles = new Vector<String>();
    private static EnvironmentSoundPlayer previous;

    public static void play(int index)
    {
	if (index >= soundFiles.size() || soundFiles.elementAt(index) == null)
	    return;
	if (previous != null)
	    previous.interruptPlayback = true;
	previous = new EnvironmentSoundPlayer(soundFiles.elementAt(index));
	Thread t = new Thread(previous);
	t.start();
    }

    public static void setSoundFile(int index, String fileName)
    {
	if (index >= soundFiles.size())
	    soundFiles.setSize(index + 1);
	soundFiles.set(index, fileName);
    }
}
