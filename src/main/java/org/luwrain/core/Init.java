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

import org.luwrain.speech.BackEnd;
import org.luwrain.sounds.EnvironmentSounds;

class Init
{
    //    private static final String  PREFIX_CONF_LIST = "--conf-list=";
    private static final String  PREFIX_REGISTRY_DIR = "--registry-dir=";
    private static final String  PREFIX_DATA_DIR = "--data-dir=";
    private static final String  PREFIX_USER_HOME_DIR = "--user-home-dir=";
    private static final String  PREFIX_SPEECH= "--speech=";
    private static final String  PREFIX_LANG= "--lang=";

    private String[] cmdLine;
    private Registry registry;
    private Interaction interaction;
    private org.luwrain.speech.BackEnd speech;
    private LaunchContext launchContext;

    public void go(String[] args)
    {
	this.cmdLine = args;
	Log.debug("init", "command line has " + cmdLine.length + " arguments:");
	for(String s: cmdLine)
	    Log.debug("init", s);
	if (init())
	    new Environment(cmdLine, registry, speech, interaction, launchContext).run();
	exit();
    }

    private boolean init()
    {
	//Registry;
	final String regDirPath = getFirstCmdLineOption(PREFIX_REGISTRY_DIR);
	if (regDirPath == null || regDirPath.isEmpty())
	{
	    Log.fatal("init", "no \'" + PREFIX_REGISTRY_DIR + "\' command line option, Luwrain don\'t know where to get registry data");
	    return false;
	}
	File regDir = new File(regDirPath);
	if (!regDir.isAbsolute() || !regDir.isDirectory())
	{
	    Log.fatal("init", "registry location \'" + regDirPath + "\' isn\'t a directory or isn\'t an absolute path");
	    return false;
	}
	registry = new org.luwrain.registry.fsdir.RegistryImpl(regDir.getAbsolutePath());

	//Launch context;
	final String dataDirPath = getFirstCmdLineOption(PREFIX_DATA_DIR);
	if (dataDirPath == null || dataDirPath.isEmpty())
	{
	    Log.fatal("init", "no command line option \'" + PREFIX_DATA_DIR + "\', Luwrain doesn\'t know where its data is");
	    return false;
	}
	final File dataDir = new File(dataDirPath);
	if (!dataDir.isDirectory() || !dataDir.isAbsolute())
	{
	    Log.fatal("init", "data location \'" + dataDirPath + "\' isn\'t a directory or isn\'t an absolute path");
	    return false;
	}
	final String userHomeDirPath = getFirstCmdLineOption(PREFIX_USER_HOME_DIR);
	if (userHomeDirPath == null || userHomeDirPath.isEmpty())
	{
	    Log.fatal("init", "no command line option \'" + PREFIX_USER_HOME_DIR + "\', Luwrain doesn\'t know where user home files should be");
	    return false;
	}
	final File userHomeDir = new File(userHomeDirPath);
	if (!userHomeDir.isDirectory() || !userHomeDir.isAbsolute())
	{
	    Log.fatal("init", "user home location \'" + userHomeDirPath + "\' isn\'t a directory or isn\'t an absolute path");
	    return false;
	}
	final String lang = getFirstCmdLineOption(PREFIX_LANG);
	if (lang == null || lang.isEmpty())
	{
	    Log.fatal("init", "no chosen language, use command line option \'" + PREFIX_LANG + "\'");
return false;
	}
launchContext = new LaunchContext(dataDir.getAbsolutePath(), userHomeDir.getAbsolutePath(), lang);

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
