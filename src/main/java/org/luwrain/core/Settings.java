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

public interface Settings
{
    static public RegistryKeys keys = new RegistryKeys();

    public interface InteractionParams
    {
	int getWindowLeft(int defValue);
	int getWindowTop(int defValue);
	int getWindowWidth(int defValue);
	int getWindowHeight(int defValue);
	int getMarginLeft(int defValue);
	int getMarginTop(int defValue);
	int getMarginRight(int defValue);
	int getMarginBottom(int defValue);
	int getFontColorRed(int defValue);
	int getFontColorGreen(int defValue);
	int getFontColorBlue(int defValue);
	int getFont2ColorRed(int defValue);
	int getFont2ColorGreen(int defValue);
	int getFont2ColorBlue(int defValue);
	int getBkgColorRed(int defValue);
	int getBkgColorGreen(int defValue);
	int getBkgColorBlue(int defValue);
	int getSplitterColorRed(int defValue);
	int getSplitterColorGreen(int defValue);
	int getSplitterColorBlue(int defValue);
	int getInitialFontSize(int defValue);
	String getFontName(String defValue);
	void setWindowLeft(int value);
	void setWindowTop(int value);
	void setWindowWidth(int value);
	void setWindowHeight(int value);
	void setMarginLeft(int value);
	void setMarginTop(int value);
	void setMarginRight(int value);
	void setMarginBottom(int value);
	void setFontColorRed(int value);
	void setFontColorGreen(int value);
	void setFontColorBlue(int value);
	void setFont2ColorRed(int value);
	void setFont2ColorGreen(int value);
	void setFont2ColorBlue(int value);
	void setBkgColorRed(int value);
	void setBkgColorGreen(int value);
	void setBkgColorBlue(int value);
	void setSplitterColorRed(int value);
	void setSplitterColorGreen(int value);
	void setSplitterColorBlue(int value);
	void setInitialFontSize(int value);
	void setFontName(String value);
    }

    public interface HotKey
    {
	String getSpecial(String defValue);
	String getCharacter(String defValue);
	boolean getWithControl(boolean defValue);
	boolean getWithShift(boolean defValue);
	boolean getWithAlt(boolean defValue);
    }

    public interface MainMenuSection
    {
	String getTitle(String defValue);
	void setTitle(String value);
	String getUniRefs(String defValue);
	void setUniRefs(String value);
    }

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
	String getDocSection(String defValue);
	String getNoContent(String defValue);

	void setEventNotProcessed(String value);
	void setGeneralError(String value);
	void setMessageDone(String value);
	void setMessageNotReady(String value);
	void setMessageOk(String value);
	void setNoApplications(String value);
	void setStartup(String value);
	void setShutdown(String value);
	void setMainMenu(String value);
	void setMainMenuEmptyLine(String value);
	void setNoItemsAbove(String value);
	void setNoItemsBelow(String value);
	void setNoLinesAbove(String value);
	void setNoLinesBelow(String value);
	void setNewListItem(String value);
	void setIntroRegular(String value);
	void setIntroPopup(String value);
	void setIntroApp(String value);
	void setCommanderNewLocation(String value);
	void setGeneralTime(String value);
	void setTermBell(String value);
	void setDocSection(String value);
	void setNoContent(String value);
    }

    public interface UserInterface
    {
	String getLaunchGreeting(String defValue);
	boolean getFilePopupSkipHidden(boolean defValue);
	void setLaunchGreeting(String value);
    }

    public interface SpeechParams
    {
	int getPitch(int defValue);
	int getRate(int defValue);
	void setPitch(int value);
	void setRate(int value);
    }

    static public InteractionParams createInteractionParams(Registry registry)
    {
	return RegistryProxy.create(registry, keys.interactionParams(), InteractionParams.class);
    }

    static public HotKey createHotKey(Registry registry, String path)
    {
	return RegistryProxy.create(registry, path, HotKey.class);
    }

    static public MainMenuSection createMainMenuSection(Registry registry, String path)
    {
	return RegistryProxy.create(registry, path, MainMenuSection.class);
    }

    static public UserInterface createUserInterface(Registry registry)
    {
	return RegistryProxy.create(registry, keys.ui(), UserInterface.class);
    }

    static public SoundScheme createCurrentSoundScheme(Registry registry)
    {
	return RegistryProxy.create(registry, keys.currentSoundScheme(), SoundScheme.class);
    }

    static public SpeechParams createSpeechParams(Registry registry)
    {
	return RegistryProxy.create(registry, keys.speech(), SpeechParams.class);
    }
}
