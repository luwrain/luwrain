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
public class Init
{
    static public final String LOG_COMPONENT = "init";
    static private final String GREETING = "LUWRAIN (visit http://luwrain.org/doc/legal/ for legal notes)";
    static private final File DEBUG_FILE = new File(new File(System.getProperty("user.home")), "luwrain-debug.txt");

    static private final String  CMDARG_HELP = "--help";
    static private final String  CMDARG_PRINT_LANG = "--print-lang";
    static private final String  CMDARG_PRINT_DIRS = "--print-dirs";
    static private final String  CMDARG_CREATE_PROFILE = "--create-profile";
    static private final String  CMDARG_CREATE_PROFILE_IN = "--create-profile-in=";

    private final File dataDir;
    private final File userDataDir;
    private final File userHomeDir;
    private final String lang;

    private final CmdLine cmdLine;
    private final boolean standaloneMode;
    private final Registry registry;
    private final PropertiesRegistry props;

    private OperatingSystem os = null;
    private org.luwrain.base.Interaction interaction = null;

    private Init(String[] cmdLine, String lang, File dataDir)
    {
	NullCheck.notNullItems(cmdLine, "cmdLine");
	NullCheck.notEmpty(lang, "lang");
	NullCheck.notNull(dataDir, "dataDir");
	this.cmdLine = new CmdLine(cmdLine);
	this.lang = lang;
	this.dataDir = dataDir;
	this.userHomeDir = new File(System.getProperty("user.home"));
	final org.luwrain.core.properties.PropertiesFiles filesProps = new org.luwrain.core.properties.PropertiesFiles();
	filesProps.load(new File(dataDir, "properties"));
	final String standaloneValue = filesProps.getProperty("luwrain.standalone.enabled");
	if (standaloneValue != null && standaloneValue.trim().toLowerCase().equals("true"))
	{
	    Log.info(LOG_COMPONENT, "enabling standalone mode");
	    this.standaloneMode = true;
	    File tmpDir = null;
	    try {
		tmpDir = File.createTempFile("lwrtmpdatadir", "");
		tmpDir.delete();
		if (!tmpDir.mkdir())
		{
		    Log.fatal(LOG_COMPONENT, "unable to create temporary directory " + tmpDir.getAbsolutePath());
		    System.exit(1);
		}
	    }
	    catch(IOException e)
	    {
		Log.fatal(LOG_COMPONENT, "unable to create the temporary user data directory:" + e.getClass().getName() + ":" + e.getMessage());
		System.exit(1);
	    }
	    this.userDataDir = tmpDir;
	    final org.luwrain.core.properties.Basic basicProps = new org.luwrain.core.properties.Basic(dataDir, userDataDir, userHomeDir);
	    this.props = new PropertiesRegistry(new org.luwrain.base.PropertiesProvider[]{basicProps, filesProps});
	    this.registry = new org.luwrain.registry.mem.RegistryImpl();
	} else
	{
	    this.standaloneMode = false;
	    this.userDataDir = Checks.detectUserDataDir();
	    if (userDataDir == null)
	    {
		Log.fatal(LOG_COMPONENT, "unable to detect the user data directory");
		System.exit(1);
	    }
	    addExtensionsJarsToClassPath(new File(userDataDir, "extensions"));
	    filesProps.load(new File(userDataDir, "properties"));
	    final org.luwrain.core.properties.Basic basicProps = new org.luwrain.core.properties.Basic(dataDir, userDataDir, userHomeDir);
	    this.props = new PropertiesRegistry(new org.luwrain.base.PropertiesProvider[]{basicProps, filesProps});
	    this.registry = new org.luwrain.registry.fsdir.RegistryImpl(new File(this.userDataDir, "registry").toPath());
	}
    }

