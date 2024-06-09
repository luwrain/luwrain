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

package org.luwrain.core.sound;

import java.util.*;
import java.io.*;
import org.apache.logging.log4j.*;

import org.luwrain.core.*;

import static org.luwrain.util.StreamUtils.*;
import static org.luwrain.core.NullCheck.*;

public final class SoundIcons
{
    static private final Logger log = LogManager.getLogger();

    private final Registry registry;
    private final File soundsDir;
    private final Map<Sounds, File> soundFiles = new HashMap<>();
        private final Map<Sounds, byte[]> cache = new HashMap<>();
    private WavePlayers.Simple previous = null;

    public SoundIcons(Registry registry, File soundsDir)
    {
	notNull(registry, "registry");
	notNull(soundsDir, "soundsDir");
	this.registry = registry;
	this.soundsDir = soundsDir;
    }

    public void play(Sounds sound, int volumePercent)
    {
	notNull(sound, "sound");
	final File soundFile;
	if (!soundFiles.containsKey(sound))
	{
	    soundFile = getSoundFile(sound);
	    if (soundFile == null)
	    {
		//Trying to load the sound file from resources
		loadToCache(sound);
		if (cache.containsKey(sound))
		{
			    	if (previous != null)
	    previous.stopPlaying();
		previous = new WavePlayers.Simple(new ByteArrayInputStream(cache.get(sound)), volumePercent);
	new Thread(previous).start();
		} else
		log.error("No sound file specified for Sounds." + sound.toString());
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

        public void play(File file, int volumePercent)
    {
	notNull(file, "file");
	if (previous != null)
	    previous.stopPlaying();
	previous = new WavePlayers.Simple(file.getAbsolutePath(), volumePercent);
	new Thread(previous).start();
    }

    public void stop()
    {
	if (previous != null)
	    previous.stopPlaying();
    }

    private File getSoundFile(Sounds sound)
    {
	notNull(sound, "sound");
	/*
	final String path = getRegistryPathForSound(sound);
	if (registry.getTypeOf(path) != Registry.STRING)
	    return null;
	final String value = registry.getString(path);
	if (value == null || value.isEmpty())
	    return null;
	return new File(soundsDir, value);
	*/
	return null;
    }

    private String getRegistryPathForSound(Sounds sound)
    {
	notNull(sound, "sound");
	final String paramName = sound.toString().toLowerCase().replaceAll("_", "-");
	return Registry.join(org.luwrain.core.Settings.CURRENT_SOUND_SCHEME_PATH, paramName);
    }

    private void loadToCache(Sounds sound)
    {
	notNull(sound, "sound");
	if (cache.containsKey(sound))
	    return;
		final String name = sound.toString().toLowerCase().replaceAll("_", "-") + ".wav";
		final var s = getClass().getResourceAsStream(name);
		if (s == null)
		    return;
		final var os = new ByteArrayOutputStream();
		try {
		try (final var is = new BufferedInputStream(s)){
		    copyAllBytes(is, os);
		}
		os.flush();
		}
		catch(IOException ex)
		{
		    log.error("Unable to load a sound resource file for " + sound.toString(), ex);
		    return;
		}
		cache.put(sound, os.toByteArray());
    }
}
