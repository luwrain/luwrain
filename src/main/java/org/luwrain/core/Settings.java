/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

public interface Settings
{
    static public RegistryKeys keys = new RegistryKeys();

    public interface SoundScheme 
    {
	String getEventNotProcessed(String defValue);
	String getGeneralError(String defValue);
	String getMessageDone(String defValue);
	String getMessageNotReady(String defValue);
	String getMessageOk(String defValue);
	String getNoApplications(String defValue);
	String getStartup(String defValue);
	String getShutdown(String defValue);
	String getMainMenu(String defValue);
	String getMainMenuEmptyLine(String defValue);
	String getNoItemsAbove(String defValue);
	String getNoItemsBelow(String defValue);
	String getNoLinesAbove(String defValue);
	String getNoLinesBelow(String defValue);
	String getNewListItem(String defValue);
	String getIntroRegular(String defValue);
	String getIntroPopup(String defValue);
	String getIntroApp(String defValue);
	String getCommanderNewLocation(String defValue);
	String getGeneralTime(String defValue);
	String getTermBell(String defValue);
    }

    public interface UserInterface
    {
	String getLaunchGreeting(String defValue);
	boolean getFilePopupSkipHidden(boolean defValue);
	void setLaunchGreeting(String value);
    }

    static public UserInterface createUserInterface(Registry registry)
    {
	return RegistryProxy.create(registry, keys.ui(), UserInterface.class);
    }

    static public SoundScheme createCurrentSoundScheme(Registry registry)
    {
	return RegistryProxy.create(registry, keys.currentSoundScheme(), SoundScheme.class);
    }
}
