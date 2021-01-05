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

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

public final class EnvironmentSounds
{
    private final Registry registry;
    private final File soundsDir;
    private final Map<Sounds, File> soundFiles = new HashMap();
    private WavePlayers.Simple previous = null;

    public EnvironmentSounds(Registry registry, File soundsDir)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(soundsDir, "soundsDir");
	this.registry = registry;
	this.soundsDir = soundsDir;
    }

    public void play(Sounds sound, int volumePercent)
    {
	NullCheck.notNull(sound, "sound");
	final File soundFile;
	if (!soundFiles.containsKey(sound))
	{
	    soundFile = getSoundFile(sound);
	    if (soundFile == null)
	    {
		Log.error("core", "no sound file specified for Sounds." + sound.toString());
		return;
	    }
	    soundFiles.put(sound, soundFile);
	} else
	    soundFile = soundFiles.get(sound);
	if (previous != null)
	    previous.stopPlaying();
	previous = new WavePlayers.Simple(soundFile.getAbsolutePath(), volumePercent);
	new Thread(previous).start();
    }

    private File getSoundFile(Sounds sound)
    {
	NullCheck.notNull(sound, "sound");
	final String path = getRegistryPathForSound(sound);
	if (registry.getTypeOf(path) != Registry.STRING)
	    return null;
	final String value = registry.getString(path);
	if (value == null || value.isEmpty())
	    return null;
	return new File(soundsDir, value);
    }

    private String getRegistryPathForSound(Sounds sound)
    {
	NullCheck.notNull(sound, "sound");
	final String paramName = sound.toString().toLowerCase().replaceAll("_", "-");
	return Registry.join(org.luwrain.core.Settings.CURRENT_SOUND_SCHEME_PATH, paramName);
    }
}
