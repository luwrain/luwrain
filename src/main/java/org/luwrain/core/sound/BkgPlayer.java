
package org.luwrain.core.sound;

import org.luwrain.core.*;

final class BkgPlayer
{                                                                               
    private boolean toContinue = true;
    private boolean loop = true;
    private String urlToPlay = "";

    BkgPlayer(String urlToPlay)
    {                                                                           
	NullCheck.notEmpty(urlToPlay, "urlToPlay");
	this.urlToPlay = urlToPlay;
    }                                                                           

synchronized     public void stopPlaying()
    {
    }

    void start()
    {
    }
}                                                                               
