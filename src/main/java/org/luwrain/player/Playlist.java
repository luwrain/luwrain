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

//LWR_API 1.0

package org.luwrain.player;

import java.util.*;

import org.luwrain.core.*;

public final class Playlist
{
    private final String[] urls;

    //The object will not be constructed unless all items are a valid URL
        public Playlist(String[] urls)
    {
	NullCheck.notNullItems(urls, "urls");
	this.urls = urls.clone();
    }

    public Playlist(String url)
    {
	this(new String[]{url});
    }

    public String[] getPlaylistUrls()
    {
	return urls.clone();
    }
}
