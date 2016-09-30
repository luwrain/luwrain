
package org.luwrain.core;

import org.luwrain.core.util.OggPlayer;

class SoundManager
{
    private OggPlayer bkgOggPlayer = null;

    void playBackground(String url)
    {
	NullCheck.notEmpty(url, "url");
	stopBackground();
	bkgOggPlayer = new OggPlayer(url);
	bkgOggPlayer.start();
    }

    void playBackground(BkgSounds bkgSound)
    {
	NullCheck.notNull(bkgSound, "bkgSound");
	switch(bkgSound)
	{
	case POPUP:
	    playBackground("file:///home/luwrain/git/data/sounds/background/popup1.ogg");
	    return;
	case FETCHING:
	    playBackground("file:///home/luwrain/git/data/sounds/background/fetching1.ogg");
	    return;

	case MAIN_MENU:
	    playBackground("file:///home/luwrain/git/data/sounds/background/mainmenu1.ogg");
	    return;


	case SEARCH:
	    playBackground("file:///home/luwrain/git/data/sounds/background/search1.ogg");
	    return;

	}
    }

    void stopBackground()
    {
	if (bkgOggPlayer != null)
	    bkgOggPlayer.stopPlaying();
	bkgOggPlayer = null;
    }
}
