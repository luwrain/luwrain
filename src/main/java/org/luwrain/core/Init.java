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
import java.io.*;
import java.nio.file.*;

import org.luwrain.os.OperatingSystem;

/**
 * The main class to launch LUWRAIN. All basic initialization is
 * implemented here (interaction, registry, operating system etc),
 * including first processing of command line options. This class
 * contains {@code main()} static method which should be used to launch
 * Java virtual machine with LUWRAIN.
 */
public class Init
{
    static private final String  PREFIX_INTERACTION = "--interaction=";
    static private final String  PREFIX_REGISTRY_DIR = "--registry-dir=";
    static private final String  PREFIX_DATA_DIR = "--data-dir=";
    static private final String  PREFIX_USER_HOME_DIR = "--user-home-dir=";
    static private final String  PREFIX_OS= "--os=";
    static private final String  PREFIX_LANG= "--lang=";

    static private final String DEFAULT_LANG = "en";
    static private final String DEFAULT_INTERACTION_CLASS = "org.luwrain.interaction.javafx.JavaFxInteraction";

    private String[] cmdLine;
    private Registry registry;
    private Interaction interaction;
    private OperatingSystem os;
    private final HashMap<String, Path> paths = new HashMap<String, Path>();
    private String lang;

    private boolean init()
    {
	if (!initRegistry())
	    return false;
	if (!initPathsAndLang())
	    return false;
	if (!initOs())
	    return false;
	final InteractionParamsLoader interactionParams = new InteractionParamsLoader();
	interactionParams.loadFromRegistry(registry);
	Object o;
	try {
	    final String interactionClass = getFirstCmdLineOption(PREFIX_INTERACTION);
	    if (interactionClass != null && !interactionClass.isEmpty())
		o = Class.forName(interactionClass).newInstance(); else
		o = Class.forName(DEFAULT_INTERACTION_CLASS).newInstance();
	}
	catch(Exception e)
	{
	    Log.fatal("init", "Unable to create an instance of  interaction class:" + e.getMessage());
	    e.printStackTrace();
	    return false;
	}
	Log.info("init", "using interaction of class " + o.getClass().getName());
	if (!(o instanceof Interaction))
	{
	    Log.fatal("init", "The instance of " + o.getClass().getName() + " isn\'t an instance of org.luwrain.core.Interaction");
	    return false;
	}
	interaction = (Interaction)o;
	if (!interaction.init(interactionParams,os))
	{
	    Log.fatal("init", "interaction initialization failed");
	    return false;
	}
	return true;
    }

    private boolean initRegistry()
    {
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
	return true;
    }

    private boolean initPathsAndLang()
    {
	final String dataDirArg = getFirstCmdLineOption(PREFIX_DATA_DIR);
	if (dataDirArg == null || dataDirArg.isEmpty())
	{
	    Log.fatal("init", "no command line option \'" + PREFIX_DATA_DIR + "\', Luwrain doesn\'t know where its data is");
	    return false;
	}
	final Path dataDirPath = Paths.get(dataDirArg);
	if (!Files.isDirectory(dataDirPath) || !dataDirPath.isAbsolute())
	{
	    Log.fatal("init", "data location \'" + dataDirArg + "\' isn\'t a directory or isn\'t an absolute path");
	    return false;
	}
	String userHomeDir = getFirstCmdLineOption(PREFIX_USER_HOME_DIR);
	if (userHomeDir == null || userHomeDir.isEmpty())
	{
	    userHomeDir = System.getProperty("user.home");
	    Log.debug("init", "using user home directory path from virtual machine:" + userHomeDir);
	}
	final Path userHomeDirPath = Paths.get(userHomeDir);
	if (!Files.isDirectory(userHomeDirPath) || !userHomeDirPath.isAbsolute())
	{
	    Log.fatal("init", "user home location \'" + userHomeDirPath + "\' isn\'t a directory or isn\'t an absolute path");
	    return false;
	}
	lang = getFirstCmdLineOption(PREFIX_LANG);
	if (lang == null || lang.isEmpty())
	    lang = DEFAULT_LANG;
	paths.put("luwrain.dir.userhome", userHomeDirPath);
	paths.put("luwrain.dir.data", dataDirPath);
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
	if (!os.init(paths.get("luwrain.dir.data").toString()))
	{
	    Log.fatal("init", "unable to initialize operating system through " + os.getClass().getName());
	    return false;
	}
	Log.debug("init", "operating system functions (" + osClass + " class) are initialized successfully");
	return true;
    }

    private void start(String[] args)
    {
	NullCheck.notNull(args, "args");
	this.cmdLine = args;
	Log.debug("init", "command line has " + cmdLine.length + " arguments:");
	for(String s: cmdLine)
	    Log.debug("init", s);
	if (init())
	    new Environment(cmdLine, registry, os, interaction, paths, lang).run();
	Log.debug("init", "closing interaction");
	interaction.close();
	Log.info("init", "exiting LUWRAIN normally");
	System.exit(0);
    }

    /**
     * The main entry point to launch LUWRAIN.
     *
     * @param args The command line arguments mentioned by user on virtual machine launch
     */
    static public void main(String[] args)
    {                    
	new Init().start(args);
    }

    private String getFirstCmdLineOption(String prefix)
    {
	NullCheck.notNull(prefix, "prefix");
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
}
