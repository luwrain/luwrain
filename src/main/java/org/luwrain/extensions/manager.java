
package org.luwrain.extensions;

import java.util.*;
import java.util.jar.*;
import java.io.*;

import org.luwrain.core.*;

public class manager
{
    private String[] cmdLine;
    private Registry registry;
    private Extension[] extensions;

    public void load()
    {
	LinkedList<Extension> res = new LinkedList<Extension>();
	final String[] extensionsList = getExtensionsList();
	if (extensionsList == null || extensionsList.length < 1)
	    return;
	for(String s: extensionsList)
	{
	    if (s == null || s.trim().isEmpty())
		continue;
	    Log.debug("extensions", "loading extension " + s);
	    Object o;
	    try {
		o = Class.forName(s).newInstance();
	    }
	    catch (InstantiationException e)
	    {
		Log.info("extensions", "loading of extension " + s + " failed:instantiation problem:" + e.getMessage());
		continue;
	    }
	    catch (IllegalAccessException e)
	    {
		Log.info("extensions", "loading of extension " + s + " failed:illegal access:" + e.getMessage());
		continue;
	    }
	    catch (ClassNotFoundException e)
	    {
		Log.info("extensions", "loading of extension " + s + " failed:class not found:" + e.getMessage());
		continue;
	    }
	    if (!(o instanceof Extension))
	    {
		Log.info("extensions", "loading of extension " + s + " failed: this object isn\'t an instance of org.luwrain.core.Extension");
		continue;
	    }
	    final Extension ext = (Extension)o;
	    String message = null;
	    try {
		message = ext.init(cmdLine, registry);
	    }
	    catch (Exception ee)
	    {
		Log.info("extensions", "loading of extension " + s + " failed: unexpected exception:" + ee.getMessage());
		ee.printStackTrace();
		continue;
	    }
	    if (message != null)
	    {
		Log.info("extensions", "loading of extension " + s + " failed: " + message);
		continue;
	    }
	    res.add(ext);
	}
	extensions = res.toArray(new Extension[res.size()]);
	Log.debug("extensions", "loaded " + extensions.length + " extensions");
    }





    public Shortcut[] getShortcuts()
    {
	LinkedList<Shortcut> res = new LinkedList<Shortcut>();
	for(Extension e: extensions)
	{
	    Shortcut[] s;
	    try { 
		s = e.getShortcuts();
	    }
	    catch (Exception ee)
	    {
		Log.error("extensions", "extension " + ee.getClass().getName() + " has thrown an exception on providing the list of shortcuts:" + ee.getMessage());
		ee.printStackTrace();
		continue;
	    }
	    if (s != null)
		for(Shortcut ss: s)
		    if (ss != null)
			res.add(ss);
	}
	return res.toArray(new Shortcut[res.size()]);
    }

    public SharedObject[] getSharedObjects()
    {
	LinkedList<SharedObject> res = new LinkedList<SharedObject>();
	for(Extension e: extensions)
	{
	    SharedObject[] s;
	    try { 
		s = e.getSharedObjects();
	    }
	    catch (Exception ee)
	    {
		Log.error("environment", "extension " + ee.getClass().getName() + " has thrown an exception on providing the list of shared objects:" + ee.getMessage());
		ee.printStackTrace();
		continue;
	    }
	    if (s != null)
		for(SharedObject ss: s)
		    if (ss != null)
			res.add(ss);
	}
	return res.toArray(new SharedObject[res.size()]);
    }

    public Command[] getCommands()
    {
	LinkedList<Command> res = new LinkedList<Command>();
	for(Extension e: extensions)
	{
	    Command[] cmds;
	    try {
		cmds = e.getCommands(null);//FIXME:
	    }
	    catch (Exception ee)
	    {
		Log.error("environment", "extension " + ee.getClass().getName() + " has thrown an exception on providing the list of commands:" + ee.getMessage());
		ee.printStackTrace();
		continue;
	    }
	    if (cmds != null)
		for(Command c: cmds)
		    if (c != null)
			res.add(c);
	}
	return res.toArray(new Command[res.size()]);
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
