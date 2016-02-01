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

package org.luwrain.sounds;

import java.util.*;
import java.io.File;

import org.luwrain.core.*;

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
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(launchContext, "launchContext");
	final Settings.SoundScheme scheme = Settings.createCurrentSoundScheme(registry);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getEventNotProcessed(""), Sounds.EVENT_NOT_PROCESSED);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getNoApplications(""), Sounds.NO_APPLICATIONS);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getStartup(""), Sounds.STARTUP);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getShutdown(""), Sounds.SHUTDOWN);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getMainMenu(""), Sounds.MAIN_MENU);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getMainMenuEmptyLine(""), Sounds.MAIN_MENU_EMPTY_LINE);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getGeneralError(""), Sounds.GENERAL_ERROR);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getMessageOk(""), Sounds.MESSAGE_OK);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getMessageDone(""), Sounds.MESSAGE_DONE);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getMessageNotReady(""), Sounds.MESSAGE_NOT_READY);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getIntroRegular(""), Sounds.INTRO_REGULAR);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getIntroPopup(""), Sounds.INTRO_POPUP);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getIntroApp(""), Sounds.INTRO_APP);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getNoItemsAbove(""), Sounds.NO_ITEMS_ABOVE);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getNoItemsBelow(""), Sounds.NO_ITEMS_BELOW);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getNoLinesAbove(""), Sounds.NO_LINES_ABOVE);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getNoLinesBelow(""), Sounds.NO_LINES_BELOW);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getCommanderNewLocation(""), Sounds.COMMANDER_NEW_LOCATION);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getNewListItem(""), Sounds.NEW_LIST_ITEM);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getGeneralTime(""), Sounds.GENERAL_TIME);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getTermBell(""), Sounds.TERM_BELL);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getDocSection(""), Sounds.DOC_SECTION);
	setSoundFile(launchContext.dataDirAsFile(), scheme.getNoContent(""), Sounds.NO_CONTENT);
    }

    private static void setSoundFile(File dataDir, String fileName,
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
