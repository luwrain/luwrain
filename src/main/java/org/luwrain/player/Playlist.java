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
    static private final String TITLE = "title";
    
    private final String[] urls;
    private final Map<String, String> props;

        public Playlist(String[] urls, Map<String, String> props)
    {
	NullCheck.notNullItems(urls, "urls");
	NullCheck.notNull(props, "props");
	this.urls = urls.clone();
	this.props = props;
    }

    public Playlist(String[] urls)
    {
	this(urls, new HashMap());
    }

    public Playlist(String url)
    {
	this(new String[]{url}, new HashMap());
    }

    public String getPlaylistTitle()
    {
	if (!props.containsKey(TITLE))
	    return "";
	final String res = props.get(TITLE);
	return res != null?res:"";
    }

    public String[] getPlaylistUrls()
    {
	return urls.clone();
    }

public Map<String, String> getProperties()
{
    final Map<String, String> res = new HashMap();
    for(Map.Entry<String, String> e: props.entrySet())
	res.put(e.getKey(), e.getValue());
    return res;
}

    @Override public String toString()
    {
	return getPlaylistTitle();
    }
}
