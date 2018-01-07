/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.*;
import java.io.*;
import java.nio.file.*;
import javax.sound.sampled.*;

class SoundsPlayer
{
    private final Map<Sounds, String> soundFiles = new HashMap();
    private WavePlayers.Simple previous = null;

    void play(Sounds sound, int volumePercent)
    {
	NullCheck.notNull(sound, "sound");
	if (!soundFiles.containsKey(sound) || soundFiles.get(sound).isEmpty())
	{
	    Log.error("core", "no sound for playing:" + sound);
	    return;
	}
	if (previous != null)
	    previous.stopPlaying();
	previous = new WavePlayers.Simple(soundFiles.get(sound), volumePercent);
	new Thread(previous).start();
    }

    boolean finished()
    {
	return previous == null || previous.finished;
    }

    void init(Registry registry, Path dataDir)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(dataDir, "dataDir");
	final Settings.SoundScheme scheme = Settings.createCurrentSoundScheme(registry);
	setSoundFile(dataDir, scheme.getEventNotProcessed(""), Sounds.EVENT_NOT_PROCESSED);
	setSoundFile(dataDir, scheme.getEndOfLine(""), Sounds.END_OF_LINE);
	setSoundFile(dataDir, scheme.getEmptyLine(""), Sounds.EMPTY_LINE);
	setSoundFile(dataDir, scheme.getAnnouncement(""), Sounds.ANNOUNCEMENT);
	setSoundFile(dataDir, scheme.getAttention(""), Sounds.ATTENTION);
	setSoundFile(dataDir, scheme.getChatMessage(""), Sounds.CHAT_MESSAGE);
	setSoundFile(dataDir, scheme.getNoApplications(""), Sounds.NO_APPLICATIONS);
	setSoundFile(dataDir, scheme.getStartup(""), Sounds.STARTUP);
	setSoundFile(dataDir, scheme.getMessage(""), Sounds.MESSAGE);
	setSoundFile(dataDir, scheme.getShutdown(""), Sounds.SHUTDOWN);
	setSoundFile(dataDir, scheme.getMainMenu(""), Sounds.MAIN_MENU);
	setSoundFile(dataDir, scheme.getMainMenuItem(""), Sounds.MAIN_MENU_ITEM);
	setSoundFile(dataDir, scheme.getMainMenuEmptyLine(""), Sounds.MAIN_MENU_EMPTY_LINE);
	setSoundFile(dataDir, scheme.getError(""), Sounds.ERROR);
	setSoundFile(dataDir, scheme.getFatal(""), Sounds.FATAL);
	setSoundFile(dataDir, scheme.getOk(""), Sounds.OK);
	setSoundFile(dataDir, scheme.getDone(""), Sounds.DONE);
	setSoundFile(dataDir, scheme.getBlocked(""), Sounds.BLOCKED);
	setSoundFile(dataDir, scheme.getIntroRegular(""), Sounds.INTRO_REGULAR);
	setSoundFile(dataDir, scheme.getIntroPopup(""), Sounds.INTRO_POPUP);
	setSoundFile(dataDir, scheme.getIntroApp(""), Sounds.INTRO_APP);
	setSoundFile(dataDir, scheme.getNoItemsAbove(""), Sounds.NO_ITEMS_ABOVE);
	setSoundFile(dataDir, scheme.getNoItemsBelow(""), Sounds.NO_ITEMS_BELOW);
	setSoundFile(dataDir, scheme.getNoLinesAbove(""), Sounds.NO_LINES_ABOVE);
	setSoundFile(dataDir, scheme.getNoLinesBelow(""), Sounds.NO_LINES_BELOW);
	setSoundFile(dataDir, scheme.getCommanderLocation(""), Sounds.COMMANDER_LOCATION);
	setSoundFile(dataDir, scheme.getListItem(""), Sounds.LIST_ITEM);
	setSoundFile(dataDir, scheme.getParagraph(""), Sounds.PARAGRAPH);
	setSoundFile(dataDir, scheme.getGeneralTime(""), Sounds.GENERAL_TIME);
	setSoundFile(dataDir, scheme.getTermBell(""), Sounds.TERM_BELL);
	setSoundFile(dataDir, scheme.getTableCell(""), Sounds.TABLE_CELL);
	setSoundFile(dataDir, scheme.getDocSection(""), Sounds.DOC_SECTION);
	setSoundFile(dataDir, scheme.getNoContent(""), Sounds.NO_CONTENT);
	setSoundFile(dataDir, scheme.getSearch(""), Sounds.SEARCH);
	setSoundFile(dataDir, scheme.getSelected(""), Sounds.SELECTED);
	setSoundFile(dataDir, scheme.getDeleted(""), Sounds.DELETED);
	setSoundFile(dataDir, scheme.getCancel(""), Sounds.CANCEL);
	setSoundFile(dataDir, scheme.getRegionPoint(""), Sounds.REGION_POINT);
	setSoundFile(dataDir, scheme.getPaste(""), Sounds.PASTE);
	setSoundFile(dataDir, scheme.getCopied(""), Sounds.COPIED);
	setSoundFile(dataDir, scheme.getUnselected(""), Sounds.UNSELECTED);
	setSoundFile(dataDir, scheme.getCut(""), Sounds.CUT);
    }

    private void setSoundFile(Path dataDir, String fileName,
			      Sounds sound)
    {
NullCheck.notNull(sound, "sound");
	if (fileName.isEmpty())
	    return;
	Path path = Paths.get(fileName);
	if (!path.isAbsolute())
	    path = dataDir.resolve(path);
	soundFiles.put(sound, path.toString());
    }
    }
