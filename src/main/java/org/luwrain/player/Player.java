/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.*;

public interface Player
{
    static public final String SHARED_OBJECT_NAME = "luwrain.player";
    static public final Set<Flags> DEFAULT_FLAGS = EnumSet.noneOf(Flags.class);

    public enum Flags {CYCLED, RANDOM, STREAMING};
    public enum Result {OK, INVALID_PLAYLIST, UNSUPPORTED_FORMAT_STARTING_TRACK, INACCESSIBLE_SOURCE, GENERAL_PLAYER_ERROR};
    public enum State {LOADING, PLAYING, PAUSED, STOPPED};

    /**
     * Starts playing of the specified playlist. This method acts in separate
     * thread and returns execution control immediately. If there is a previous
     * playing, initiated prior to this call, it will be silently
     * cancelled. You may specify the desired track number and a position in
     * audio file to begin playing from.
     *
     * @param playlist A playlist to play
     * @param startingTrackNum A desired 0-based track number to play from
     * @param startingPosMsec A position in audio file in milliseconds to start playing from
     */
    Result play(Playlist playlist, int startingTrackNum, long startingPosMsec, Set<Flags> flags);
    boolean stop();
    boolean pauseResume();
    boolean jump(long offsetMsec);
    boolean nextTrack();
    boolean prevTrack();
    State getState();
    boolean hasPlaylist();
    Playlist getPlaylist();
    int getTrackNum();
    void addListener(Listener listener);
    void removeListener(Listener listener);
}
