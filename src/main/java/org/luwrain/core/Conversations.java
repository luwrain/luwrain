
package org.luwrain.core;

import java.util.*;
import java.io.*;
import java.nio.file.*;

import org.luwrain.popups.*;

class Conversations
{
    private final Luwrain luwrain;
    private final Environment env;

    Conversations(Luwrain luwrain, Environment env)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(env, "env");
	this.luwrain = luwrain;
	this.env = env;
    }

    boolean quitConfirmation()
    {
	final YesNoPopup popup = new YesNoPopup(luwrain, luwrain.i18n().getStaticStr("QuitPopupName"), luwrain.i18n().getStaticStr("QuitPopupText"), true, Popups.DEFAULT_POPUP_FLAGS);
	env.popup(null, popup, Popup.Position.BOTTOM, popup.closing, true, true);
	return !popup.closing.cancelled() && popup.result();
    }

    File openPopup()
    {
	final Path current = Paths.get(luwrain.currentAreaDir());
	final FilePopup popup = new FilePopup(luwrain, 
					      luwrain.i18n().getStaticStr("OpenPopupName"),
					      luwrain.i18n().getStaticStr("OpenPopupPrefix"), 
					      null,
					      current,
					      current, 
					      env.uiSettings.getFilePopupSkipHidden(false)?EnumSet.of(FilePopup.Flags.SKIP_HIDDEN):EnumSet.noneOf(FilePopup.Flags.class),
					      EnumSet.noneOf(Popup.Flags.class));
	env.popup(null, popup, Popup.Position.BOTTOM, popup.closing, true, true);
	if (popup.closing.cancelled())
	    return null;
	return popup.result().toFile();
    }
}
