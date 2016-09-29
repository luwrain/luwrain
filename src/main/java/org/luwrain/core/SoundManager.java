
package org.luwrain.core;

import org.luwrain.core.util.OggPlayer;

class SoundManager
{
    enum Predefined {
POPUP
    };

    private OggPlayer oggPlayer = null;

    void playBackground(String url)
    {
	NullCheck.notEmpty(url, "url");
	if (oggPlayer != null)
	    oggPlayer.stop();
	oggPlayer = new OggPlayer(url);
	oggPlayer.start();
    }

    void playBackground(Predefined predefined)
    {
	NullCheck.notNull(predefined, "predefined");
	switch(predefined)
	{
	case POPUP:
	    playBackground("file:///home/luwrain/git/data/sounds/background/popup1.ogg");
	}
    }

    void stopBackgroudn()
    {
	if (oggPlayer != null)
	    oggPlayer.stopPlaying();
	oggPlayer = null;
    }
}
