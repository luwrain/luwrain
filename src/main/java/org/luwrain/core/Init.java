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

import java.util.*;
import java.io.*;
//import java.nio.file.*;
import java.lang.reflect.Field;
import java.nio.charset.Charset;


import org.luwrain.base.OperatingSystem;
import org.luwrain.core.init.*;

/**
 * The main class to launch LUWRAIN. All basic initialization is
 * implemented here (interaction, registry, operating system etc),
 * including first processing of command line options. This class
 * contains {@code main()} static method which should be used to launch
 * Java virtual machine with LUWRAIN.
 */
public class Init implements org.luwrain.base.CoreProperties
{
    static private final String LOG_COMPONENT = "init";

    static private final File DEBUG_FILE = new File(new File(System.getProperty("user.home")), "luwrain-debug.txt");

    static private final String  PREFIX_PKG_LAUNCH = "--pkg-launch";
    static private final String  PREFIX_DATA_DIR = "--data-dir=";
    static private final String  PREFIX_USER_HOME_DIR = "--user-home-dir=";
    static private final String  PREFIX_USER_DATA_DIR = "--user-data-dir=";
    static private final String  PREFIX_LANG= "--lang=";

    //    static private final String ENV_APP_DATA = "APPDATA";
    //    static private final String ENV_USER_PROFILE = "USERPROFILE";
    //    static private final String DEFAULT_USER_DATA_DIR_WINDOWS = "Luwrain";
    //    static private final String DEFAULT_USER_DATA_DIR_LINUX = ".luwrain";

    private final CmdLine cmdLine;
    private final CoreProperties coreProps = new CoreProperties();
    private final File dataDir;
    private final File userDataDir;
    private final File userHomeDir;
    private final String lang;

    private Registry registry;
    private org.luwrain.base.Interaction interaction;
    private OperatingSystem os;

    private Init(String[] cmdLine, String lang,
		 File dataDir, File userDataDir)
    {
	NullCheck.notNullItems(cmdLine, "cmdLine");
	NullCheck.notEmpty(lang, "lang");
	NullCheck.notNull(dataDir, "dataDir");
	NullCheck.notNull(userDataDir, "userDataDir");
	this.cmdLine = new CmdLine(cmdLine);
	this.lang = lang;
	this.dataDir = dataDir;
	this.userDataDir = userDataDir;
	this.userHomeDir = new File(System.getProperty("user.home"));
    }

    public String getProperty(String propName)
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

    public File getFileProperty(String propName)
    {
	NullCheck.notEmpty(propName, "propName");
	switch(propName)
	{
	case "luwrain.dir.userhome":
	    return userHomeDir;
	case "luwrain.dir.data":
	    return dataDir;
	case "luwrain.dir.scripts":
	    return new File(dataDir, "scripts");
	case "luwrain.dir.properties":
	    return new File(dataDir, "properties");
	case "luwrain.dir.sounds":
	    return new File(dataDir, "sounds");
	case "luwrain.dir.userdata":
	    return userDataDir;
	case "luwrain.dir.appdata":
	    return new File(userDataDir, "app");
	default:
	    return coreProps.getFileProperty(propName);
	}
    }

