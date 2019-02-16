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

package org.luwrain.core.script.api;

import java.util.*;
import java.util.function.*;
import jdk.nashorn.api.scripting.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.player.*;

final class PlayerObj extends AbstractJSObject
{
    private final Player player;

    PlayerObj(Player player)
    {
	NullCheck.notNull(player, "player");
	this.player = player;
    }

    @Override public Object getMember(String name)
    {
	NullCheck.notNull(name, "name");
	switch(name)
	{
	case "play":
	    return (BiPredicate)this::play;
	case "pauseResume":
	    	    return (Supplier)this::pauseResume;

		    	case "stop":
	    	    return (Supplier)this::stop;

		    
	case "jump":
	    return (Predicate)this::jump;
	case "setVolume":
	    return (Consumer)this::setVolume;
	case "getVolume":
	    return (Supplier)this::getVolume;
	case "state":
	    	return player.getState().toString().toLowerCase();
	default:
	    return super.getMember(name);
	}
    }

    private boolean play(Object playlist, Object props)
    {
	final JSObject jsPlaylist = org.luwrain.script.ScriptUtils.toValidJsObject(playlist);
	if (jsPlaylist == null)
	    return false;
	final JSObject jsTracks = org.luwrain.script.ScriptUtils.toValidJsObject(jsPlaylist.getMember("tracks"));
	if (jsTracks == null)
	    return false;
	final List<String> tracks = org.luwrain.script.ScriptUtils.getStringArray(jsTracks);
	if (tracks == null || tracks.isEmpty())
	    return false;
	player.play(new Playlist(tracks.toArray(new String[tracks.size()])), 0, 0, Player.DEFAULT_FLAGS);
	return true;
    }

    private Object pauseResume()
    {
	return new Boolean(player.pauseResume());
    }

        private Object stop()
    {
	return new Boolean(player.stop());
    }


    private boolean jump(Object offset)
    {
	if (offset == null || !(offset instanceof Number))
	    return false;
	return player.jump(((Number)offset).longValue());
    }

    private Object getVolume()
    {
	return new Integer(player.getVolume());
    }

    private void setVolume(Object level)
    {
	if (level == null || !(level instanceof Number))
	    return;
player.setVolume(((Number)level).intValue());
    }
}
