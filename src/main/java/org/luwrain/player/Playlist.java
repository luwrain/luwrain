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

import org.luwrain.core.*;

public final class Playlist
{
    static final public class ExtInfo
    {
	private final Map<String, String> props = new HashMap<String, String>();

	public ExtInfo(Map<String, String> props)
	{
	    NullCheck.notNull(props, "props");
	    for(Map.Entry<String, String> e: props.entrySet())
	    {
		if (e.getKey() != null && e.getValue() != null)
		    this.props.put(e.getKey(), e.getValue());
	    }
	}

	public String getProp(String propName)
	{
	    NullCheck.notEmpty(propName, "propName");
	    if (!props.containsKey(propName))
		return "";
	    return props.get(propName);
	}
    }

    private final String[] urls;
    private final ExtInfo extInfo;

    public Playlist(String[] urls)
    {
	NullCheck.notNullItems(urls, "urls");
	this.urls = urls;
	this.extInfo = null;
    }

    public Playlist(String title, String[] urls)
    {
	NullCheck.notNull(title, "title");
	NullCheck.notNullItems(urls, "urls");
	this.urls = urls;
	final Map<String, String> props = new HashMap<String, String>();
	props.put("title", title);
	this.extInfo = new ExtInfo(props);
    }

    public Playlist(String url)
    {
	NullCheck.notNull(url, "url");
	this.urls = new String[]{url};
	this.extInfo = null;
    }

    public Playlist(String title, String url)
    {
	NullCheck.notNull(title, "title");
	NullCheck.notNull(url, "url");
	this.urls = new String[]{url};
	final Map<String, String> props = new HashMap<String, String>();
	props.put("title", title);
	this.extInfo = new ExtInfo(props);
    }

    public Playlist(String[] urls, ExtInfo extInfo)
    {
	NullCheck.notNullItems(urls, "urls");
	this.urls = urls;
	this.extInfo = extInfo;
    }

    public String getPlaylistTitle()
    {
	return extInfo != null?extInfo.getProp("title"):"";
    }

    public String[] getPlaylistUrls()
    {
	return urls;
    }

    //May return null
    public ExtInfo getExtInfo()
    {
	return extInfo;
    }

    @Override public String toString()
    {
	return getPlaylistTitle();
    }
}
