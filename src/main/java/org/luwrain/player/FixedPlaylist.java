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

//LWR_API 1.0

package org.luwrain.player;

import java.util.*;
import java.net.*;

import org.luwrain.core.*;

public final class FixedPlaylist implements Playlist
{
    private final String[] urls;
    private final VolumeListener volumeListener;
    private final ProgressListener progressListener;
    private int volume = Player.MAX_VOLUME;

    //The object will not be constructed unless all items are a valid URL
    public FixedPlaylist(String[] urls, ProgressListener progressListener, VolumeListener volumeListener, int volume)
    {
	NullCheck.notNullItems(urls, "urls");
	this.volumeListener = volumeListener;
	this.progressListener = progressListener;
	this.volume = Math.min(Math.max(volume, Player.MIN_VOLUME), Player.MAX_VOLUME);
	this.urls = new String[urls.length];
	for(int i = 0;i < urls.length;i++)
	{
	    final URL u;
	    try {
		u = new URL(urls[i]);
	    }
	    catch(MalformedURLException e)
	    {
		throw new IllegalArgumentException(e);
	    }
	    this.urls[i] = u.toString();
	}
    }

    public FixedPlaylist(String[] urls, VolumeListener volumeListener, int volume)
    {
	this(urls, null, volumeListener, Player.MAX_VOLUME);
    }

    public FixedPlaylist(String[] urls)
    {
	this(urls, null, Player.MAX_VOLUME);
    }

    public FixedPlaylist(String url)
    {
	this(new String[]{url});
    }

    public String[] getAllTracks()
    {
	return urls.clone();
    }

    @Override public int getTrackCount()
    {
	return urls.length;
    }

    @Override public String getTrackUrl(int index)
    {
	return urls[index];
    }

    @Override public int getVolume()
    {
	return this.volume;
    }

    @Override public void onNewVolume(int newVolumeLevel)
    {
	this.volume = newVolumeLevel;
	if (volumeListener != null)
	    volumeListener.onNewVolume(volume);
    }

    @Override public void onProgress(int trackIndex, long timeMsec)
    {
	if (progressListener != null)
	    progressListener.onProgress(trackIndex, timeMsec);
    }
}
