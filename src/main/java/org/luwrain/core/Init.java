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
    static private final String  PREFIX_DATA_DIR = "--data-dir=";
    static private final String  PREFIX_USER_HOME_DIR = "--user-home-dir=";
    static private final String  PREFIX_USER_DATA_DIR = "--user-data-dir=";
    static private final String  PREFIX_LANG= "--lang=";

    private final CmdLine cmdLine;
    private final CoreProperties coreProps = new CoreProperties();
    private Path dataDir = null;
    private Path userDataDir = null;
    private Path userHomeDir = null;
    private String lang;

    private Registry registry;
    private Interaction interaction;
    private OperatingSystem os;

    private Init(String[] cmdLine)
    {
	NullCheck.notNullItems(cmdLine, "cmdLine");
	this.cmdLine = new CmdLine(cmdLine);
    }

    private String getSystemProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
			   switch(propName)
			   {
			   case "luwrain.lang":
			       return lang;
			   default:
			       return coreProps.getProperty(propName);
			   }
    }

    private Path getSystemPath(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	switch(propName)
	{
	case "luwrain.dir.userhome":
	    return userHomeDir;
	case "luwrain.dir.data":
	    return dataDir;
	case "luwrain.dir.scripts":
	    return dataDir.resolve("scripts");
	case "luwrain.dir.properties":
	    return dataDir.resolve("properties");
	case "luwrain.dir.sounds":
	    return dataDir.resolve("sounds");
	default:
	    return coreProps.getPathProperty(propName);
	}
    }
    private boolean init()
    {
	if (!processCmdLine())
	    return false;
	coreProps.load(dataDir.resolve("properties"), userDataDir.resolve("properties"));
	registry = new org.luwrain.registry.fsdir.RegistryImpl(userDataDir.resolve("registry").toString());
	if (!initOs())
	    return false;
	final InteractionParamsLoader interactionParams = new InteractionParamsLoader();
	interactionParams.loadFromRegistry(registry);
	final Object o;
	try {
	    final String interactionClass = coreProps.getProperty("luwrain.class.interaction");
	    if (interactionClass.isEmpty())
	    {
		Log.fatal("init", "unable to load interaction:no luwrain.class.interaction property among loaded properties");
		return false;
	    }
		o = Class.forName(interactionClass).newInstance();
	}
	catch(Exception e)
	{
	    Log.fatal("init", "Unable to create an instance of  interaction class:" + e.getMessage());
	    return false;
	}
	Log.debug("init", "using interaction of class " + o.getClass().getName());
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

	return true;
    }

    private boolean initOs()
    {
	final String osClass = coreProps.getProperty("luwrain.class.os");
	if (osClass.isEmpty())
	{
	    Log.fatal("init", "unable to load operating system interface:no luwrain.class.os property in loaded core properties");
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
	if (!os.init(dataDir.toString()))
	{
	    Log.fatal("init", "unable to initialize operating system through " + os.getClass().getName());
	    return false;
	}
	Log.debug("init", "operating system functions (" + osClass + " class) are initialized successfully");
	return true;
    }

    private boolean processCmdLine()
    {
String userHomeStr = cmdLine.getFirstArg(PREFIX_USER_HOME_DIR);
if (userHomeStr == null)
    userHomeStr = System.getProperty("user.home");
if (userHomeStr == null || userHomeStr.isEmpty())
{
    Log.fatal("init", "unable to find user home directory (useful command line option is \'" + PREFIX_USER_HOME_DIR + "\')");
    return false;
}
userHomeDir = Paths.get(userHomeStr);
final String userDataStr = cmdLine.getFirstArg(PREFIX_USER_DATA_DIR);
if (userDataStr == null || userDataStr.isEmpty())
{
    Log.fatal("init", "unable to find user data directory (useful command line option is \'" + PREFIX_USER_DATA_DIR + "\')");
    return false;
}
userDataDir = Paths.get(userDataStr);
final String dataStr = cmdLine.getFirstArg(PREFIX_DATA_DIR);
if (dataStr == null || dataStr.isEmpty())
{
    Log.fatal("init", "unable to find data directory (useful command line option is \'" + PREFIX_DATA_DIR + "\')");
    return false;
}
dataDir = Paths.get(dataStr);
lang = cmdLine.getFirstArg(PREFIX_LANG);
if (lang == null)
    lang = "";
Log.debug("init", "data dir:" + dataDir.toString());
Log.debug("init", "user data dir:" + userDataDir.toString());
Log.debug("init", "user home dir:" + userHomeDir.toString());
return true;
    }

    private void start()
    {
	Log.info("init", "starting LUWRAIN: Java " + System.getProperty("java.version") + " by " + System.getProperty("java.vendor") + " (installed in " + System.getProperty("java.home") + ")");
	final boolean initRes = init();
	if (initRes)
	    new Environment(cmdLine, registry, os, interaction, 
			    new org.luwrain.base.CoreProperties(){
				@Override public String getProperty(String propName)
				{
				    NullCheck.notNull(propName, "propName");
				    return getSystemProperty(propName);
				}
				@Override public Path getPathProperty(String propName)
				{
				    NullCheck.notEmpty(propName, "propName");
				    return getSystemPath(propName);
				}
			    }, lang).run();
	if (interaction != null)
	{
	    Log.debug("init", "closing interaction");
	    interaction.close();
	}
	if (initRes)
	Log.info("init", "exiting LUWRAIN normally");
	System.exit(initRes?0:1);
    }

    /**
     * The main entry point to launch LUWRAIN.
     *
     * @param args The command line arguments mentioned by user on virtual machine launch
     */
    static public void main(String[] args)
    {                    
	new Init(args).start();
    }

    /*
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
    */
}
