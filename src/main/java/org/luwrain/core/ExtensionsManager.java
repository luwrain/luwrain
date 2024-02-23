/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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
import java.util.jar.*;
import java.io.*;

import static org.luwrain.core.Base.*;
import static org.luwrain.core.NullCheck.*;

public final class ExtensionsManager implements AutoCloseable
{
    static final String
	EXTENSIONS_LIST_PREFIX = "--extensions=";

        static final class Entry
    {
	final Extension ext;
	final Luwrain luwrain;
	final String id;
	Entry(Extension ext, Luwrain luwrain)
	{
	    notNull(ext, "ext");
	    notNull(luwrain, "luwrain");
	    this.ext = ext;
	    this.luwrain = luwrain;
	    this.id = java.util.UUID.randomUUID().toString();
	}
    }

    private final Base base;
    private final InterfaceManager interfaces;
    List<Entry> extensions = new ArrayList<>();

    ExtensionsManager(Base base, InterfaceManager interfaces)
    {
	notNull(base, "base");
	notNull(interfaces, "interfaces");
	this.base = base;
	this.interfaces = interfaces;
    }

    void load(InterfaceRequest interfaceRequest, CmdLine cmdLine, ClassLoader classLoader)
    {
	notNull(interfaceRequest, "interfaceRequest");
	notNull(cmdLine, "cmdLine");
	notNull(classLoader, "classLoader");
	final String[] extensionsList = getExtensionsList(cmdLine, classLoader);
	if (extensionsList == null || extensionsList.length == 0)
	    return;
	for(String s: extensionsList)
	{
	    if (s == null || s.trim().isEmpty())
		continue;
	    debug("loading " + s);
	    final Object o;
	    try {
		o = Class.forName(s, true, classLoader).getDeclaredConstructor().newInstance();
	    }
	    catch (Throwable e)
	    {
		error(e, "loading of extension " + s + " failed");
		continue;
	    }
	    if (!(o instanceof Extension))
	    {
		error("loading of extension " + s + " failed: this object isn't an instance of org.luwrain.core.Extension");
		continue;
	    }
	    final Extension ext = (Extension)o;
	    final Luwrain iface = interfaceRequest.getInterfaceObj(ext);
	    final String message;
	    try {
		message = ext.init(iface);
	    }
	    catch (Throwable ex)
	    {
		error(ex, "loading of extension " + s + " failed on extension init");
		interfaces.release(iface);
		continue;
	    }
	    if (message != null)
	    {
		error("loading of extension " + s + " failed: " + message);
		interfaces.release(iface);
		continue;
	    }
	    extensions.add(new Entry(ext, iface));
	}
    }

    @Override public void close()
    {
	for(Entry e: extensions)
	{
	    try {
		e.ext.close();
	    }
	    catch (Throwable ex)
	    {
		error(ex);
	    }
	    interfaces.release(e.luwrain);
	}
	extensions = null;
    }

    //From any thread
    public boolean runHooks(String hookName, Luwrain.HookRunner runner)
    {
	notEmpty(hookName, "hookName");
	notNull(runner, "runner");
	for(Entry e: extensions)
	    if (e.ext instanceof HookContainer && !((HookContainer)e.ext).runHooks(hookName, runner))
		return false;
	return true;
    }

    private String[] getExtensionsListFromManifest(ClassLoader classLoader)
    {
	NullCheck.notNull(classLoader, "classLoader");
	final List<String> res = new ArrayList<>();
	try {
	    Enumeration<java.net.URL> resources = classLoader.getResources("META-INF/MANIFEST.MF");
	    while (resources.hasMoreElements())
	    {                                                                                                         
		try {
		    Manifest manifest = new Manifest(resources.nextElement().openStream());
		    Attributes attr = manifest.getAttributes("org/luwrain");
		    if (attr == null)
			continue;
		    final String value = attr.getValue("Extensions");
		    if (value != null)
			res.add(value);
		}
		catch (IOException e)
		{                                                                                                                 
		    e.printStackTrace();
		}
	    }
	}
	catch (IOException ee)
	{
	    ee.printStackTrace();
	}
	return res.toArray(new String[res.size()]);
    }

    private String[] getExtensionsList(CmdLine cmdLine, ClassLoader classLoader)
    {
	NullCheck.notNull(cmdLine, "cmdLine");
	NullCheck.notNull(classLoader, "classLoader");
	final String[] cmdlineExtList = cmdLine.getArgs(EXTENSIONS_LIST_PREFIX);
	if(cmdlineExtList.length > 0)
	{
	    for(String s: cmdlineExtList)
		return s.split(":",-1);
	    return new String[0];
	}
	return getExtensionsListFromManifest(classLoader);
    }

    List<ScriptFile> getScriptFiles(String componentName)
    {
	notEmpty(componentName, "componentName");
	final String dataDir = base.props.getProperty(Luwrain.PROP_DIR_DATA);

	//Common JavaScript extensions
	final List<ScriptFile> res = new ArrayList<>();
	final File jsDir = base.props.getFileProperty(Luwrain.PROP_DIR_JS);
	if (jsDir.exists() && jsDir.isDirectory())
	{
	    final File[] files = jsDir.listFiles();
	    if (files != null)
		for(File f: files)
		{
		    if (f == null || !f.exists() || f.isDirectory())
			continue;
		    if (!f.getName().toUpperCase().endsWith(".JS"))
			continue;
		    final String name = f.getName();
		    final int pos = name.indexOf("-");
		    if (pos < 1 || pos >= name.length() - 4 || !name.substring(0, pos).toUpperCase().equals(componentName.toUpperCase()))
			continue;
		    res.add(new ScriptFile(componentName, f.getAbsolutePath(), dataDir));
		}
	}

	//JavaScript extensions from packs
	final File[] packs = base.getInstalledPacksDirs();
	for(File pack: packs)
	{
	    final File packDataDir = new File(pack, "data");
	    if (packDataDir.exists() && !packDataDir.isDirectory())
	    {
		warn("the pack contains '" + packDataDir.getAbsolutePath() + "' exists and it isn't a directory");
		continue;
	    }
	    if (!packDataDir.exists() && !packDataDir.mkdir())
	    {
		error("unable to create '" + packDataDir.getAbsolutePath() + "', skipping the pack");
		continue;
	    }
	    final File jsExtDir = new File(pack, "js");
	    if (!jsExtDir.exists() || !jsExtDir.isDirectory())
		continue;
	    final File[] files = jsExtDir.listFiles();
	    if (files == null)
		continue;
	    for(File f: files)
	    {
		if (f == null || !f.exists() || f.isDirectory())
		    continue;
		if (!f.getName().toUpperCase().endsWith(".JS"))
		    continue;
		final String name = f.getName();
		final int pos = name.indexOf("-");
		if (pos < 1 || pos >= name.length() - 4 || !name.substring(0, pos).toUpperCase().equals(componentName.toUpperCase()))
		    continue;
		res.add(new ScriptFile(componentName, f.getAbsolutePath(), packDataDir.getAbsolutePath()));
	    }
	}
	return res;
    }

    interface InterfaceRequest 
    {
	Luwrain getInterfaceObj(Extension ext);
    }

}
