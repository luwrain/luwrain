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

package org.luwrain.core;

public interface ContentTypes
{
    static public final String DATA_BINARY[] = new String[]{
	"application/octet-stream",
    };

    static public final String DATA_BINARY_DEFAULT = DATA_BINARY[0];

    public enum ExpectedType {
	ANY,
	AUDIO,
	TEXT,
    };

    static public final String[] TEXT_PLAIN = new String[]{
	"text/plain",
    };

    static public final String TEXT_PLAIN_DEFAULT = TEXT_PLAIN[0];

    static public final String[] TEXT_HTML = new String[]{
	"text/html",
    };

    static public final String TEXT_HTML_DEFAULT = TEXT_HTML[0];

    static public final String[] SOUND_WAVE = new String[]{
	"audio/vnd.wave",
    };

    static public final String SOUND_WAVE_DEFAULT = SOUND_WAVE[0];

    static public final String[] SOUND_MP3 = new String[]{
	"audio/mpeg",
	"audio/MPA",
	"audio/mpa-robust",
    };

    static public final String SOUND_MP3_DEFAULT = SOUND_MP3[0];
}
