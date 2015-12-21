/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.player;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.player.backends.*;
import org.luwrain.util.RegistryPath;


class Impl
{
    BackEnd regularBackEnd = null;
    BackEnd streamingBackEnd = null;

    private BackEnd currentPlayer = null;//null means the player is in idle state
    private final Vector<Listener> listeners = new Vector<Listener>();
    private Playlist currentPlaylist;
    private int currentTrackNum = 0;
    private int lastSec = 0;

synchronized void play(Playlist playlist)
    {
	NullCheck.notNull(playlist, "playlist");
	System.out.println("play1");
	if (playlist.getPlaylistItems() == null || playlist.getPlaylistItems().length < 1)
	    return;
	stop();
	this.currentPlaylist = playlist;
	currentTrackNum = 0;
	if (playlist.isStreaming())
	    currentPlayer = streamingBackEnd; else
	    currentPlayer = regularBackEnd;
	for(Listener l: listeners)
	{
	    l.onNewPlaylist(playlist);
	    l.onNewTrack(currentTrackNum);
	}
	currentPlayer.play(playlist.getPlaylistItems()[currentTrackNum]);
    }

synchronized void stop()
    {
	if (currentPlayer == null)
	    return;
	currentPlayer.stop();
	for(Listener l: listeners)
	    l.onPlayerStop();
	currentPlayer = null;
    }

synchronized Playlist getCurrentPlaylist()
    {
	return currentPlaylist;
    }

    synchronized int getCurrentTrackNum()
    {
	return currentTrackNum;
    } 

    synchronized void onBackEndTime(int sec)
    {
	if (lastSec == sec)
	    return;
	lastSec = sec;
	//	System.out.println("" + listeners.size() + " listeners");
	for(Listener l: listeners)
	    l.onTrackTime(lastSec);
    }

    synchronized void onBackEndFinish()
    {
    }

    synchronized void addListener(Listener listener)
    {
	NullCheck.notNull(listener, "listener");
	for(Listener l: listeners)
	    if (l == listener)
		return;
	listeners.add(listener);
    }

    synchronized void removeListener(Listener listener)
    {
    }
}
