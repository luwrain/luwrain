/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.player.*;

public final class Player implements PropertiesProvider, org.luwrain.player.Listener
{
    private PropertiesProvider.Listener listener = null;
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

    @Override public Set<org.luwrain.base.PropertiesProvider.Flags> getPropertyFlags(String propName)
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
	case "luwrain.player.track.sec":
	    return new Long(trackTimeMsec / 1000).toString();
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

    @Override public void setListener(org.luwrain.base.PropertiesProvider.Listener listener)
    {
	this.listener = listener;
    }

        @Override public void onNewPlaylist(Playlist playlist)
    {
    }
    
    @Override public void onNewTrack(Playlist playlist, int trackNum)
    {
    }
    
    @Override public void onTrackTime(Playlist playlist, int trackNum,  long msec)
    {
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
