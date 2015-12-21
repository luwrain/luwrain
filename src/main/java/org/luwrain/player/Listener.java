
package org.luwrain.player;

public interface Listener
{
    void onNewPlaylist(Playlist playlist);
    void onNewTrack(int trackNum);
    void onTrackTime(int sec);
    void onPlayerStop();
}
