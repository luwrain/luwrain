/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.sound;

import java.net.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.core.*;

public final class Manager
{
    private final ExtObjects extObjs;
    private final Settings.BackgroundSounds sett;
    private final Path soundsDir;
    private BkgPlayer bkgPlayer = null;
    private boolean startingMode = false;

    public Manager(ExtObjects extObjs, Luwrain luwrain)
    {
	NullCheck.notNull(extObjs, "extObjs");
	NullCheck.notNull(luwrain, "luwrain");
	this.extObjs = extObjs;
	this.sett = Settings.createBackgroundSounds(luwrain.getRegistry());
	this.soundsDir = luwrain.getFileProperty("luwrain.dir.sounds").toPath();
    }

    public void playBackground(String url)
    {
	NullCheck.notNull(url, "url");
		stopBackground();
	if (url.isEmpty())
	    return;
	this.bkgPlayer = new BkgPlayer(this.extObjs, url);
	this.bkgPlayer.start();
    }

    public void playBackground(BkgSounds bkgSound)
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

    public void stopBackground()
    {
	if (startingMode)
	    return;
	if (bkgPlayer != null)
	    bkgPlayer.stopPlaying();
	bkgPlayer = null;
    }

    public void startingMode()
    {
	if (startingMode)
	    return;
	playBackground(BkgSounds.STARTING);
	startingMode = true;
    }

    public void stopStartingMode()
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
