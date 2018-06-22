/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.util.Set;
import java.io.File;
import java.nio.file.*;//FIXME:

import org.luwrain.base.FilesOperations;
import org.luwrain.speech.Channel;

/**
 * The main bridge for applications and extensions purposed for communication with
 * LUWRAIN core. This class is a central object to be used by
 * applications and extensions to call system routines. Applications and
 * extensions never have the access with the level deeper than this
 * class. The packages like {@code org.luwrain.controls} or 
 * {@code org.luwrain.popups} always wrap the instance of {@code Luwrain} class
 * (meaning, are unable to give more access to system core than provided
 * with given instance of {@code Luwrain} class).
 * <p>
 * The core creates new instance of this class for each newly launched
 * application or loaded extension. Therefore, the environment is always
 * aware which application oor extension has issued the particular
 * request. Applications get the object associated with them through
 * {@code onLaunch()} method. Extensions get the corresponding instance
 * through the argument for the methods they override (it is always the
 * same instance provided this way just for convenience). Everybody is
 * encouraged to keep provided instance in secret. 
 * <p>
 * It could be slightly confusing that the extension and the applications
 * launched by this extension get different instances of {@code Luwrain}
 * class, but it's necessary to distinguish multiple instances of the
 * particular application (while an extension can be loaded only once).
 * <p>
 * Various instance of {@code Luwrain} class may provide different level
 * of access.  It is necessary to make extensions using more accurate and
 * transparent.
 */
public interface LuwrainProto extends org.luwrain.base.EventConsumer, org.luwrain.base.PropertiesBase
{
    public enum MessageType {
	ANNOUNCEMENT,
	DONE,
	ERROR,
	NONE,
	OK,
	REGULAR,
	UNAVAILABLE,
    };

   
    public enum AreaTextType {
	REGION,
	WORD,
	LINE,
	SENTENCE,
	URL,
    };

    String getActiveAreaText(AreaTextType type, boolean issueErrorMessage);

    //Never returns null, returns user home dir if area doesn't speak about that
    String currentAreaDir();

    void enqueueEvent(Event e);

    /**
     * Returns a path to the directory where the application may safely store
     * its auxiliary data. The returned directory, if it it isn't {@code null},
     * always exists and always belongs to the current user. Meaning,
     * different users get different directories for the same
     * application. Application must be identified with some short string. We
     * discourage using application names starting with "luwrain.", because
     * such names are usually used by the applications from LUWRAIN standard
     * distribution.
     *
     * @param appName A short string for application identification, the same application name will result in the same directory
     * @return The application data directory or {@code null} if the directory cannot be created
     */
    Path getAppDataDir(String appName);

    void closeApp();
    Registry getRegistry();
    I18n i18n();
    void crash(Exception e);
    void launchApp(String shortcutName);
void launchApp(String shortcutName, String[] args);
    /*
LaunchContext launchContext();

void message(String text);
void message(String text, MessageType semantic);
void message(String text, Sounds sound);

    /**
     * Notifies the environment that the area gets new position of the hot
     * point. This method causes updating of the visual position of the hot
     * point on the screen for low vision users.  Please keep in mind that
     * this method doesn't produce any speech announcement of the new
     * position and you should do that on your own, depending on the
     * behaviour of your application.
     *
     * @param area The area which gets new position of the hot point
     */
    void onAreaNewHotPoint(Area area);

    /**
     * Notifies the environment that the area gets new content. This method
     * causes updating of the visual representation of the area content on
     * the screen for low vision users.  Please keep in mind that this method
     * doesn't produce any speech announcement of the changes and you should
     * do that on your own, depending on the behaviour of your application.
     *
     * @param area The area which gets new content
     */
void onAreaNewContent(Area area);

    /**
     * Notifies the environment that the area gets new name. This method
     * causes updating of the visual title of the area on the screen for low
     * vision users.  Please keep in mind that this method doesn't produce
     * any speech announcement of name changes and you should do that on your
     * own, depending on the behaviour of your application.
     *
     * @param area The area which gets new name
     */
    void onAreaNewName(Area area);

    void onAreaNewBackgroundSound(Area area);

    //May return -1 if area is not shown on the screen;
int getAreaVisibleHeight(Area area);
    int getAreaVisibleWidth(Area area);
int getScreenWidth();
    int getScreenHeight();
void announceActiveArea();

Clipboard getClipboard();

    //Doesn't produce any announcement
void onNewAreaLayout();
void openFile(String fileName);
    void openFiles(String[] fileNames);

    /**
    /**
    * Plays one of the system sounds.  This method takes an identifier of
    * the system sound, stops any previous playing, if there was any, and
    * plays. The exact sound is selected depending on user's
    * settings. Please node that sounds playing isn't interfering with
    * speech. 
    *
    * @param sound The identifier of the sound to play
    */
    void playSound(Sounds sound);

    void popup(Popup popup);
    boolean runCommand(String command);
    void say(String text);
    void say(String text, Sounds sound);
    void sayLetter(char letter);
    void silence();

    /**
     * Sets the new active area of the application. This method asks the
     * environment to choose another visible area as an active area of the
     * application. This operation is applicable only to regular areas, not
     * for popup areas, for which it is pointless. In contrast to
     * {@code onAreaNewHotPoint()}, {@code onAreaNewName()} and 
     * {@code onAreaNewContent()} methods, this one produces proper introduction of
     * the area being activated.
     *
     * @param area The area to choose as an active
     */
    void setActiveArea(Area area);

    //Never returns null, doesn't take empty strings
UniRefInfo getUniRefInfo(String uniRef);
    boolean openUniRef(String uniRef);
    boolean openUniRef(UniRefInfo uniRefInfo);
    org.luwrain.browser.Browser createBrowser();
Channel getAnySpeechChannelByCond(Set<Channel.Features> cond);
Channel[] getSpeechChannelsByCond(Set<Channel.Features> cond);
    void runInMainThread(Runnable runnable);
    String[] getAllShortcutNames();
java.io.File getFileProperty(String propName);
    /*
    OsCommand runOsCommand(String cmd);
    OsCommand runOsCommand(String cmd, String dir);
    OsCommand runOsCommand(String cmd, String dir, OsCommand.Output output);
    OsCommand runOsCommand(String cmd, String dir,
			   OsCommand.Output output, OsCommand.Listener listener);
    */
String getProperty(String propName);
    void setEventResponse(EventResponse eventResponse);
    FilesOperations getFilesOperations();
}