    private boolean init()
    {
	coreProps.load(new File(dataDir, "properties"), new File(userDataDir, "properties"));
	registry = new org.luwrain.registry.fsdir.RegistryImpl(new File(userDataDir, "registry").toPath());

	    //time zone
	{
	    final Settings.DateTime sett = Settings.createDateTime(registry);
	    final String value = sett.getTimeZone("");
	    if (!value.trim().isEmpty())
	    {
		final TimeZone timeZone = TimeZone.getTimeZone(value.trim());
		if (timeZone != null)
		{
		Log.debug(LOG_COMPONENT, "Setting time zone to " + value.trim());
		TimeZone.setDefault(timeZone);
		} else
		    Log.warning(LOG_COMPONENT, "time zone " + value.trim() + " is unknown");
	    }
	}

	if (!initOs())
	    return false;
	final InteractionParamsLoader interactionParams = new InteractionParamsLoader();
	interactionParams.loadFromRegistry(registry);
	final Object o;
	try {
	    final String interactionClass = coreProps.getProperty("luwrain.class.interaction");
	    if (interactionClass.isEmpty())
	    {
		Log.fatal(LOG_COMPONENT, "unable to load interaction:no luwrain.class.interaction property among loaded properties");
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
	if (!(o instanceof org.luwrain.base.Interaction))
	{
	    Log.fatal("init", "The instance of " + o.getClass().getName() + " isn\'t an instance of org.luwrain.core.Interaction");
	    return false;
	}
	interaction = (org.luwrain.base.Interaction)o;
	if (!interaction.init(interactionParams,os))
	{
	    Log.fatal("init", "interaction initialization failed");
	    return false;
	}

	//network
	final Settings.Network network = Settings.createNetwork(registry);
	System.getProperties().put("socksProxyHost", network.getSocksProxyHost(""));
	System.getProperties().put("socksProxyPort", network.getSocksProxyPort(""));
	System.getProperties().put("http.proxyHost", network.getHttpProxyHost(""));
	System.getProperties().put("http.proxyPort", network.getHttpProxyPort(""));
	System.getProperties().put("http.proxyUser", network.getHttpProxyUser(""));
	System.getProperties().put("http.proxyPassword",network.getHttpProxyPassword("") );

	//OK!
	return true;
    }

    /*
    private boolean initRegistry()
    {
	return true;
    }
    */

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
	os = (org.luwrain.base.OperatingSystem)o;
	final InitResult initRes = os.init(this);
	if (initRes == null || !initRes.isOk())
	{
	    if (initRes != null)
	    Log.fatal(LOG_COMPONENT, "unable to initialize operating system with " + os.getClass().getName() + ":" + initRes.toString()); else
	    Log.fatal(LOG_COMPONENT, "unable to initialize operating system with " + os.getClass().getName());
	    return false;
	}
	Log.debug(LOG_COMPONENT, "OS (" + osClass + ") initialized successfully");
	return true;
    }

    private void start()
    {
	try {
	    Log.info(LOG_COMPONENT, "starting LUWRAIN: Java " + System.getProperty("java.version") + " by " + System.getProperty("java.vendor") + " (installed in " + System.getProperty("java.home") + ")");
	    final boolean initRes = init();
	    if (initRes)
		new Environment(
				cmdLine, registry, os, interaction, 
				this, //core properties
				lang).run();
	    if (interaction != null)
	    {
		Log.debug(LOG_COMPONENT, "closing interaction");
		interaction.close();
	    }
	    if (initRes)
		Log.info(LOG_COMPONENT, "exiting LUWRAIN normally");
	    System.exit(initRes?0:1);
	}
	catch(Throwable e)
	{
	    Log.fatal(LOG_COMPONENT, "terminating LUWRAIN very abnormally due to unexpected exception:" + e.getClass().getName() + ":" + e.getMessage());
	    e.printStackTrace();
	}
    }

    /**
     * The main entry point to launch LUWRAIN.
     *
     * @param args The command line arguments mentioned by user on virtual machine launch
     */
    static public void main(String[] args) throws IOException
    {
	org.luwrain.app.console.App.installListener();
	if (DEBUG_FILE.exists() && !DEBUG_FILE.isDirectory())
	{
	    final PrintStream log = new PrintStream(new BufferedOutputStream(new FileOutputStream(DEBUG_FILE)), true);
	    System.setOut(log);
	    System.setErr(log);
	} else
	    Log.enableBriefMode();
	System.out.println("LUWRAIN (see http://luwrain.org/doc/legal/ for legal notes)");
	System.out.println();
	setUtf8();
	addJarsToClassPath("jar");
	addJarsToClassPath("lib");

		final File userDataDir = Checks.detectUserDataDir();
		
	if (userDataDir == null)
	    System.exit(1);
	new Init(args, "ru", new File("data"), userDataDir).start();//FIXME:lang
    }

    static private void addJarsToClassPath(String dirName)
    {
	NullCheck.notEmpty(dirName, "dirName");
	try {
	    for(File f: new File(dirName).listFiles())
		if (f.getName().toLowerCase().trim().endsWith(".jar"))
		{
		    final java.net.URL url = f.toURI().toURL();
		    ClassPath.addUrl(url);
		}
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to add to the classpath the directory " + dirName + ":" + e.getClass().getName() + ":" + e.getMessage());
	}
    }

    static private void setUtf8()
    {
	Log.debug("init", "using UTF-8, while default system charset was " + System.getProperty("file.encoding"));
	System.setProperty("file.encoding","UTF-8");
	Field charset;
	try {
	    charset=Charset.class.getDeclaredField("defaultCharset");
	    charset.setAccessible(true);
	    charset.set(null,null);
	} catch(Exception e)
	{
	    e.printStackTrace();
	}
    }
}
