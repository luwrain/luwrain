
package org.luwrain.core;

import org.luwrain.core.util.OggPlayer;

class SoundManager
{
    private OggPlayer bkgOggPlayer = null;
    private boolean startingMode = false;

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
	if (startingMode)
	    return;
	switch(bkgSound)
	{
	case STARTING:
	    playBackground("file:///home/luwrain/git/data/sounds/background/starting1.ogg");
	    return;
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
	if (startingMode)
	    return;
	if (bkgOggPlayer != null)
	    bkgOggPlayer.stopPlaying();
	bkgOggPlayer = null;
    }

    void startingMode()
    {
	if (startingMode)
	    return;
	playBackground(BkgSounds.STARTING);
	startingMode = true;
    }

    void stopStartingMode()
    {
	if (!startingMode)
	    return;
	startingMode = false;
	stopBackground();
    }
}
