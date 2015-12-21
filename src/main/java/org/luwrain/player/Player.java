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
import java.util.concurrent.*;

import org.luwrain.core.*;
import org.luwrain.util.RegistryPath;

import org.luwrain.player.backends.*;

public class Player
{
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private FutureTask playerTask;
    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(1024); 
    private final Impl impl = new Impl();
    private Registry registry;

    public Player(Registry registry)
    {
	this.registry = registry;
	NullCheck.notNull(registry, "registry");
	start();
    }

    public void start()
    {
	if (playerTask != null && !playerTask.isDone())
	{
	    Log.warning("player", "trying to get two player threads running simultaneously");
	    return;
	}

	impl.regularBackEnd = new JavaFx(createBackEndStatus());
	impl.streamingBackEnd = new JLayer(createBackEndStatus());



	playerTask = new FutureTask(()->{
		while(true)
		{
		    try {
		    final Runnable r = queue.take();
		    if (r != null)
			r.run();
		    }
		    catch (InterruptedException e)
		    {
			Thread.currentThread().interrupt();
		    }

		}
	    }, null);
	executor.execute(playerTask);
    }

    public void play(Playlist playlist)
    {
	run(()->impl.play(playlist));
    }

    public void stop()
    {
	run(()->impl.stop());
    }

    public Playlist getCurrentPlaylist()
    {
	return impl.getCurrentPlaylist();
    }

    public int getCurrentTrackNum()
    {
	return impl.getCurrentTrackNum();
    }

    public Playlist[] loadRegistryPlaylists()
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

    public void addListener(Listener listener)
    {
	run(()->impl.addListener(listener));
    }

    public void removeListener(Listener listener)
    {
	run(()->impl.removeListener(listener));
    }


    private BackEndStatus createBackEndStatus()
    {
	return new BackEndStatus(){
	    @Override public void onBackEndTime(int sec)
	    {
		run(()->impl.onBackEndTime(sec));
	    }
	    @Override public void onBackEndFinish()
	    {
		run(()->impl.onBackEndFinish());
	    }
	};
    }

    private void run(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	try {
	    queue.put(runnable);
	}
	catch(InterruptedException e)
	{
	    Thread.currentThread().interrupt();
	}
    }
}
