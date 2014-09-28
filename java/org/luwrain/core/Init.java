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

import java.util.*;
import java.io.*;
import java.sql.*;
import org.luwrain.core.registry.Registry;
import org.luwrain.os.SpeechBackEnd;
import org.luwrain.os.SpeechBackEnds;
import org.luwrain.pim.PimManager;
import org.luwrain.mmedia.EnvironmentSounds;

class Init
{
    private static final String  PREFIX_CONF_LIST = "--conf-list=";
    private static final String  PREFIX_DATA_DIR = "--data-dir=";
    private static final String  PREFIX_USER_HOME_DIR = "--user-home-dir=";
    private static final String  PREFIX_ADD_REGISTRY = "--add-reg=";

    private String[] cmdLine;
    private Registry registry;
    private Interaction interaction = new org.luwrain.interaction.AwtInteraction();
    private PimManager pimManager;
    private Connection jdbcConRegistry;

    public void go(String[] args)
    {
	this.cmdLine = args;
	Log.debug("init", "command line has " + cmdLine.length + " arguments:");
	for(String s: cmdLine)
	    Log.debug("init", s);
	if (init())
	{
	    Environment environment = new Environment(cmdLine, registry, interaction, pimManager);
	    if (!Luwrain.setEnvironmentObject(environment))
		Log.fatal("init", "there is another environment object chosen as a default, probably there is another launch Luwrain instance"); else
		environment.run();
	}
	exit();
    }

    private boolean init()
    {
	if (!initRegistryFirstStage())
	    return false;
	if (!initJdbcForRegistry())
	    Log.warning("init", "jdbc initialization failed, registry access are restricted");
	if (!initRegistrySecondStage())
	    Log.warning("init", "second stage of registry initialization failed, registry data is incomplete");
	processAddRegKeys();
	if (!initLanguages())
	    return false;
	if (!initSpeech())
	    return false;
	if (!initPim())
	    Log.warning("init", "PIM initialization failed or incomplete");
	if (!initInteraction())
	    return false;
	if (!initEnvironmentSounds())
	    return false;
	return true;
    }

    private boolean initRegistryFirstStage()
    {
	registry = new Registry();
	if (!registry.initWithConfFiles(getConfList()))
	{
	    Log.fatal("init", "Stopping initialization due to configuration file error");
	    return false;
	}
	for(String s: cmdLine)
	{
	    if (s == null)
		continue;
	    if (s.startsWith(PREFIX_DATA_DIR))
	    {
		String rest = s.substring(PREFIX_DATA_DIR.length());
		registry.setStaticString(CoreRegistryValues.INSTANCE_DATA_DIR, rest);
		Log.info("init", "data directory path is set to " + rest);
		continue;
	    }
	    if (s.startsWith(PREFIX_USER_HOME_DIR))
	    {
		String rest = s.substring(PREFIX_USER_HOME_DIR.length());
		registry.setStaticString(CoreRegistryValues.INSTANCE_USER_HOME_DIR, rest);
		Log.info("init", "user home directory path is set to " + rest);
		continue;
	    }
	}
	return true;
    }

