/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.util.Vector;
import java.io.File;
import org.luwrain.core.registry.Registry;
import org.luwrain.pim.PimManager;
import org.luwrain.mmedia.EnvironmentSounds;

public class Init
{
    private static final String  PREFIX_CONF_LIST = "--conf-list=";
    private static final String  PREFIX_DATA_DIR = "--data-dir=";
    private static final String  PREFIX_USER_HOME_DIR = "--user-home-dir=";

    public static String[] commandLine;
    private static Interaction interaction = new org.luwrain.interaction.AwtInteraction();
    private static Registry registry;

    public static void go(String[] args)
    {
	commandLine = args;
	//	Log.debug("init", "command line has " + commandLine.length + " options");
	//	for(int i = 0;i < commandLine.length;i++)
	//	    Log.debug("init", "option " + (i + 1) + ":" + commandLine[i]);
	if (init())
	    Environment.run(interaction, args);
	exit();
    }

    private static boolean init()
    {
	if (!initRegistry())
	    return false;
	if (!initLanguages())
	    return false;
	if (!initSpeech())
	    return false;
	if (!initPim())
	    return false;
	if (!initDBus())
	    return false;
	if (!initInteraction())
	    return false;
	if (!initEnvironmentSounds())
	    return false;
	return true;
    }

    private static boolean initLanguages()
    {
	if (registry.getTypeOf(CoreRegistryValues.LANGS_CURRENT) != Registry.STRING)
	{
	    Log.warning("init", "No value " + CoreRegistryValues.LANGS_CURRENT + ", using English language as a default");
	    return true;
	}
	final String lang = registry.getString(CoreRegistryValues.LANGS_CURRENT);
    if (lang.equals("ru"))
    {
	Log.info("init", "using Russian language for user interface");
	Langs.setCurrentLang(new org.luwrain.langs.ru.Language());
	return true;
    }
    Log.warning("init", "unknown language \'" + lang + "\', using English as a default");
    return true;
    }

    private static boolean initRegistry()
    {
	Vector<String> confList = getConfList();
	try {
	    for(int i = 0;i < confList.size();i++)
	    {
		Log.debug("init", "reading configuration file:" + confList.get(i));
		//FIXME:		registry.readFile(confList.get(i));
	    }
	}
	catch(Exception e)
	{
	    Log.fatal("init", "an error occurred while reading registry data:" + e.getMessage());
	    return false;
	}

	for(int i = 0;i < commandLine.length;i++)
	{
	    if (commandLine[i].startsWith(PREFIX_DATA_DIR))
	    {
		String rest = commandLine[i].substring(PREFIX_DATA_DIR.length());
		registry.setString(CoreRegistryValues.INSTANCE_DATA_DIR, rest);
		Log.info("init", "data directory path is set to " + rest);
		continue;
	    }
	    if (commandLine[i].startsWith(PREFIX_USER_HOME_DIR))
	    {
		String rest = commandLine[i].substring(PREFIX_USER_HOME_DIR.length());
		registry.setString(CoreRegistryValues.INSTANCE_USER_HOME_DIR, rest);
		Log.info("init", "user home directory path is set to " + rest);
		continue;
	    }
	}
	/*FIXME:
	if (!Registry.setInstance(registry))
	{
	    Log.fatal("init", "registry instance installation failed, is it second attempt to launch Luwrain?");
	    return false;
	}
	*/
	return true;
    }

    private static boolean initSpeech()
    {
	//FIXME:
	SpeechBackEndVoiceMan backend = new SpeechBackEndVoiceMan();
	backend.connect("localhost", 5511);
	Speech.setBackEnd(backend);
	return true;
    }

    private static boolean initEnvironmentSounds()
    {
	if (registry.getTypeOf(CoreRegistryValues.INSTANCE_DATA_DIR) != Registry.STRING)
	{
	    Log.error("init", "initialization of environment sounds is impossible, no proper registry value for " + CoreRegistryValues.INSTANCE_DATA_DIR);
	    return true;
	}
	File dataDir = new File(registry.getString(CoreRegistryValues.INSTANCE_DATA_DIR));
	setSoundFileName(dataDir, "event-not-processed", EnvironmentSounds.EVENT_NOT_PROCESSED);
	setSoundFileName(dataDir, "no-applications", EnvironmentSounds.NO_APPLICATIONS);
	setSoundFileName(dataDir, "startup", EnvironmentSounds.STARTUP);
	setSoundFileName(dataDir, "shutdown", EnvironmentSounds.SHUTDOWN);
	setSoundFileName(dataDir, "main-menu", EnvironmentSounds.MAIN_MENU);
	setSoundFileName(dataDir, "main-menu-item", EnvironmentSounds.MAIN_MENU_ITEM);
	setSoundFileName(dataDir, "main-menu-empty-line", EnvironmentSounds.MAIN_MENU_EMPTY_LINE);
	return true;
    }

