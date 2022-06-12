/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core.properties;

import java.io.*;
import java.util.*;

import org.luwrain.core.*;
import org.luwrain.player.*;

public final class Player implements PropertiesProvider, org.luwrain.player.Listener
{
    static private final String PROP_TRACK_INDEX = "luwrain.player.track.index";
    static private final String PROP_TRACK_URL = "luwrain.player.track.url";
    static private final String PROP_TRACK_SEC = "luwrain.player.track.sec";

    private PropertiesProvider.Listener listener = null;
    private int trackNum = 0;
    private String trackUrl = "";
    private long trackTimeMsec = 0;

    public Player()
    {
    }

    @Override public String getExtObjName()
    {
	return this.getClass().getName();
    }

    @Override public String[] getPropertiesRegex()
    {
	return new String[0];
    }

    @Override public Set<PropertiesProvider.Flags> getPropertyFlags(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	final String value = getProperty(propName);
	if (value != null)
	    return EnumSet.of(PropertiesProvider.Flags.PUBLIC);
	return null;
    }

    @Override public String getProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	switch(propName)
	{
	    	case PROP_TRACK_INDEX:
		    	    return String.valueOf(trackNum);
			    	    	case PROP_TRACK_URL:
		    	    return trackUrl;
	case PROP_TRACK_SEC:
	    return String.valueOf(trackTimeMsec / 1000);
	default:
	    	    return null;
	}
    }

    @Override public boolean setProperty(String propName, String value)
    {
	NullCheck.notEmpty(propName, "propName");
	NullCheck.notNull(value, "value");
	return false;
    }

    @Override public void setListener(PropertiesProvider.Listener listener)
    {
	this.listener = listener;
    }

        @Override public void onNewPlaylist(Playlist playlist)
    {
    }

    @Override public void onNewTrack(Playlist playlist, int trackNum)
    {
	NullCheck.notNull(playlist, "playlist");
	this.trackNum = trackNum;
	this.trackUrl = playlist.getTrackUrl(trackNum);
	if (listener != null)
	{
	listener.onNewPropertyValue(PROP_TRACK_INDEX, String.valueOf(trackNum));
	listener.onNewPropertyValue(PROP_TRACK_URL, trackUrl);
	}
    }

    @Override public void onTrackTime(Playlist playlist, int trackNum,  long msec)
    {
	NullCheck.notNull(playlist, "playlist");
	if (msec >= trackTimeMsec && msec < trackTimeMsec + 1000)
	    return;
	this.trackTimeMsec = msec - (msec % 1000);
	if (listener != null)
	    listener.onNewPropertyValue("luwrain.player.track.sec", getProperty("luwrain.player.track.sec"));
    }
    
    @Override public void onNewState(Playlist playlist, org.luwrain.player.Player.State state)
    {
    }

    @Override public void onPlayingError(Playlist playlist, Exception e)
    {
    }
}
