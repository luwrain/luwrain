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

package org.luwrain.core;

interface ContentTypes
{
    static final String[] SOUND_WAVE = new String[]{
	"audio/vnd.wave",
    };

    static final String SOUND_WAVE_DEFAULT = SOUND_WAVE[0];

    static final String[] SOUND_MP3 = new String[]{
	"audio/mpeg",
	"audio/MPA",
	"audio/mpa-robust",
    };

    static final String SOUND_MP3_DEFAULT = SOUND_MP3[0];
}
