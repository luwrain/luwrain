/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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
	    return new AbstractJSObject(){
		@Override public Object call(Object th, Object[] args)
		{
		    return play(args);
		}
		@Override public boolean isFunction()
		{
		    return true;
		}
	    };
	case "pauseResume":
	    	    return (Supplier)this::pauseResume;
		    	case "stop":
	    	    return (Supplier)this::stop;
	case "nextTrack":
	    return (Supplier)()->{return new Boolean(player.nextTrack());};
	    	case "prevTrack":
	    return (Supplier)()->{return new Boolean(player.prevTrack());};
	case "jump":
	    return (Predicate)this::jump;
	case "setVolume":
	    return (Consumer)this::setVolume;
	case "getVolume":
	    return (Supplier)this::getVolume;
	case "state":
	    	return player.getState().toString().toLowerCase();
	case "flags":
	    {
		final Set<Player.Flags> flags = player.getFlags();
		final List<String> res = new LinkedList();
		if (flags != null)
		for(Object o: flags)
		res.add(o.toString().toLowerCase());
		return org.luwrain.script.ScriptUtils.createReadOnlyArray(res.toArray(new String[res.size()]));
	    }
	default:
	    return super.getMember(name);
	}
    }

    private boolean play(Object[] args)
    {
	NullCheck.notNullItems(args, "args");
	if (args.length != 5)
	    return false;
	final Object tracksObj = args[0];
	final Object trackNumObj = args[1];
	final Object fromMsecObj = args[2];
	final Object flagsObj = args[3];
	final Object propsObj = args[4];
	final List<String> tracks = org.luwrain.script.ScriptUtils.getStringArray(tracksObj);
	if (tracks == null || tracks.isEmpty())
	    return false;
	final Playlist playlist;
	try {
	    playlist = new Playlist(tracks.toArray(new String[tracks.size()]));
	}
	catch(IllegalArgumentException e)
	{
	    return false;
	}
	//FIXME:track num
	//FIXME: from msec
	final List<String> flagsList = org.luwrain.script.ScriptUtils.getStringArray(flagsObj);
	if (flagsList == null)
	    return false;
	Set<Player.Flags> flags = EnumSet.noneOf(Player.Flags.class);
	for(String f: flagsList)
	    switch(f)
	    {
	    case "streaming":
		flags.add(Player.Flags.STREAMING);
	    }
	final Properties props;
	if (propsObj instanceof org.luwrain.script.PropertiesHookObject)
	    props = ((org.luwrain.script.PropertiesHookObject)propsObj).getProperties();  else
	    props = null;
    	player.play(playlist, 0, 0, flags, props);
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
