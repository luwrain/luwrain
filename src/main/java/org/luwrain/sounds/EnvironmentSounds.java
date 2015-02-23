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

package org.luwrain.sounds;

import java.util.*;
import java.io.File;

import org.luwrain.core.Sounds;
import org.luwrain.core.Registry;
import org.luwrain.core.LaunchContext;
import org.luwrain.util.RegistryAutoCheck;

public class EnvironmentSounds
{
    private static Vector<String> soundFiles = new Vector<String>();
    private static EnvironmentSoundPlayer previous;

    public static void play(int index)
    {
	if (soundFiles == null)
	    throw new NullPointerException("soundFiles may not be null");
	if (index < 0)
	    throw new IllegalArgumentException("illegal index equal to " + index);
	if (soundFiles.get(index) == null)
return;
	if (previous != null)
	    previous.interruptPlayback = true;
	previous = new EnvironmentSoundPlayer(soundFiles.elementAt(index));
	Thread t = new Thread(previous);
	t.start();
    }

    public static void init(Registry registry, LaunchContext launchContext)
    {
	if (registry == null)
	    throw new NullPointerException("registry may not be null");
	if (launchContext == null)
	    throw new NullPointerException("launchContent may not be null");
	RegistryAutoCheck check = new RegistryAutoCheck(registry, "sounds");
	RegistryKeys keys = new RegistryKeys();
	setSoundFile(launchContext.dataDirAsFile(), check.stringNotEmpty(keys.eventNotProcessed(), ""), Sounds.EVENT_NOT_PROCESSED);
	setSoundFile(launchContext.dataDirAsFile(), check.stringNotEmpty(keys.noApplications(), ""), Sounds.NO_APPLICATIONS);
	setSoundFile(launchContext.dataDirAsFile(), check.stringNotEmpty(keys.startup(), ""), Sounds.STARTUP);
	setSoundFile(launchContext.dataDirAsFile(), check.stringNotEmpty(keys.shutdown(), ""), Sounds.SHUTDOWN);
	setSoundFile(launchContext.dataDirAsFile(), check.stringNotEmpty(keys.mainMenu(), ""), Sounds.MAIN_MENU);
	setSoundFile(launchContext.dataDirAsFile(), check.stringNotEmpty(keys.mainMenuItem(), ""), Sounds.MAIN_MENU_ITEM);
	setSoundFile(launchContext.dataDirAsFile(), check.stringNotEmpty(keys.mainMenuEmptyItem(), ""), Sounds.MAIN_MENU_EMPTY_LINE);
	setSoundFile(launchContext.dataDirAsFile(), check.stringNotEmpty(keys.generalError(), ""), Sounds.GENERAL_ERROR);
    }

    private static void setSoundFile(File dataDir,
				    String fileName,
				    int index)
    {
	if (fileName == null || fileName.isEmpty())
	    return;
	if (index < 0)
	    throw new IllegalArgumentException("index equal to " + index + " may not be negative");
	if (index >= soundFiles.size())
	    soundFiles.setSize(index + 1);
	soundFiles.set(index, new File(dataDir, fileName).getAbsolutePath());
    }
}