    static private void setSoundFileName(File dataDir,
				  String valueName,
				  int soundId)
    {
	String v = CoreRegistryValues.SOUNDS + "/" + valueName;
	if (registry.getTypeOf(v) != Registry.STRING)
	{
	    Log.warning("init", "registry has no value for sound file by path " + v);
	    return;
	}
	File f = new File(dataDir, registry.getString(v));
	if (!f.exists() || f.isDirectory())
	{
	    Log.error("init", "sound file " + f.getAbsolutePath() + " does not exist or is a directory");
	    return;
	}
	EnvironmentSounds.setSoundFile(soundId, f.getAbsolutePath());
    }

    private static boolean initPim()
    {
	//Mail;
	//FIXME:
	//News;
	if (registry.getTypeOf(CoreRegistryValues.PIM_NEWS_TYPE) != Registry.STRING)
	{
	    Log.warning("init", "No value " + CoreRegistryValues.PIM_NEWS_TYPE + " needed for news storing, news service will be inaccessible");
	    return true;
	}
	final String type = registry.getString(CoreRegistryValues.PIM_NEWS_TYPE);
	if (!type.equals("jdbc"))
	{
	    Log.warning("init", "only jdbc pim type for news is supported, news service will be inaccessible");
	    return true;
	}
	if (registry.getTypeOf(CoreRegistryValues.PIM_NEWS_URL) != Registry.STRING)
	{
	    Log.warning("init", "No value " + CoreRegistryValues.PIM_NEWS_URL + " needed for news storing, news service will be inaccessible");
	    return true;
	}
	if (registry.getTypeOf(CoreRegistryValues.PIM_NEWS_DRIVER) != Registry.STRING)
	{
	    Log.warning("init", "No value " + CoreRegistryValues.PIM_NEWS_DRIVER + " needed for news storing, news service will be inaccessible");
	    return true;
	}
	if (registry.getTypeOf(CoreRegistryValues.PIM_NEWS_LOGIN) != Registry.STRING)
	{
	    Log.warning("init", "No value " + CoreRegistryValues.PIM_NEWS_LOGIN + " needed for news storing, news service will be inaccessible");
	    return true;
	}
	if (registry.getTypeOf(CoreRegistryValues.PIM_NEWS_PASSWD) != Registry.STRING)
	{
	    Log.warning("init", "No value " + CoreRegistryValues.PIM_NEWS_PASSWD + " needed for news storing, news service will be inaccessible");
	    return true;
	}
	final String url = registry.getString(CoreRegistryValues.PIM_NEWS_URL);
	final String driver = registry.getString(CoreRegistryValues.PIM_NEWS_DRIVER);
	final String login = registry.getString(CoreRegistryValues.PIM_NEWS_LOGIN);
	final String passwd = registry.getString(CoreRegistryValues.PIM_NEWS_PASSWD);
	if (!PimManager.newsConnectJdbc(url, driver, login, passwd))
	    Log.warning("init", "news jdbc link init failed, news reading services remain inaccessible");
	return true;
    }

    private static boolean initDBus()
    {
	//FIXME:
	/*
	try {
	    org.luwrain.dbus.DBus.connect();
	}
	catch(org.freedesktop.dbus.exceptions.DBusException e)
	{
	    Log.fatal("init", "DBus initialization fault:" + e.getMessage());
	    return false;
	}
	*/
	return true;
    }