    private boolean initJdbcForRegistry()
    {
	if (registry.getTypeOf(CoreRegistryValues.REGISTRY_JDBC_URL) != Registry.STRING)
	{
	    Log.error("init", "no registry value \'" + CoreRegistryValues.REGISTRY_JDBC_URL + "\' needed for proper registry work");
	    return false;
	}
	if (registry.getTypeOf(CoreRegistryValues.REGISTRY_JDBC_DRIVER) != Registry.STRING)
	{
	    Log.error("init", "no registry value \'" + CoreRegistryValues.REGISTRY_JDBC_DRIVER + "\' needed for proper registry work");
	    return false;
	}
	if (registry.getTypeOf(CoreRegistryValues.REGISTRY_JDBC_LOGIN) != Registry.STRING)
	{
	    Log.error("init", "no registry value \'" + CoreRegistryValues.REGISTRY_JDBC_LOGIN + "\' needed for proper registry work");
	    return false;
	}
	if (registry.getTypeOf(CoreRegistryValues.REGISTRY_JDBC_PASSWD) != Registry.STRING)
	{
	    Log.error("init", "no registry value \'" + CoreRegistryValues.REGISTRY_JDBC_PASSWD + "\' needed for proper registry work");
	    return false;
	}
	final String url = registry.getString(CoreRegistryValues.REGISTRY_JDBC_URL);
	final String driver = registry.getString(CoreRegistryValues.REGISTRY_JDBC_DRIVER);
	final String login = registry.getString(CoreRegistryValues.REGISTRY_JDBC_LOGIN);
	final String passwd = registry.getString(CoreRegistryValues.REGISTRY_JDBC_PASSWD);
	if (url.trim().isEmpty())
	{
	    Log.error("init", "the registry value at " + CoreRegistryValues.REGISTRY_JDBC_URL + " is empty");
	    return false;
	}
	if (login.trim().isEmpty())
	{
	    Log.error("init", "the registry value at " + CoreRegistryValues.REGISTRY_JDBC_LOGIN + " is empty");
	    return false;
	}
	Log.debug("init", "ready to establish the jdbc connection for registry ");
	Log.debug("init", "driver: " + driver);
	Log.debug("init", "URL: " + url);
	Log.debug("init", "login: " + login);
	Log.debug("init", "passwd: " + passwd.length() + " characters");
	try {
	    Class.forName (driver).newInstance ();
	    jdbcConRegistry = DriverManager.getConnection (url, login, passwd);
	}
	catch(Exception e)
	{
	    Log.error("init", "jdbc connection problem:" + e.getMessage());
	    return false;
	}
	Log.debug("init", "jdbc connection for registry is obtained");
	return true;
    }

    private boolean initRegistrySecondStage()
    {
	if (jdbcConRegistry == null)
	{
	    Log.error("init", "skipping second stage registry initialization (no jdbc connection)");
	    return false;
	}
	return registry.initWithJdbc(jdbcConRegistry);
    }

    private boolean initLanguages()
    {
	if (registry.getTypeOf(CoreRegistryValues.LANGS_CURRENT) != Registry.STRING)
	{
	    Log.warning("init", "No value " + CoreRegistryValues.LANGS_CURRENT + ", using English language as a default");
	    return true;
	}
	final String lang = registry.getString(CoreRegistryValues.LANGS_CURRENT);
    if (lang.equals("ru"))
    {
	Log.info("init", "using Russian language in user interface");
	Langs.setCurrentLang(new org.luwrain.langs.ru.Language());
	return true;
    }

    if (lang.equals("en"))
    {
	Log.info("init", "using English language in user interface");
	Langs.setCurrentLang(new org.luwrain.langs.en.Language());
	return true;
    }
    Log.warning("init", "unknown language \'" + lang + "\', using English as a default");
    return true;
    }

    private boolean initSpeech()
    {
	if (registry.getTypeOf(CoreRegistryValues.SPEECH_TYPE) != Registry.STRING)
	{
	    Log.fatal("init", "no registry key " + CoreRegistryValues.SPEECH_TYPE + " needed for obtaining  speech output");
	    return false;
	}
	if (registry.getTypeOf(CoreRegistryValues.SPEECH_HOST) != Registry.STRING)
	{
	    Log.fatal("init", "no registry key " + CoreRegistryValues.SPEECH_HOST + " needed for obtaining  speech output");
	    return false;
	}
	if (registry.getTypeOf(CoreRegistryValues.SPEECH_PORT) != Registry.INTEGER)
	{
	    Log.fatal("init", "no registry key " + CoreRegistryValues.SPEECH_PORT + " needed for obtaining  speech output");
	    return false;
	}
	final String type = registry.getString(CoreRegistryValues.SPEECH_TYPE);
	final String host = registry.getString(CoreRegistryValues.SPEECH_HOST);
	final int port = registry.getInteger(CoreRegistryValues.SPEECH_PORT);
	Log.debug("init", "ready to obtain speech output connection with the following parameters:");
	Log.debug("init", "type: " + type);
	Log.debug("init", "host: " + host);
	Log.debug("init", "port: " + port);
	SpeechBackEnd backend = SpeechBackEnds.obtain(type, host, port);
	if (backend == null)
	{
	    Log.fatal("init", "unable to obtain speech backend with specified parameters");
	    return false;
	}
	Speech.setBackEnd(backend);
	return true;
    }

