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

import java.net.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.util.OggPlayer;

class SoundManager
{
    private final Settings.BackgroundSounds sett;
    private final Path soundsDir;
    private OggPlayer bkgOggPlayer = null;
    private boolean startingMode = false;

    SoundManager(Registry registry, org.luwrain.base.CoreProperties coreProps)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(coreProps, "coreProps");
	this.sett = Settings.createBackgroundSounds(registry);
	this.soundsDir = coreProps.getFileProperty("luwrain.dir.sounds").toPath();
    }



    void playBackground(String url)
    {
	NullCheck.notNull(url, "url");
	if (url.isEmpty())
	    return;
	stopBackground();
	bkgOggPlayer = new OggPlayer(url);
	bkgOggPlayer.start();
    }

    void playBackground(BkgSounds bkgSound)
    {
	NullCheck.notNull(bkgSound, "bkgSound");
	if (startingMode)
	    return;
	switch(bkgSound)
	{
	case STARTING:
	    playBackground(getFileUrl(sett.getStarting("")));
	    return;
	case POPUP:
	    playBackground(getFileUrl(sett.getPopup("")));
	    return;
	case FETCHING:
	    playBackground(getFileUrl(sett.getFetching("")));
	    return;
	case MAIN_MENU:
	    playBackground(getFileUrl(sett.getMainMenu("")));
	    return;
	case WIFI:
	    playBackground(getFileUrl(sett.getWifi("")));
	    return;
	case SEARCH:
	    playBackground(getFileUrl(sett.getSearch("")));
	    return;
	}
    }

    void stopBackground()
    {
	if (startingMode)
	    return;
	if (bkgOggPlayer != null)
	    bkgOggPlayer.stopPlaying();
	bkgOggPlayer = null;
    }

    void startingMode()
    {
	if (startingMode)
	    return;
	playBackground(BkgSounds.STARTING);
	startingMode = true;
    }

    void stopStartingMode()
    {
	if (!startingMode)
	    return;
	startingMode = false;
	stopBackground();
    }

    private String getFileUrl(String fileName)
    {
	NullCheck.notNull(fileName, "fileName");
	if (fileName.isEmpty())
	    return "";
	Path path = Paths.get(fileName);
	if (!path.isAbsolute())
	    path = soundsDir.resolve(path);
	try {
	    return path.toUri().toURL().toString();
	}
	catch(MalformedURLException e)
	{
	    Log.warning("core", "unable to construct sound file URL using string \'" + fileName + "\'");
	    return fileName;
	}
    }
}