    private static boolean initInteraction()
    {
	InteractionParams params = new InteractionParams();
	String backend = "awt";
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_BACKEND) == Registry.STRING)
	    backend = registry.getString(CoreRegistryValues.INTERACTION_BACKEND); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_BACKEND + "\' or it has incorrect type, using default value");
	if (!backend.equals("awt"))
	{
	    Log.fatal("init", "unknown interaction back-end \'" + backend + "\', only \'awt\' back-end is currently supported");
	    return false;
	}
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_FONT_NAME) == Registry.STRING)
	{
	    String value = registry.getString(CoreRegistryValues.INTERACTION_FONT_NAME);
	    if (!value.trim().isEmpty())
		params.fontName = value; else
		Log.warning("init", "registry value \'" + CoreRegistryValues.INTERACTION_FONT_NAME + "\' is empty, using default value \'" + params.fontName + "\'");
	}else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_FONT_NAME + "\' or it has incorrect type, using default value \'" + params.fontName + "\'");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_INITIAL_FONT_SIZE) == Registry.INTEGER)
	    params.initialFontSize = registry.getInteger(CoreRegistryValues.INTERACTION_INITIAL_FONT_SIZE); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_INITIAL_FONT_SIZE + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_WND_X) == Registry.INTEGER)
	    params.wndLeft = registry.getInteger(CoreRegistryValues.INTERACTION_WND_X); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_WND_X + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_WND_Y) == Registry.INTEGER)
	    params.wndTop = registry.getInteger(CoreRegistryValues.INTERACTION_WND_Y); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_WND_Y + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_WND_WIDTH) == Registry.INTEGER)
	    params.wndWidth = registry.getInteger(CoreRegistryValues.INTERACTION_WND_WIDTH); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_WND_WIDTH + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_WND_HEIGHT) == Registry.INTEGER)
	    params.wndHeight = registry.getInteger(CoreRegistryValues.INTERACTION_WND_HEIGHT); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_WND_HEIGHT + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_MARGIN_LEFT) == Registry.INTEGER)
	    params.marginLeft = registry.getInteger(CoreRegistryValues.INTERACTION_MARGIN_LEFT); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_MARGIN_LEFT + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_MARGIN_TOP) == Registry.INTEGER)
	    params.marginTop = registry.getInteger(CoreRegistryValues.INTERACTION_MARGIN_TOP); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_MARGIN_TOP + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_MARGIN_RIGHT) == Registry.INTEGER)
	    params.marginRight = registry.getInteger(CoreRegistryValues.INTERACTION_MARGIN_RIGHT); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_MARGIN_RIGHT + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_MARGIN_BOTTOM) == Registry.INTEGER)
	    params.marginBottom = registry.getInteger(CoreRegistryValues.INTERACTION_MARGIN_BOTTOM); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_MARGIN_BOTTOM + "\' or it has incorrect type, using default value");
	int fontRed = 255, fontGreen = 255, fontBlue = 255;
	int bkgRed = 0, bkgGreen = 0, bkgBlue = 0;
	int splitterRed = 128, splitterGreen = 128, splitterBlue = 128;
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_FONT_RED) == Registry.INTEGER)
	    fontRed = registry.getInteger(CoreRegistryValues.INTERACTION_FONT_RED); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_FONT_RED + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_FONT_GREEN) == Registry.INTEGER)
	    fontGreen = registry.getInteger(CoreRegistryValues.INTERACTION_FONT_GREEN); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_FONT_GREEN + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_FONT_BLUE) == Registry.INTEGER)
	    fontBlue = registry.getInteger(CoreRegistryValues.INTERACTION_FONT_BLUE); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_FONT_BLUE + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_BKG_RED) == Registry.INTEGER)
	    bkgRed = registry.getInteger(CoreRegistryValues.INTERACTION_BKG_RED); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_BKG_RED + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_BKG_GREEN) == Registry.INTEGER)
	    bkgGreen = registry.getInteger(CoreRegistryValues.INTERACTION_BKG_GREEN); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_BKG_GREEN + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_BKG_BLUE) == Registry.INTEGER)
	    bkgBlue = registry.getInteger(CoreRegistryValues.INTERACTION_BKG_BLUE); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_BKG_BLUE + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_SPLITTER_RED) == Registry.INTEGER)
	    splitterRed = registry.getInteger(CoreRegistryValues.INTERACTION_SPLITTER_RED); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_SPLITTER_RED + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_SPLITTER_GREEN) == Registry.INTEGER)
	    splitterGreen = registry.getInteger(CoreRegistryValues.INTERACTION_SPLITTER_GREEN); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_SPLITTER_GREEN + "\' or it has incorrect type, using default value");
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_SPLITTER_BLUE) == Registry.INTEGER)
	    splitterBlue = registry.getInteger(CoreRegistryValues.INTERACTION_SPLITTER_BLUE); else
	    Log.warning("init", "no registry value \'" + CoreRegistryValues.INTERACTION_SPLITTER_BLUE + "\' or it has incorrect type, using default value");
	if (params.initialFontSize < 8)
	    params.initialFontSize = 8;
	if (params.wndLeft < 0)
	    params.wndLeft = 0;
	if (params.wndTop < 0)
	    params.wndTop = 0;
	if (params.wndWidth < 0)
	    params.wndWidth = 0;
	if (params.wndHeight < 0)
	    params.wndHeight = 0;
	if (params.marginLeft < 0)
	    params.marginLeft = 0;
	if (params.marginTop < 0)
	    params.marginTop = 0;
	if (params.marginRight < 0)
	    params.marginRight = 0;
	if (params.marginBottom < 0)
	    params.marginBottom = 0;
	params.fontColor = new java.awt.Color(fontRed, fontGreen, fontBlue);
	params.bkgColor = new java.awt.Color(bkgRed, bkgGreen, bkgBlue);
	params.splitterColor = new java.awt.Color(splitterRed, splitterGreen, splitterBlue);
	//FIXME:Adjust color values to be inside of range between 0 and 255;
	return interaction.init(params);
    }

    private static void shutdown()
    {
	interaction.close();
	//FIXME:	org.luwrain.dbus.DBus.shutdown();
    }

    public static void exit()
    {
	shutdown();
	System.exit(0);
    }

    static private Vector<String> getConfList()
    {
	Vector<String> res = new Vector<String>();
	for(int i = 0;i < commandLine.length;i++)
	{
	    if (!commandLine[i].startsWith(PREFIX_CONF_LIST))
		continue;
	    String rest = commandLine[i].substring(PREFIX_CONF_LIST.length());
	    String s = "";
	    for(int k = 0;k < rest.length();k++)
	    {
		if (rest.charAt(k) != ':')
		{
		    s += rest.charAt(k);
		    continue;
		}
		s = s.trim();
		if (!s.isEmpty())
		    res.add(s);
		s = "";
	    }
	    s = s.trim();
	    if (!s.isEmpty())
		res.add(s);
	}
	return res;
    }

    public static void main(String[] args)
    {                    
	go(args);
    }
}
