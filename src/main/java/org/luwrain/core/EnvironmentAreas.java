/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

abstract class EnvironmentAreas extends EnvironmentBase
{
    protected ScreenContentManager screenContentManager;
    protected WindowManager windowManager;
    protected final InterfaceManager interfaces = new InterfaceManager();
    protected AppManager apps;

    protected EnvironmentAreas(CmdLine cmdLine, Registry registry,
			       org.luwrain.base.CoreProperties coreProps, String lang)
    {
	super(cmdLine, registry, coreProps, lang);
    }

    public void onNewAreasLayout()
    {
	screenContentManager.updatePopupState();
	windowManager.redraw();
	updateBackgroundSound(null);
    }

    protected void updateBackgroundSound(Area updateFor)
    {
	final Area area = getValidActiveArea(false);
	//Requested area is not active, we may do nothing
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

    //Returns an effective area for the specified one
    //Returns null if specified area not known in applications and areas managers 
    //Instance is not mandatory but can increase speed of search
    protected Area getEffectiveAreaFor(Luwrain instance, Area area)
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

    //This method may not return an unwrapped area, there should be at least ta security wrapper
    protected Area getActiveArea()
    {
	final Area area = screenContentManager.getActiveArea();
	return area;
    }

    protected boolean isActiveAreaBlockedByPopup()
    {
	return screenContentManager.isActiveAreaBlockedByPopup();
    }

    protected boolean isAreaBlockedBySecurity(Area area)
    {
	return false;
    }

    Luwrain getObjForEnvironment()
    {
	return interfaces.getObjForEnvironment();
    }

    abstract Area getValidActiveArea(boolean speakMessages);
}