    private boolean init()
    {
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
	final String interactionClass = props.getProperty("luwrain.class.interaction");
	if (interactionClass.isEmpty())
	{
	    Log.fatal(LOG_COMPONENT, "unable to load interaction:no luwrain.class.interaction property among loaded properties");
	    return false;
	}
	interaction = (org.luwrain.base.Interaction)org.luwrain.util.ClassUtils.newInstanceOf(interactionClass, org.luwrain.base.Interaction.class);
	if (interaction == null)
	{
	    Log.fatal(LOG_COMPONENT, "Unable to create an instance of  the interaction class " + interactionClass);
	    return false;
	}
	if (!interaction.init(interactionParams,os))
	{
	    Log.fatal(LOG_COMPONENT, "interaction initialization failed");
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

    private boolean initOs()
    {
	final String osClass = props.getProperty("luwrain.class.os");
	if (osClass.isEmpty())
	{
	    Log.fatal(LOG_COMPONENT, "unable to load operating system interface:no luwrain.class.os property in loaded core properties");
	    return false;
	}
	os = (org.luwrain.base.OperatingSystem)org.luwrain.util.ClassUtils.newInstanceOf(osClass, org.luwrain.base.OperatingSystem.class);
	if (os == null)
	{
	    Log.fatal(LOG_COMPONENT, "unable to create a new instance of the operating system class " + osClass);
	    return false;
	}
	final InitResult initRes = os.init(props);
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
	if (cmdLine.used(CMDARG_HELP))
	{
	    System.out.println("Valid command line options are:");
	    System.out.println(CMDARG_HELP + " - print this help info and exit");
	    System.out.println(CMDARG_PRINT_LANG + " - print the chosen language and exit");

	    	    System.out.println(Checks.CMDARG_LANG + " - set the language to use");
		    	    System.out.println(CMDARG_PRINT_DIRS + " - print the detected values of the system directories and exit");
			    if (!standaloneMode)
			    {
	    System.out.println(CMDARG_CREATE_PROFILE + " - generate the user profile directory in its default location and exit");
	    System.out.println(CMDARG_CREATE_PROFILE_IN + "<DESTDIR> - generate the user profile directory in <DESTDIR> and exit");
			    }
	    System.exit(0);
	}

	if (cmdLine.used(CMDARG_PRINT_LANG))
	{
	    System.out.println("Chosen language: " + lang);
	    System.exit(0);
	}

	if (cmdLine.used(CMDARG_PRINT_DIRS))
	{
	    System.out.println("Data: " + dataDir.getAbsolutePath());
	    System.out.println("User profile: " + userDataDir.getAbsolutePath());
	    System.out.println("User home: " + userHomeDir.getAbsolutePath());
	    System.exit(0);
	}

	if (!standaloneMode && cmdLine.getArgs(CMDARG_CREATE_PROFILE_IN).length > 0)
	{
	    final String[] destDirs = cmdLine.getArgs((CMDARG_CREATE_PROFILE_IN));
	    try {
		for(String d: destDirs)
		{
		    final File destDir = new File(d);
		    System.out.println("Creating user profile in " + destDir.getAbsolutePath());
		    UserProfile.createUserProfile(dataDir, destDir, lang);
		}
		System.exit(0);
	    }
	    catch(IOException e)
	    {
		System.err.println("ERROR: " + e.getClass().getName() + ":" + e.getMessage());
		System.exit(1);
	    }
	}

	if (!standaloneMode && cmdLine.used(CMDARG_CREATE_PROFILE))
	{
	    try {
		System.out.println("Creating user profile in " + userDataDir.getAbsolutePath());
		UserProfile.createUserProfile(dataDir, userDataDir, lang);
		System.exit(0);
	    }
	    catch(IOException e)
	    {
		System.err.println("ERROR: " + e.getClass().getName() + ":" + e.getMessage());
		System.exit(1);
	    }
	}

	try {
		    Log.info(LOG_COMPONENT, "starting LUWRAIN: Java " + System.getProperty("java.version") + " by " + System.getProperty("java.vendor") + " (installed in " + System.getProperty("java.home") + ")");
		    if (standaloneMode)
		    {

						UserProfile.createUserProfile(dataDir, userDataDir, lang);
		    } else 
		    if (!Checks.isProfileInstalled(userDataDir))
		    {
			Log.debug(LOG_COMPONENT, "generating the initial content of the user data directory " + userDataDir.getAbsolutePath());
			UserProfile.createUserProfile(dataDir, userDataDir, lang);
		    } else
			Log.debug(LOG_COMPONENT, "the user data directory " + userDataDir.getAbsolutePath() + " considered properly prepared");
		    final boolean initRes = init();
		    if (initRes)
		new Core(
				cmdLine, registry, os, interaction, 
				props, lang).run();
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
	    System.exit(1);
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
	System.out.println(GREETING);
	System.out.println();
	setUtf8();
	addJarsToClassPath(new File("jar"));
	addJarsToClassPath(new File("lib"));
	final String lang = Checks.detectLang(new CmdLine(args));
	if (lang.isEmpty())
	{
	    Log.fatal(LOG_COMPONENT, "unable to select a language to use");
	    System.exit(1);
	}
	new Init(args, lang, new File("data")).start();
    }

    static private void addExtensionsJarsToClassPath(File extensionsDir)
    {
	NullCheck.notNull(extensionsDir, "extensionsDir");
	final File[] subdirs = extensionsDir.listFiles();
	if (subdirs == null)
	    return;
	for(File s: subdirs)
	{
	    if (!s.isDirectory())
		continue;
	    final File jarsDir = new File(s, "jar");
	    if (!jarsDir.isDirectory())
		continue;
	    Log.debug(LOG_COMPONENT, "registering extension jars from the directory " + jarsDir.getAbsolutePath());
	    addJarsToClassPath(jarsDir);
	}
    }

    static private void addJarsToClassPath(File file)
    {
	NullCheck.notNull(file, "file");
	try {
	    final File[] files = file.listFiles();
	    if (files == null)
		return;
	    for(File f: files)
		if (!f.isDirectory() && f.getName().toLowerCase().trim().endsWith(".jar"))
		{
		    final java.net.URL url = f.toURI().toURL();
		    ClassPath.addUrl(url);
		}
	}
	catch(IOException e)
	{
	    Log.error(LOG_COMPONENT, "unable to add to the classpath the directory " + file.getAbsolutePath() + ":" + e.getClass().getName() + ":" + e.getMessage());
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
