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


public class Impl implements Player
{
    private final BackEnd jlayer = new JLayer();
    private final BackEnd javafx = new JavaFx();
    private BackEnd currentPlayer = null;

    private Registry registry;
    private final Vector<Listener> listeners = new Vector<Listener>();
    private Playlist currentPlaylist;

    public Impl(Registry registry)
    {
	this.registry = registry;
	NullCheck.notNull(registry, "registry");
    }

    @Override public void play(Playlist playlist)
    {
	NullCheck.notNull(playlist, "playlist");
	if (playlist.getPlaylistItems() == null || playlist.getPlaylistItems().length < 1)
	    return;
	this.currentPlaylist = playlist;
	stop();
	if (playlist.isStreaming())
	    currentPlayer = jlayer; else
	    currentPlayer = javafx;
	currentPlayer.play(playlist.getPlaylistItems()[0]);
    }

    @Override public void stop()
    {
	if (currentPlayer == null)
	    return;
	currentPlayer.stop();
    }

    @Override public Playlist getCurrentPlaylist()
    {
	return currentPlaylist;
    }

    @Override public Playlist[] loadRegistryPlaylists()
    {
	final String dir = "/org/luwrain/player/playlists";//FIXME:
	final String[] dirs = registry.getDirectories(dir); 
	final LinkedList<Playlist> res = new LinkedList<Playlist>();
	for(String s: dirs)
	{
	    final String path = RegistryPath.join(dir, s);
	    final RegistryPlaylist playlist = new RegistryPlaylist(registry);
	    if (playlist.init(path))
		res.add(playlist);
	}
	return res.toArray(new Playlist[res.size()]);
    }

    @Override public void addListener(Listener listener)
    {
	NullCheck.notNull(listener, "listener");
	for(Listener l: listeners)
	    if (l == listener)
		return;
	listeners.add(listener);
    }

    @Override public void removeListener(Listener listener)
    {

    }
}
