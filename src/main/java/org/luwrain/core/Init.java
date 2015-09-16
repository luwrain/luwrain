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

import java.util.*;
import java.util.jar.*;
import java.io.*;

import org.luwrain.speech.BackEnd;
import org.luwrain.os.OperatingSystem;


class Init
{
    static private final String  PREFIX_INTERACTION = "--interaction=";
    static private final String  PREFIX_REGISTRY_DIR = "--registry-dir=";
    static private final String  PREFIX_DATA_DIR = "--data-dir=";
    static private final String  PREFIX_USER_HOME_DIR = "--user-home-dir=";
    static private final String  PREFIX_SPEECH= "--speech=";
    static private final String  PREFIX_OS= "--os=";
    static private final String  PREFIX_LANG= "--lang=";

    private String[] cmdLine;
    private Registry registry;
    private Interaction interaction;
    private OperatingSystem os;
    private org.luwrain.speech.BackEnd speech;
    private LaunchContext launchContext;

    public void go(String[] args)
    {
	this.cmdLine = args;
	Log.debug("init", "command line has " + cmdLine.length + " arguments:");
	for(String s: cmdLine)
	    Log.debug("init", s);
	if (init())
	    new Environment(cmdLine, registry, speech, os, interaction, launchContext).run();
	interaction.close();
	System.exit(0);
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

	if (!initOs())
	    return false;
	if (!initSpeech())
	    return false;

	//Interaction;
	final String interactionClass = getFirstCmdLineOption(PREFIX_INTERACTION);
	InteractionParamsLoader interactionParams = new InteractionParamsLoader();
	interactionParams.loadFromRegistry(registry);
	if (!interactionParams.backend.equals("awt"))
	{
	    Log.fatal("init", "unsupported interaction type \'" + interactionParams.backend + "\'");
	    return false;
	}
	if (interactionClass != null && !interactionClass.isEmpty())
	{
	    Object o;
	    try {
		o = Class.forName(interactionClass).newInstance();
	    }
	    catch(Exception e)
	    {
		Log.fatal("init", "error occurred while creating an instance of " + interactionClass + " for interaction:" + e.getMessage());
		e.printStackTrace();
		return false;
	    }
	    if (!(o instanceof Interaction))
	    {
		Log.fatal("init", "The instance of " + interactionClass + " isn\'t an instance of org.luwrain.core.Interaction");
		return false;
	    }
	    interaction = (Interaction)o;
	    Log.info("init", "using the instance of " + interactionClass + " for interaction");
	} else
	{
	    interaction = new org.luwrain.interaction.AwtInteraction();
	    Log.info("init", "using default interaction");
	}
	if (!interaction.init(interactionParams))
	{
	    Log.fatal("init", "interaction initialization failed");
	    return false;
	}

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

    private boolean initOs()
    {
	final String osClass = getFirstCmdLineOption(PREFIX_OS);
	if (osClass == null || osClass.isEmpty())
	{
	    Log.fatal("init", "no operating system class in the command line (the \'" + PREFIX_OS + "\' option)");
	    return false;
	}
	Object o;
	try {
	    o = Class.forName(osClass).newInstance();
	}
	catch (InstantiationException e)
	{
	    Log.fatal("init", "an error while creating a new instance of class " + osClass + ":InstantiationException:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	catch (IllegalAccessException e)
	{
	    Log.fatal("init", "an error while creating a new instance of class " + osClass + ":IllegalAccessException:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	catch (ClassNotFoundException e)
	{
	    Log.fatal("init", "an error while creating a new instance of class " + osClass + ":ClassNotFoundException:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	if (!(o instanceof OperatingSystem))
	{
	    Log.fatal("init", "created instance of class " + osClass + " is not an instance of org.luwrain.os.OperatingSystem");
	    return false;
	}
	os = (org.luwrain.os.OperatingSystem)o;
	final String errorMessage = os.init();
	if (errorMessage != null)
	{
	    Log.fatal("init", "operating system initialization failed:" + errorMessage);
	    return false;
	}
	Log.debug("init", "operating system functions (" + osClass + " class) are initialized successfully");
	return true;
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
	new Init().go(args);
    }
}
