/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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
import org.luwrain.speech.*;
//import org.luwrain.os.SpeechBackEnds;
import org.luwrain.mmedia.EnvironmentSounds;

class Init
{
    private static final String  PREFIX_CONF_LIST = "--conf-list=";
    private static final String  PREFIX_DATA_DIR = "--data-dir=";
    private static final String  PREFIX_USER_HOME_DIR = "--user-home-dir=";
    private static final String  PREFIX_SPEECH= "--speech=";
    private static final String  PREFIX_ADD_REGISTRY = "--add-reg=";

    private String[] cmdLine;
    private Registry registry;
    private Interaction interaction;
    private org.luwrain.speech.BackEnd speech;

    public void go(String[] args)
    {
	this.cmdLine = args;
	Log.debug("init", "command line has " + cmdLine.length + " arguments:");
	for(String s: cmdLine)
	    Log.debug("init", s);
	if (init())
	{
	    SystemDirs systemDirs = new SystemDirs();//FIXME:
	    Environment environment = new Environment(cmdLine, registry, interaction, systemDirs);
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

	//Interaction;
	InteractionParams interactionParams = new InteractionParams();
	interactionParams.loadFromRegistry(registry);
	if (!interactionParams.backend.equals("awt"))
	{
	    Log.fatal("init", "unsupported interaction type \'" + interactionParams.backend + "\'");
	    return false;
	}
	Interaction interaction = new org.luwrain.interaction.AwtInteraction();
	if (!interaction.init(interactionParams))
	{
	    Log.fatal("init", "interaction initialization failed");
	    return false;
	}



	if (!initEnvironmentSounds())
	    return false;
	return true;
    }

    private boolean initRegistryFirstStage()
    {
	/*
	//	registry = new Registry();
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
		registry.setStaticString(RegistryKeys.INSTANCE_DATA_DIR, rest);
		Log.info("init", "data directory path is set to " + rest);
		continue;
	    }
	    if (s.startsWith(PREFIX_USER_HOME_DIR))
	    {
		String rest = s.substring(PREFIX_USER_HOME_DIR.length());
		registry.setStaticString(RegistryKeys.INSTANCE_USER_HOME_DIR, rest);
		Log.info("init", "user home directory path is set to " + rest);
		continue;
	    }
	}
	*/
	return true;
    }

    private boolean initJdbcForRegistry()
    {
	/*
	if (registry.getTypeOf(RegistryKeys.REGISTRY_JDBC_URL) != Registry.STRING)
	{
	    Log.error("init", "no registry value \'" + RegistryKeys.REGISTRY_JDBC_URL + "\' needed for proper registry work");
	    return false;
	}
	if (registry.getTypeOf(RegistryKeys.REGISTRY_JDBC_DRIVER) != Registry.STRING)
	{
	    Log.error("init", "no registry value \'" + RegistryKeys.REGISTRY_JDBC_DRIVER + "\' needed for proper registry work");
	    return false;
	}
	if (registry.getTypeOf(RegistryKeys.REGISTRY_JDBC_LOGIN) != Registry.STRING)
	{
	    Log.error("init", "no registry value \'" + RegistryKeys.REGISTRY_JDBC_LOGIN + "\' needed for proper registry work");
	    return false;
	}
	if (registry.getTypeOf(RegistryKeys.REGISTRY_JDBC_PASSWD) != Registry.STRING)
	{
	    Log.error("init", "no registry value \'" + RegistryKeys.REGISTRY_JDBC_PASSWD + "\' needed for proper registry work");
	    return false;
	}
	final String url = registry.getString(RegistryKeys.REGISTRY_JDBC_URL);
	final String driver = registry.getString(RegistryKeys.REGISTRY_JDBC_DRIVER);
	final String login = registry.getString(RegistryKeys.REGISTRY_JDBC_LOGIN);
	final String passwd = registry.getString(RegistryKeys.REGISTRY_JDBC_PASSWD);
	if (url.trim().isEmpty())
	{
	    Log.error("init", "the registry value at " + RegistryKeys.REGISTRY_JDBC_URL + " is empty");
	    return false;
	}
	if (login.trim().isEmpty())
	{
	    Log.error("init", "the registry value at " + RegistryKeys.REGISTRY_JDBC_LOGIN + " is empty");
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
	*/
	return true;
    }

    private boolean initRegistrySecondStage()
    {
	/*
	if (jdbcConRegistry == null)
	{
	    Log.error("init", "skipping second stage registry initialization (no jdbc connection)");
	    return false;
	}
	return registry.initWithJdbc(jdbcConRegistry);
	*/
	return false;
    }

    private boolean initLanguages()
    {
	/*
	if (registry.getTypeOf(RegistryKeys.LANGS_CURRENT) != Registry.STRING)
	{
	    Log.warning("init", "No value " + RegistryKeys.LANGS_CURRENT + ", using English language as a default");
	    return true;
	}
	final String lang = registry.getString(RegistryKeys.LANGS_CURRENT);
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
	*/
    return true;
    }

    private boolean initSpeech()
    {
	final String backendClass = getFirstCmdLineOption(PREFIX_SPEECH);
	if (backendClass == null || backendClass.isEmpty())
	{
	    Log.fatal("init", "no speech back-end class in the command line (the \'--speech=\' option), Luwrain has no idea how to speak");
	    return false;
	}
	Object o;
	try {
	    o = Class.forName(backendClass).newInstance();
	}
	catch (InstantiationException e)
	{
	    Log.fatal("init", "an error while creating a new instance of class " + backendClass + ":InstantiationException:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	catch (IllegalAccessException e)
	{
	    Log.fatal("init", "an error while creating a new instance of class " + backendClass + ":IllegalAccessException:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	catch (ClassNotFoundException e)
	{
	    Log.fatal("init", "an error while creating a new instance of class " + backendClass + ":ClassNotFoundException:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}

	if (!(o instanceof org.luwrain.speech.BackEnd))
	{
	    Log.fatal("init", "created instance of class " + backendClass + " is not an instance of org.luwrain.speech.BackEnd");
	    return false;
	}
	speech = (org.luwrain.speech.BackEnd)o;
	final String errorMessage = speech.init(cmdLine);
	if (errorMessage != null)
	{
	    Log.fatal("init", "speech back-end initialization failed:" + errorMessage);
	    return false;
	}
	Log.debug("init", "speech back-end " + backendClass + " is initialized successfully");
	return true;
    }

    private boolean initEnvironmentSounds()
    {
	/*
	if (registry.getTypeOf(RegistryKeys.INSTANCE_DATA_DIR) != Registry.STRING)
	{
	    Log.error("init", "initialization of environment sounds is impossible, no proper registry value for " + RegistryKeys.INSTANCE_DATA_DIR);
	    return true;
	}
	File dataDir = new File(registry.getString(RegistryKeys.INSTANCE_DATA_DIR));
	setSoundFileName(dataDir, "event-not-processed", EnvironmentSounds.EVENT_NOT_PROCESSED);
	setSoundFileName(dataDir, "no-applications", EnvironmentSounds.NO_APPLICATIONS);
	setSoundFileName(dataDir, "startup", EnvironmentSounds.STARTUP);
	setSoundFileName(dataDir, "shutdown", EnvironmentSounds.SHUTDOWN);
	setSoundFileName(dataDir, "main-menu", EnvironmentSounds.MAIN_MENU);
	setSoundFileName(dataDir, "main-menu-item", EnvironmentSounds.MAIN_MENU_ITEM);
	setSoundFileName(dataDir, "main-menu-empty-line", EnvironmentSounds.MAIN_MENU_EMPTY_LINE);
	*/
	return true;
    }

    private void setSoundFileName(File dataDir,
				  String valueName,
				  int soundId)
    {
	/*
	String v = RegistryKeys.SOUNDS + "/" + valueName;
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
	*/
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
	/*
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
	*/
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

    private String getFirstCmdLineOption(String prefix)
    {
	if (prefix == null)
	    throw new NullPointerException("prefix may not be null");
	if (prefix.isEmpty())
	    throw new IllegalArgumentException("prefix may not be empty");
	if (cmdLine == null)
	    return null;
	for(String s: cmdLine)
	{
	    if (s == null)
		continue;
	    if (s.startsWith(prefix))
return s.substring(prefix.length());
	}
	return null;
    }

    public static void main(String[] args)
    {                    
	Init init = new Init();
	init.go(args);
    }
}
