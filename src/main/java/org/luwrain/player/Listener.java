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

//LWR_API 1.0

package org.luwrain.player;

public interface Listener
{
    void onNewPlaylist(Playlist playlist);
    void onNewTrack(Playlist playlist, int trackNum);
    void onTrackTime(Playlist playlist, int trackNum,  long msec);
    void onNewState(Playlist playlist, Player.State state);
    void onPlayingError(Playlist playlist, Exception e);
}
