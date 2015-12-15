
package org.luwrain.player;

import java.util.*;

class PlaylistBase implements Playlist
{
protected String title;
    protected String url;
    protected boolean streaming = false;
    protected boolean hasBookmark = false;
protected final Vector<String> items = new Vector<String>();

    @Override public String getPlaylistTitle()
    {
	return title;
    }

    @Override public String[] getPlaylistItems()
    {
	return items.toArray(new String[items.size()]);
    }

    @Override public boolean isStreaming()
    {
	return streaming;
    }

    @Override public boolean hasBookmark()
    {
	return hasBookmark;
    }

    @Override public String toString()
    {
	return title;
    }


}
