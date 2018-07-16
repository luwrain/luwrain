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

package org.luwrain.core.extensions;

import java.io.*;
import java.util.*;

import org.luwrain.base.*;
import org.luwrain.core.*;

final class TextExtension implements DynamicExtension
{
    static private final String LOG_COMPONENT = Manager.LOG_COMPONENT;
    
    static private final String PREFIX_COMMAND = "command.";
    
    final String name;
    private Luwrain luwrain = null;

    private final Map<String, CmdEntry> commands = new HashMap();

    TextExtension(String name)
    {
	NullCheck.notEmpty(name, "name");
	this.name = name;
    }

@Override public String init(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	return null;
    }

    private void load(File file) throws IOException
    {
	NullCheck.notNull(file, "file");
	final Properties props = new Properties();
	final InputStream is = new FileInputStream(file);
	try {
	    props.load(is);
	}
	finally {
	    is.close();
	}
	for(Map.Entry e: props.entrySet())
	{
	    if (e.getKey() == null || e.getValue() == null)
		continue;
	    final String key = e.getKey().toString().trim();
	    final String value = e.getValue().toString().trim();
	    if (key.startsWith(PREFIX_COMMAND))
	    {
		addCommand(key.substring(PREFIX_COMMAND.length()), value);
		continue;
	    }
	}
    }

	private void addCommand(String key, String value)
	{
	    NullCheck.notNull(key, "key");
	    NullCheck.notNull(value, "value");
	    final String SUFFIX_FILE = ".file";
	    if (key.endsWith(SUFFIX_FILE))
	    {
		final String cmdName = key.substring(0, key.length() - SUFFIX_FILE.length());
		if (cmdName.trim().isEmpty())
		{
		    Log.error(LOG_COMPONENT, "a command without a name in the text extension \'" + name + "\'");
		    return;
		}
		if (value.isEmpty())
		{
		    Log.error(LOG_COMPONENT, "no argument for the file command \'" + cmdName + "\' in the text extension \'" + name + "\'");
		    return;
		}
		this.commands.put(cmdName, new CmdEntry(cmdName, CmdEntry.Type.FILE, value));
		return;
	    }
	    Log.error(LOG_COMPONENT, "unrecognized entry \'" + key + "\' in the text extension \'" + name + "\'");
	}
	
            @Override public void close()
    {
    }

    @Override public ExtensionObject[] getExtObjects(Luwrain luwrain)
    {
	final List<ExtensionObject> res = new LinkedList();
	//FIXME:
	return res.toArray(new ExtensionObject[res.size()]);
    }

    @Override public Command[] getCommands(Luwrain luwrain)
    {
	final List<Command> res = new LinkedList();
	//FIXME:
	return res.toArray(new Command[res.size()]);
    }

    @Override public Shortcut[] getShortcuts(Luwrain luwrain)
    {
	return new Shortcut[0];
    }

    @Override public void i18nExtension(Luwrain luwrain, I18nExtension i18nExt)
    {
    }

    @Override public org.luwrain.cpanel.Factory[] getControlPanelFactories(Luwrain luwrain)
    {
	return new org.luwrain.cpanel.Factory[0];
    } 

    @Override public UniRefProc[] getUniRefProcs(Luwrain luwrain)
    {
	return new UniRefProc[0];
    }

    static private final class CmdEntry
    {
	enum Type {
	    FILE,
	};
	
	final String name;
	final Type type;
	final String arg;

	CmdEntry(String name, Type type, String arg)
	{
	    NullCheck.notEmpty(name, "name");
	    NullCheck.notNull(type, "type");
	    NullCheck.notNull(arg, "arg");
	    this.name = name;
	    this.type = type;
	    this.arg = arg;
	}
    }
}
