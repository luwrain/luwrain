
package org.luwrain.player;

public interface Playlist
{
    String getPlaylistTitle();
    String[] getPlaylistItems();
    boolean isStreaming();
    boolean hasBookmark();
}
