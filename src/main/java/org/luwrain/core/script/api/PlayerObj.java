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
	default:
	    return super.getMember(name);
	}
    }

    private boolean play(Object playlist, Object props)
    {
	if (playlist == null || !(playlist instanceof JSObject))
	    return false;
	final List<String> playlistItems = org.luwrain.script.ScriptUtils.getStringArray((JSObject)playlist);
	if (playlistItems == null || playlistItems.isEmpty())
	    return false;
	player.play(new Playlist(playlistItems.toArray(new String[playlistItems.size()])), 0, 0, Player.DEFAULT_FLAGS);
	return true;
    }
}