    private boolean initEnvironmentSounds()
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

    private void setSoundFileName(File dataDir,
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

    private boolean initPim()
    {
	pimManager = new PimManager(registry);
	return pimManager.initDefaultConnections();
    }

    private boolean initInteraction()
    {
	InteractionParams params = new InteractionParams();
	String backend = "awt";
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_BACKEND) == Registry.STRING)
	    backend = registry.getString(CoreRegistryValues.INTERACTION_BACKEND);
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
	}
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_INITIAL_FONT_SIZE) == Registry.INTEGER)
	    params.initialFontSize = registry.getInteger(CoreRegistryValues.INTERACTION_INITIAL_FONT_SIZE);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_WND_X) == Registry.INTEGER)
	    params.wndLeft = registry.getInteger(CoreRegistryValues.INTERACTION_WND_X);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_WND_Y) == Registry.INTEGER)
	    params.wndTop = registry.getInteger(CoreRegistryValues.INTERACTION_WND_Y);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_WND_WIDTH) == Registry.INTEGER)
	    params.wndWidth = registry.getInteger(CoreRegistryValues.INTERACTION_WND_WIDTH);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_WND_HEIGHT) == Registry.INTEGER)
	    params.wndHeight = registry.getInteger(CoreRegistryValues.INTERACTION_WND_HEIGHT);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_MARGIN_LEFT) == Registry.INTEGER)
	    params.marginLeft = registry.getInteger(CoreRegistryValues.INTERACTION_MARGIN_LEFT);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_MARGIN_TOP) == Registry.INTEGER)
	    params.marginTop = registry.getInteger(CoreRegistryValues.INTERACTION_MARGIN_TOP);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_MARGIN_RIGHT) == Registry.INTEGER)
	    params.marginRight = registry.getInteger(CoreRegistryValues.INTERACTION_MARGIN_RIGHT);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_MARGIN_BOTTOM) == Registry.INTEGER)
	    params.marginBottom = registry.getInteger(CoreRegistryValues.INTERACTION_MARGIN_BOTTOM);
	int fontRed = params.fontColor.getRed();
	int fontGreen = params.fontColor.getGreen();
	int fontBlue = params.fontColor.getBlue();
	int bkgRed = params.bkgColor.getRed();
	int bkgGreen = params.bkgColor.getGreen();
	int bkgBlue = params.bkgColor.getBlue();
	int splitterRed = params.splitterColor.getRed();
	int splitterGreen = params.splitterColor.getGreen();
	int splitterBlue = params.splitterColor.getBlue();
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_FONT_RED) == Registry.INTEGER)
	    fontRed = registry.getInteger(CoreRegistryValues.INTERACTION_FONT_RED);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_FONT_GREEN) == Registry.INTEGER)
	    fontGreen = registry.getInteger(CoreRegistryValues.INTERACTION_FONT_GREEN);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_FONT_BLUE) == Registry.INTEGER)
	    fontBlue = registry.getInteger(CoreRegistryValues.INTERACTION_FONT_BLUE);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_BKG_RED) == Registry.INTEGER)
	    bkgRed = registry.getInteger(CoreRegistryValues.INTERACTION_BKG_RED); 
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_BKG_GREEN) == Registry.INTEGER)
	    bkgGreen = registry.getInteger(CoreRegistryValues.INTERACTION_BKG_GREEN);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_BKG_BLUE) == Registry.INTEGER)
	    bkgBlue = registry.getInteger(CoreRegistryValues.INTERACTION_BKG_BLUE);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_SPLITTER_RED) == Registry.INTEGER)
	    splitterRed = registry.getInteger(CoreRegistryValues.INTERACTION_SPLITTER_RED);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_SPLITTER_GREEN) == Registry.INTEGER)
	    splitterGreen = registry.getInteger(CoreRegistryValues.INTERACTION_SPLITTER_GREEN);
	if (registry.getTypeOf(CoreRegistryValues.INTERACTION_SPLITTER_BLUE) == Registry.INTEGER)
	    splitterBlue = registry.getInteger(CoreRegistryValues.INTERACTION_SPLITTER_BLUE);
	if (params.initialFontSize < 8)
	    params.initialFontSize = 8;
	if (params.wndLeft < 0)
	    params.wndLeft = 0;
	if (params.wndTop < 0)
	    params.wndTop = 0;
	if (params.marginLeft < 0)
	    params.marginLeft = 0;
	if (params.marginTop < 0)
	    params.marginTop = 0;
	if (params.marginRight < 0)
	    params.marginRight = 0;
	if (params.marginBottom < 0)
	    params.marginBottom = 0;
	if (fontRed < 0)
	    fontGreen = 0;
	if (fontGreen < 0)
	    fontGreen = 0;
	if (fontBlue < 0)
	    fontBlue = 0;
	if (bkgRed < 0)
	    bkgGreen = 0;
	if (bkgGreen < 0)
	    bkgGreen = 0;
	if (bkgBlue < 0)
	    bkgBlue = 0;
	if (splitterRed < 0)
	    splitterGreen = 0;
	if (splitterGreen < 0)
	    splitterGreen = 0;
	if (splitterBlue < 0)
	    splitterBlue = 0;
	if (fontRed > 255)
	    fontGreen = 255;
	if (fontGreen > 255)
	    fontGreen = 255;
	if (fontBlue > 255)
	    fontBlue = 255;
	if (bkgRed > 255)
	    bkgGreen = 255;
	if (bkgGreen > 255)
	    bkgGreen = 255;
	if (bkgBlue > 255)
	    bkgBlue = 255;
	if (splitterRed > 255)
	    splitterGreen = 255;
	if (splitterGreen > 255)
	    splitterGreen = 255;
	if (splitterBlue > 255)
	    splitterBlue = 255;
	params.fontColor = new java.awt.Color(fontRed, fontGreen, fontBlue);
	params.bkgColor = new java.awt.Color(bkgRed, bkgGreen, bkgBlue);
	params.splitterColor = new java.awt.Color(splitterRed, splitterGreen, splitterBlue);
	return interaction.init(params);
    }

    private void shutdown()
    {
	interaction.close();
    }

    public void exit()
    {
	shutdown();
	System.exit(0);
    }

    private void processAddRegKeys()
    {
	RegistryValuesFile valuesFile = new RegistryValuesFile(registry);
	for(String s: cmdLine)
	{
	    if (s == null || 
		s.length() <= PREFIX_ADD_REGISTRY.length() ||
		!s.startsWith(PREFIX_ADD_REGISTRY))
		continue;
	    String rest = s.substring(PREFIX_ADD_REGISTRY.length());
	    Log.debug("init", "reading registry values from file " + rest);
	    valuesFile.readValuesFromFile(rest);
	}
    }

    private String[] getConfList()
    {
	ArrayList<String> res = new ArrayList<String>();
	for(String s: cmdLine)
	{
	    if (s == null || !s.startsWith(PREFIX_CONF_LIST))
		continue;
	    String rest = s.substring(PREFIX_CONF_LIST.length());
	    String ss = "";
	    for(int k = 0;k < rest.length();k++)
	    {
		if (rest.charAt(k) != ':')
		{
		    ss += rest.charAt(k);
		    continue;
		}
		ss = ss.trim();
		if (!ss.isEmpty())
		    res.add(ss);
		ss = "";
	    }
	    ss = ss.trim();
	    if (!ss.isEmpty())
		res.add(ss);
	}
	return res.toArray(new String[res.size()]);
    }

    public static void main(String[] args)
    {                    
	Init init = new Init();
	init.go(args);
    }
}
