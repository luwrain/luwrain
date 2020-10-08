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

package org.luwrain.core.sound;

import java.net.*;

import org.luwrain.base.*;
import org.luwrain.base.MediaResourcePlayer.Instance;
import org.luwrain.core.*;

final class BkgPlayer
{
    static private final String LOG_COMPONENT = "core";

    private final ExtObjects extObjs;
    private final String url;
    private Instance instance = null;

    BkgPlayer(ExtObjects extObjs, String url)
    {
	NullCheck.notNull(extObjs, "extObjs");
	NullCheck.notEmpty(url, "url");
	this.extObjs = extObjs;
	this.url = url;
    }                                                                           

    void start()
    {
	Log.debug(LOG_COMPONENT, "starting playing the background sound " + url.toString());
	MediaResourcePlayer player = null;
	for(MediaResourcePlayer p: extObjs.getMediaResourcePlayers())
	    if (p.getSupportedMimeType().equals(ContentTypes.SOUND_MP3_DEFAULT))
	    {
		player = p;
		break;
	    }
	if (player == null)
	{
	    Log.error(LOG_COMPONENT, "unable to find a media resource player for " + url.toString());
	    return;
	}
	this.instance = player.newMediaResourcePlayer(new MediaResourcePlayer.Listener(){
		@Override public void onPlayerTime(Instance instance, long msec)
		{
		}
		@Override public void onPlayerFinish(Instance instance)
		{
		    play();
		}
		@Override public void onPlayerError(Exception e)
		{
		    Log.error(LOG_COMPONENT, "media resource player error for " + url.toString() + ": " + e.getClass().getName() + ":" + e.getMessage());
		}
	    });
	play();
    }

    synchronized void stopPlaying()
    {
	Log.debug(LOG_COMPONENT, "stopping playing the background sound " + url.toString());
	if (instance == null)
	    return;
	instance.stop();
	instance = null;
    }

    synchronized private void play()
    {
	if (this.instance == null)
	    return;
	try {
	    instance.stop();
	    instance.play(new URL(url), new MediaResourcePlayer.Params());
	}
	catch(Throwable e)
	{
	    Log.error(LOG_COMPONENT, "unable to start playing of " + url.toString() + ": " + e.getClass().getName() + ":" + e.getMessage());
	}
    }
}                                                                               
