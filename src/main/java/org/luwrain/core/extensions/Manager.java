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

package org.luwrain.core.extensions;

import java.util.*;
import java.util.jar.*;
import java.io.*;

import org.luwrain.core.*;

public class Manager
{
    public interface InterfaceRequest 
    {
	Luwrain getInterfaceObj(Extension ext);
    }

    private InterfaceManager interfaces;
    private LoadedExtension[] extensions;

    public Manager(InterfaceManager interfaces)
    {
	this.interfaces = interfaces;
	NullCheck.notNull(interfaces, "interfaces");
    }

    public void load(InterfaceRequest interfaceRequest)
    {
	LinkedList<LoadedExtension> res = new LinkedList<LoadedExtension>();
	final String[] extensionsList = getExtensionsList();
	if (extensionsList == null || extensionsList.length < 1)
	    return;
	for(String s: extensionsList)
	{
	    if (s == null || s.trim().isEmpty())
		continue;
	    Log.debug("core", "loading extension " + s);
	    Object o;
	    try {
		o = Class.forName(s).newInstance();
	    }
	    catch (InstantiationException e)
	    {
		Log.error("environment", "loading of extension " + s + " failed:instantiation problem:" + e.getMessage());
		continue;
	    }
	    catch (IllegalAccessException e)
	    {
		Log.error("core", "loading of extension " + s + " failed:illegal access:" + e.getMessage());
		continue;
	    }
	    catch (ClassNotFoundException e)
	    {
		Log.error("core", "loading of extension " + s + " failed:class not found:" + e.getMessage());
		continue;
	    }
	    if (!(o instanceof Extension))
	    {
		Log.error("core", "loading of extension " + s + " failed: this object isn\'t an instance of org.luwrain.core.Extension");
		continue;
	    }
	    final Extension ext = (Extension)o;
	    final Luwrain iface = interfaceRequest.getInterfaceObj(ext);
	    String message = null;
	    try {
		message = ext.init(iface);
	    }
	    catch (Exception ee)
	    {
		Log.error("core", "loading of extension " + s + " failed: unexpected exception:" + ee.getMessage());
		ee.printStackTrace();
		interfaces.release(iface);
		continue;
	    }
	    if (message != null)
	    {
		Log.error("core", "loading of extension " + s + " failed: " + message);
		interfaces.release(iface);
		continue;
	    }
	    final LoadedExtension loadedExt = new LoadedExtension();
	    loadedExt.ext = ext;
	    loadedExt.luwrain = iface;
	    loadedExt.commands = getCommands(ext, iface);
	    loadedExt.shortcuts = getShortcuts(ext, iface);
	    loadedExt.sharedObjects = getSharedObjects(ext, iface);
	    loadedExt.uniRefProcs = getUniRefProcs(ext, iface);
	    loadedExt.controlPanelFactories = getControlPanelFactories(ext, iface);
	    loadedExt.speechFactories = getSpeechFactories(ext, iface);
	    res.add(loadedExt);
	}
	extensions = res.toArray(new LoadedExtension[res.size()]);
	Log.debug("core", "loaded " + extensions.length + " extensions");
    }

    public void close()
    {
	for(LoadedExtension e: extensions)
	{
	    try {
		Log.debug("core", "closing extension " + e.ext.getClass().getName());
		e.ext.close();
	    }
	    catch (Throwable t)
	    {
		t.printStackTrace();
	    }
	    interfaces.release(e.luwrain);
	}
	extensions = null;
    }

    public LoadedExtension[] getAllLoadedExtensions()
    {
	return extensions;
    }

    private Shortcut[] getShortcuts(Extension ext, Luwrain luwrain)
    {
	try { 
		final Shortcut[] res = ext.getShortcuts(luwrain);
		return res != null?res:new Shortcut[0];
	}
	catch (Exception ee)
	{
	    Log.error("core", "extension " + ee.getClass().getName() + " has thrown an exception on providing the list of shortcuts:" + ee.getMessage());
	    ee.printStackTrace();
	    return new Shortcut[0];
	}
    }

    private SharedObject[] getSharedObjects(Extension ext, Luwrain luwrain)
    {
	try { 
	    final SharedObject[] res = ext.getSharedObjects(luwrain);
	    return res != null?res:new SharedObject[0];
	}
	catch (Exception ee)
	{
	    Log.error("environment", "extension " + ee.getClass().getName() + " has thrown an exception on providing the list of shared objects:" + ee.getMessage());
	    ee.printStackTrace();
	    return new SharedObject[0];
	}
    }

    private Command[] getCommands(Extension ext, Luwrain luwrain)
    {
	try {
	    final Command[] res = ext.getCommands(luwrain);
	    return res != null?res:new Command[0];
	}
	catch (Exception ee)
	{
	    Log.error("environment", "extension " + ee.getClass().getName() + " has thrown an exception on providing the list of commands:" + ee.getMessage());
	    ee.printStackTrace();
	    return new Command[0];
	}
    }

    private UniRefProc[] getUniRefProcs(Extension ext, Luwrain luwrain)
    {
	try {
	    final UniRefProc[] res = ext.getUniRefProcs(luwrain);
	    return res != null?res:new UniRefProc[0];
	}
	catch (Exception ee)
	{
	    Log.error("environment", "extension " + ee.getClass().getName() + " has thrown an exception on providing the list of uniRefProcs:" + ee.getMessage());
	    ee.printStackTrace();
	    return new UniRefProc[0];
	}
    }

    private org.luwrain.cpanel.Factory[] getControlPanelFactories(Extension ext, Luwrain luwrain)
    {
	try {
	    final org.luwrain.cpanel.Factory[] res = ext.getControlPanelFactories(luwrain);
	    return res != null?res:new org.luwrain.cpanel.Factory[0];
	}
	catch (Exception ee)
	{
	    Log.error("environment", "extension " + ee.getClass().getName() + " has thrown an exception on providing the list of control panel factories:" + ee.getMessage());
	    ee.printStackTrace();
	    return new org.luwrain.cpanel.Factory[0];
	}
    }

    private org.luwrain.speech.Factory[] getSpeechFactories(Extension ext, Luwrain luwrain)
    {
	try {
	    final org.luwrain.speech.Factory[] res = ext.getSpeechFactories(luwrain);
	    return res != null?res:new org.luwrain.speech.Factory[0];
	}
	catch (Exception ee)
	{
	    Log.error("environment", "extension " + ee.getClass().getName() + " has thrown an exception on providing the list of speech factories:" + ee.getMessage());
	    ee.printStackTrace();
	    return new org.luwrain.speech.Factory[0];
	}
    }

    private String[] getExtensionsList()
    {
	Vector<String> res = new Vector<String>();
	try {
	    Enumeration<java.net.URL> resources = getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
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
}
