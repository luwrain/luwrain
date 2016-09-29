/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

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

import java.util.*;
import org.luwrain.util.*;

class AppManager
{
    private final Vector<LaunchedApp> apps = new Vector<LaunchedApp>();
    private final LaunchedAppBase environment = new LaunchedAppBase();
    private final Vector<OpenedPopup> popups = new Vector<OpenedPopup>();
    private int activeAppIndex = -1;
    private LaunchedApp defaultApp;

    AppManager(Application defaultApp)
    {
	if (defaultApp != null)
	{
	    this.defaultApp = new LaunchedApp();
	    if (!this.defaultApp.init(defaultApp))
		throw new IllegalArgumentException("Provided defaultApp does not suit for LaunchedApp.init() ");
	} else
	    this.defaultApp = null;
    }

    Application[] getLaunchedApps()
    {
	final LinkedList<Application> res = new LinkedList<Application>();
	for(LaunchedApp a: apps)
	    res.add(a.app);
	return res.toArray(new Application[res.size()]);
    }

    boolean setActiveApp(Application app)
    {
	NullCheck.notNull(app, "app");
	final int index = findApp(app);
	if (index < 0)
	    return false;
	activeAppIndex = index;
	return true;
    }

    boolean isAppActive(Application app)
    {
	NullCheck.notNull(app, "app");
	if (app == defaultApp && activeAppIndex < 0)
	    return true;
	if (apps.get(activeAppIndex).app == app)
	    return true;
	return false;
    }

    Application getActiveApp()
    {
	if (activeAppIndex < 0)
	    return defaultApp != null?defaultApp.app:null;
	return apps.get(activeAppIndex).app;
    }

    boolean newApp(Application app)
    {
	NullCheck.notNull(app, "app");
	final Application activeNow = activeAppIndex >= 0?apps.get(activeAppIndex).app:null;
	final int index = findApp(app);
	if (index >= 0)
	{
	    //FIXME:prepareForSwitching();
	    activeAppIndex = index;
	    return true;
	}
	final LaunchedApp launchedApp = new LaunchedApp();
	if (!launchedApp.init(app))
	    return false;
	launchedApp.activeAppBeforeLaunch = activeNow;
	apps.add(launchedApp);
	activeAppIndex = apps.size() - 1;
	return true;
    }

    void closeApp(Application app)
    {
	NullCheck.notNull(app, "app");
	final int index = findApp(app);
	if (index == -1)
	    return;
	final LaunchedApp removedApp = apps.get(index);
	apps.remove(index);
	for(LaunchedApp a: apps)
	    if (a.activeAppBeforeLaunch == app)
		a.activeAppBeforeLaunch = null;
	if (apps.isEmpty())
	{
	    activeAppIndex = -1;
	    return;
	}
	if (removedApp.activeAppBeforeLaunch == null)
	{
	    activeAppIndex = apps.size() - 1;
	    return;
	}
	//Trying to activate the application which was active before launch of the one being removed;
	activeAppIndex = findApp(removedApp.activeAppBeforeLaunch);
	if (activeAppIndex < 0)//We have a bug in this case
	    activeAppIndex = apps.size() - 1;
    }

    void switchNextApp()
    {
	if (apps.isEmpty())
	{
	    activeAppIndex = -1;
	    return;
	}
	if (activeAppIndex < 0)//We are in desktop
	{
	    //FIXME:general preparing for switching;
	    defaultApp.removeReviewWrappers();
	    activeAppIndex = 0;
	    return;
	}
	apps.get(activeAppIndex).removeReviewWrappers();
	++activeAppIndex;
	if (activeAppIndex >= apps.size())
	    activeAppIndex = 0;
    }

    boolean refreshAreaLayoutOfApp(Application app)
    {
	NullCheck.notNull(app, "app");
	final int index = findApp(app);
	if (index < 0)
	    return false;
	return apps.get(index).refreshAreaLayout();
    }

    /**
     * Sets the new active area for the application. Area argument may
     * designate the desired area by the reference of any kind, either to the
     * original area or to any of its wrappers. The area must present in area
     * layout associated with the application/ This method sets active only
     * non-popul areas.
     *
     * @param app The application to set active area for
     * @param area The reference to new active area (to the original area or to any wrapper)
     * @return True if new active area was set, false otherwise
     */
    boolean setActiveAreaOfApp(Application app, Area area)
    {
	NullCheck.notNull(app, "app");
	NullCheck.notNull(area, "area");
	final int index = findApp(app);
	if (index < 0)
	    return false;
	return apps.get(index).setActiveArea(area);
    }

