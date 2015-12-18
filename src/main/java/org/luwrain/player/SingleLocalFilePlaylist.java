
package org.luwrain.player;

import org.luwrain.core.NullCheck;

public class SingleLocalFilePlaylist implements Playlist
{
    private String uri ;

    public SingleLocalFilePlaylist(String uri)
    {
	NullCheck.notNull(uri, "uri");
	this.uri = uri;
    }

    @Override public String getPlaylistTitle()
    {
	return uri;
    }

    @Override public String[] getPlaylistItems()
    {
	return new String[]{uri};
    }

    @Override public boolean isStreaming()
    {
	return false;
    }

    @Override public boolean hasBookmark()
    {
	return false;
    }
}
