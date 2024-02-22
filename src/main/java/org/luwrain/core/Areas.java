/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core;

import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

import static org.luwrain.core.NullCheck.*;

abstract class Areas extends Base
{
        protected final AppManager apps;
    protected final ScreenContentManager screenContentManager;
    protected final WindowManager windowManager;

    protected Areas(CmdLine cmdLine, Registry registry,
			       PropertiesRegistry props, String lang, Interaction interaction)
    {
	super(cmdLine, registry, props, lang);
	notNull(interaction, "interaction");
	this.apps = new AppManager();
	this.screenContentManager = new ScreenContentManager(apps);
	this.windowManager = new WindowManager(interaction, screenContentManager);
    }

    //        abstract Area getActiveArea(boolean speakMessages);

    void onNewAreasLayout()
    {
	screenContentManager.updatePopupState();
	windowManager.redraw();
	updateBackgroundSound(null);
    }

    protected void updateBackgroundSound(Area updateFor)
    {
	final Area area = screenContentManager.getActiveArea();
	//The requested area isnt active, we are doing nothing
	if (updateFor != null && area != updateFor)
	    return;
	if (area != null)
	{
	    final BackgroundSoundQuery query = new BackgroundSoundQuery();
	    if (AreaQuery.ask(area, query))
	    {
		final BackgroundSoundQuery.Answer answer = query.getAnswer();
		if (answer.isUrl())
		    soundManager.playBackground(answer.getUrl()); else
		    soundManager.playBackground(answer.getBkgSound()); 
		return;
	    }
	    if (updateFor != null)
	    {
	    soundManager.stopBackground();
	    return;
	    }
	}
	//General update, checking only for popups
	if (screenContentManager.isPopupActive())
	    soundManager.playBackground(BkgSounds.POPUP); else
	    soundManager.stopBackground();
    }

    //Instance is not mandatory but can increase speed of search
    Area getFrontAreaFor(Luwrain instance, Area area)
    {
	Area effectiveArea = null;
	if (instance != null)
	{
	    final Application app = interfaces.findApp(instance);
	    if (app != null && apps.isAppLaunched(app))
		effectiveArea = apps.getCorrespondingEffectiveArea(app, area);
	}
	//No provided instance or it didn't help
	if (effectiveArea == null)
	    effectiveArea = apps.getCorrespondingEffectiveArea(area);
	return effectiveArea;
    }
}