    Area getEffectiveActiveAreaOfApp(Application app)
    {
	NullCheck.notNull(app, "app");
	if (isDefaultApp(app))
	    return defaultApp.getEffectiveActiveArea();
	final int index = findApp(app);
	if (index < 0)
	    return null;
	return apps.get(index).getEffectiveActiveArea();
    }

    Area getEffectiveActiveAreaOfActiveApp()
    {
	if (activeAppIndex < 0 && hasDefaultApp())
	    return defaultApp.getEffectiveActiveArea();
	if (activeAppIndex >= 0)
	    return apps.get(activeAppIndex).getEffectiveActiveArea();
	return null;
    }

    boolean noEffectiveActiveArea()
    {
	return getEffectiveActiveAreaOfActiveApp() == null;
    }

    private int findApp(Application app)
    {
	for(int i = 0;i < apps.size();i++)
	    if (apps.get(i).app == app)
		return i;
	return -1;
    }

    //null app means environment popup;
    void addNewPopup(Application app, Area area,
			    Popup.Position position, PopupEventLoopStopCondition stopCondition,
			    boolean noMultipleCopies, boolean isWeak)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(position, "position");
	NullCheck.notNull(stopCondition, "stopCondition");
	LaunchedAppBase launchedApp;
	if (app != null)
	{
	    //Desktop may not open popups;
	    final int index = findApp(app);
	    if (index < 0)
		return;
	    launchedApp = apps.get(index);
	} else
	    launchedApp = environment;
	final int popupIndex = 	launchedApp.addPopup(area);
	popups.add(new OpenedPopup(app, popupIndex, position, stopCondition, noMultipleCopies, isWeak));
    }

    void closeLastPopup()
    {
	if (popups.isEmpty())
	{
	    Log.warning("core", "trying to remove the last popup without having any popups at all");
	    return;
	}
	final OpenedPopup removedPopup = popups.lastElement();
	popups.remove(popups.size() - 1);
	if (removedPopup.app != null)
	{
	    final int appIndex = findApp(removedPopup.app);
	    if (appIndex >= 0)
		apps.get(appIndex).closeLastPopup(); else
		Log.warning("core", "the popup being closing is associated with the unknown application");
	} else
	    environment.closeLastPopup();
    }

    boolean isLastPopupDiscontinued()
    {
	if (popups.isEmpty())
	    return true;
	return !popups.lastElement().stopCondition.continueEventLoop();
    }

    boolean hasAnyPopup()
    {
	return !popups.isEmpty();
    }

    //null is a valid argument;
    boolean hasPopupOfApp(Application app)
    {
	LaunchedAppBase launchedApp;
	if (app != null)
	{
	    final int index = findApp(app);
	    if (index < 0)
		return false;
	    launchedApp = apps.get(index);
	} else
	    launchedApp = environment;
	return launchedApp.popups.size() > 0;
    }

    Application getAppOfLastPopup()
    {
	return !popups.isEmpty()?popups.lastElement().app:null;
    }

    Area getEffectiveAreaOfLastPopup()
    {
	if (popups.isEmpty())
	    return null;
	final OpenedPopup popup = popups.lastElement();
	LaunchedAppBase launchedApp;
	if (popup.app != null)
	{
	    final int appIndex = findApp(popup.app);
	    if (appIndex < 0)
	    {
		Log.warning("core", "last popup is associated with the unknown application");
		return null;
	    }
	    launchedApp = apps.get(appIndex);
	} else
	    launchedApp = environment;
	return launchedApp.getEffectiveAreaOfPopup(popup.index);
    }

    AreaLayout getEffectiveAreaLayout(Application app)
    {
	NullCheck.notNull(app, "app");
	if (isDefaultApp(app))
	    return defaultApp.getEffectiveAreaLayout();
	final int index = findApp(app);
	if (index < 0)
	    return null;
	return apps.get(index).getEffectiveAreaLayout();
    }

    boolean isAppLaunched(Application app)
    {
	NullCheck.notNull(app, "app");
	if (isDefaultApp(app))
	    return true;
	return findApp(app) >= 0;
    }

    //app may not be null, environment popups should be processed with getCorrespondingEffectiveArea(area);
    //Area may be an area of any kind, either natural or wrapping;
    Area getCorrespondingEffectiveArea(Application app, Area area)
    {
	NullCheck.notNull(app, "app");
	NullCheck.notNull(area, "area");
	if (isDefaultApp(app))
	    return defaultApp.getCorrespondingEffectiveArea(area);
	final int index = findApp(app);
	if (index < 0)
	    return null;
	return apps.get(index).getCorrespondingEffectiveArea(area);
    }

    //Area may be an area of any kind, either natural or wrapping;
    Area getCorrespondingEffectiveArea(Area area)
    {
	NullCheck.notNull(area, "area");
	if (hasDefaultApp())
	{
	    final Area res = defaultApp.getCorrespondingEffectiveArea(area);
	    if (res != null)
		return res;
	}
	for(LaunchedApp a: apps)
	{
	    final Area res = a.getCorrespondingEffectiveArea(area);
	    if (res != null)
		return res;
	}
	final Area res = environment.getCorrespondingEffectiveArea(area);
	if (res != null)
	    return res;
	return null;
    }

    /**
     * Returns the area wrapping object for the required area. Provided
     * reference designates a cell in the application layout, pointing either
     * to the natural area, either to the security wrapper or to the review
     * wrapper. Search is carried out over all applications (including the
     * default application) and all environment popups. 
     *
     * @param area The area designating a cell in application layout by the natural area itself or by any of its wrappers
     * @return The area wrapping which corresponds to  the requested cell of the application layout
     */
    LaunchedAppBase.AreaWrapping getAreaWrapping(Area area)
    {
	NullCheck.notNull(area, "area");
	if (hasDefaultApp())
	{
	    final LaunchedAppBase.AreaWrapping res = defaultApp.getAreaWrapping(area);
	    if (res != null)
		return res;
	}
	for(LaunchedApp a: apps)
	{
	    final LaunchedAppBase.AreaWrapping res = a.getAreaWrapping(area);
	    if (res != null)
		return res;
	}
	final LaunchedAppBase.AreaWrapping res = environment.getAreaWrapping(area);
	if (res != null)
	    return res;
	return null;
    }

    boolean setReviewAreaWrapper(Area area, ReviewAreaWrapperFactory factory)
    {
	NullCheck.notNull(area, "area");
	NullCheck.notNull(factory, "factory");
	final LaunchedAppBase.AreaWrapping wrapping = getAreaWrapping(area);
	if (wrapping == null)
	    return false;
	final Area wrapper = factory.createReviewAreaWrapper(area, wrapping);
	if (wrapper == null)
	    return false;
	wrapping.reviewWrapper = wrapper;
	return true;
    }

    Popup.Position getPositionOfLastPopup()
    {
	if (popups.isEmpty())
	    return null;
	return popups.lastElement().position;
    }

    void onNewPopupOpening(Application app, Class newCopyClass)
    {
	if (isDefaultApp(app))
	    return;
	for(OpenedPopup p: popups)
	{
	    if (p.app != app || !p.noMultipleCopies)
		continue;
	    Area area;
	    if (app != null)
	    {
		final int index = findApp(app);
		if (index < 0)
		{
		    Log.error("core", "popups contains a reference to the unregistered application " + app.getClass().getName());
		    continue;
		}
		area = apps.get(index).getNativeAreaOfPopup(p.index);
	    } else
		area = environment.getNativeAreaOfPopup(p.index);
	    if (area == null)
	    {
		Log.error("core", "unable to find a native area of the popup with index " + p.index + " of " + (app != null?" the application " + app.getClass().getName():" the environment"));
		continue;
	    }
	    if (area.getClass().equals(newCopyClass))
		p.stopCondition.cancel();
	}
    }

    boolean isLastPopupWeak()
    {
	if (popups.isEmpty())
	    return false;
	return popups.lastElement().isWeak;
    }

    void cancelLastPopup()
    {
	if (popups.isEmpty())
	    return;
	popups.lastElement().stopCondition.cancel();
    }

    void sendBroadcastEvent(org.luwrain.core.events.EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	for(LaunchedApp a: apps)
	    a.sendBroadcastEvent(event);
    }

    private boolean isDefaultApp(Application app)
    {
	if (app == null || defaultApp == null)
	    return false;
	return defaultApp.app == app;
    }

    private boolean hasDefaultApp()
    {
	return defaultApp != null;
    }
}
