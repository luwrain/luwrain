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
    static private final String  PREFIX_PKG_LAUNCH = "--pkg-launch";
    static private final String  PREFIX_DATA_DIR = "--data-dir=";
    static private final String  PREFIX_USER_HOME_DIR = "--user-home-dir=";
    static private final String  PREFIX_USER_DATA_DIR = "--user-data-dir=";
    static private final String  PREFIX_LANG= "--lang=";

    static private final String ENV_APP_DATA = "APPDATA";
    static private final String ENV_USER_PROFILE = "USERPROFILE";
    static private final String DEFAULT_USER_DATA_DIR_WINDOWS = "Luwrain";
    static private final String DEFAULT_USER_DATA_DIR_LINUX = ".luwrain";

    private final CmdLine cmdLine;
    private final CoreProperties coreProps = new CoreProperties();
    private final Path dataDir;
    private final Path userDataDir;
    private final Path userHomeDir;
    private final String lang;

    private Registry registry;
    private Interaction interaction;
    private OperatingSystem os;

    private Init(String[] cmdLine, String lang,
		 Path dataDir, Path userDataDir)
    {
	NullCheck.notNullItems(cmdLine, "cmdLine");
	NullCheck.notEmpty(lang, "lang");
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(userDataDir, "userDataDir");
	this.cmdLine = new CmdLine(cmdLine);
	this.lang = lang;
	this.dataDir = dataDir;
	this.userDataDir = userDataDir;
	this.userHomeDir = Paths.get(System.getProperty("user.home"));
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
	case "luwrain.dir.userdata":
	    return userDataDir;
	case "luwrain.dir.appdata":
	    return userDataDir.resolve("app");
	default:
	    return coreProps.getPathProperty(propName);
	}
    }
    private boolean init()
    {
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
    static public void main(String[] args) throws IOException
    {                    
	addJarsToClassPath("jar");
	addJarsToClassPath("lib");
	final Path userDataDir = prepareUserDataDir(); 
	if (userDataDir == null)
	    System.exit(1);
	new Init(args, "ru", Paths.get("data"), userDataDir).start();
    }

    static private Path prepareUserDataDir()
    {
	final Path userDataDir = detectUserDataDir();
	Log.debug("init", "user data directory detected as " + userDataDir.toString());
	final Path registryDir = userDataDir.resolve("registry");
	final Path sqliteDir = userDataDir.resolve("sqlite");
	try {
	    if (!Files.exists(registryDir))
	    {
		copyDir(Paths.get("registry"), registryDir);
		copyDir(Paths.get("i18n", "ru"), registryDir);
		createRegistryFiles(registryDir.resolve("org"));
	    }
	    if (!Files.exists(sqliteDir))
		copyDir(Paths.get("sqlite"), sqliteDir);
	    Files.createDirectories(userDataDir.resolve("extensions"));
	    Files.createDirectories(userDataDir.resolve("properties"));
	}
	catch(IOException e)
	{
	    Log.fatal("init", "unable to prepare user data directory:" + e.getClass().getName() + ":" + e.getMessage());
	    return null;
	}
	Log.debug("init", "user data directory prepared");
	return userDataDir;
    }

    static private void createRegistryFiles(Path dest) throws IOException
    {
	NullCheck.notNull(dest, "dest");
	if (!Files.exists(dest.resolve("strings.txt")))
	    Files.createFile(dest.resolve("strings.txt"));
	if (!Files.exists(dest.resolve("integers.txt")))
	    Files.createFile(dest.resolve("integers.txt"));
	if (!Files.exists(dest.resolve("booleans.txt")))
	    Files.createFile(dest.resolve("booleans.txt"));
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dest)) {
		for (Path p : directoryStream) 
		{
		    final Path newDest = dest.resolve(p.getFileName());
		    if (Files.isDirectory(newDest))
			createRegistryFiles(newDest);
		}
	    }
    }


    static private void copyDir(Path fromDir, Path dest) throws IOException
    {
	NullCheck.notNull(fromDir, "fromDir");
	NullCheck.notNull(dest, "dest");
	Files.createDirectories(dest);
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(fromDir)) {
		for (Path p : directoryStream) 
		{
		    final Path newDest = dest.resolve(p.getFileName());
		    if (Files.isDirectory(p))
		    {
			Files.createDirectories(newDest);
			copyDir(p, newDest);
		    } else
		    {
			final InputStream is = Files.newInputStream(p);
			try {
			    Files.copy(is, newDest, StandardCopyOption.REPLACE_EXISTING);
			}
			finally {
			    is.close();
			}
		    }
		}
	    } 
    }

    static private Path detectUserDataDir()
    {
	if(System.getenv().containsKey(ENV_APP_DATA) && !System.getenv().get(ENV_APP_DATA).trim().isEmpty())
	{
	    final Path appData = Paths.get(System.getenv().get(ENV_APP_DATA));
	    return appData.resolve(DEFAULT_USER_DATA_DIR_WINDOWS);
	}
	if(System.getenv().containsKey(ENV_USER_PROFILE) && !System.getenv().get(ENV_USER_PROFILE).trim().isEmpty())
	{
	    final Path userProfile = Paths.get(System.getenv().get(ENV_USER_PROFILE));
	    return userProfile.resolve("Local Settings").resolve("Application Data").resolve(DEFAULT_USER_DATA_DIR_WINDOWS);
	}
	return Paths.get(System.getProperty("user.home")).resolve(DEFAULT_USER_DATA_DIR_LINUX);
    }

    static private void addJarsToClassPath(String dirName)
    {
	NullCheck.notEmpty(dirName, "dirName");
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(dirName))) {
		for (Path p : directoryStream) 
		{
		    final java.net.URL url = p.toUri().toURL();
		    ClassPath.addUrl(url);
		}
	    }
	catch(IOException e)
	{
	}
    }
}
