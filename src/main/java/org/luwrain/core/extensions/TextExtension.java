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

public final class TextExtension implements DynamicExtension
{
    static private final String LOG_COMPONENT = Manager.LOG_COMPONENT;
    static private final String PREFIX_COMMAND = "command.";

    private File baseDir;
    private Luwrain luwrain = null;

    private final Map<String, CmdEntry> commands = new HashMap();
    private final Map<String, LangEntry> langs = new HashMap();

    public TextExtension(File baseDir)
    {
	NullCheck.notNull(baseDir, "baseDir");
	this.baseDir = baseDir;
    }

    @Override public String init(Luwrain luwrain)
    {
	//Never called for this type of extension
	return null;
    }

    public void load(String text) throws IOException
    {
	NullCheck.notNull(text, "text");
	final Properties props = new Properties();
	final StringReader reader = new StringReader(text);
	try {
	    props.load(reader);
	}
	finally {
	    reader.close();
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
	final String SUFFIX_RADIO = ".radio";
	if (key.matches(".*\\.title\\..."))
	{
	    final String cmdName = key.substring(0, key.length() - 9);
	    final String lang = key.substring(key.length() - 2).toLowerCase();
	    if (cmdName.trim().isEmpty())
	    {
		Log.error(LOG_COMPONENT, "a command without a name in text extension");
		return;
	    }
	    if (value.isEmpty())
	    {
		Log.error(LOG_COMPONENT, "no argument for the title of the command \'" + cmdName + "\' in text extension");
		return;
	    }
	    if (!langs.containsKey(lang))
		langs.put(lang, new LangEntry(lang));
	    langs.get(lang).cmdTitles.put(cmdName, value);
	    return;
	}
	if (key.endsWith(SUFFIX_FILE) ||
	    key.endsWith(SUFFIX_RADIO))
	{
	    final CmdEntry.Type type;
	    if (key.endsWith(SUFFIX_FILE))
		type = CmdEntry.Type.FILE; else
		if (key.endsWith(SUFFIX_RADIO))
		    type = CmdEntry.Type.RADIO; else
		    return;//never happens
	    final String cmdName;
	    switch(type)
	    {
	    case FILE:
		cmdName = key.substring(0, key.length() - SUFFIX_FILE.length());
		break;
	    case RADIO:
		cmdName = key.substring(0, key.length() - SUFFIX_RADIO.length());
		break;
	    default:
		return;//never happens
	    }
	    if (cmdName.trim().isEmpty())
	    {
		Log.error(LOG_COMPONENT, "a command without a name in text extension");
		return;
	    }
	    if (value.isEmpty())
	    {
		Log.error(LOG_COMPONENT, "no argument for the file command \'" + cmdName + "\' in text extension");
		return;
	    }
	    this.commands.put(cmdName, new CmdEntry(cmdName, type, value));
	    return;
	}
	Log.error(LOG_COMPONENT, "unrecognized entry \'" + key + "\' in text extension");
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
	for(Map.Entry<String, CmdEntry> e: commands.entrySet())
	{
	    final CmdEntry entry = e.getValue();
	    final Command cmd = new Command(){
		    @Override public String getName()
		    {
			return entry.name;
		    }
		    @Override public void onCommand(Luwrain luwrain)
		    {
			NullCheck.notNull(luwrain, "luwrain");
			switch(entry.type)
			{
			case  FILE:
			    luwrain.openFile(new File(baseDir, entry.arg).getAbsolutePath());
			case  RADIO:
			    if (luwrain.getPlayer() == null)
			    {
				luwrain.playSound(Sounds.ERROR);//FIXME: message
				return;
			    }
			    luwrain.playSound(Sounds.PARAGRAPH);//FIXME:
			    luwrain.getPlayer().play(new org.luwrain.player.Playlist(entry.arg), 0, 0, EnumSet.of(org.luwrain.player.Player.Flags.STREAMING));
			    break;
			}
		    }
		};
	    res.add(cmd);
	}
	return res.toArray(new Command[res.size()]);
    }

    @Override public Shortcut[] getShortcuts(Luwrain luwrain)
    {
	return new Shortcut[0];
    }

    @Override public void i18nExtension(Luwrain luwrain, I18nExtension i18nExt)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(i18nExt, "i18nExt");
	for(Map.Entry<String, LangEntry> l: langs.entrySet())
	{
	    final LangEntry lang = l.getValue();
	    for(Map.Entry<String, String> c: lang.cmdTitles.entrySet())
		i18nExt.addCommandTitle(lang.langName, c.getKey(), c.getValue());
	}
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
	    RADIO,
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

    static private final class LangEntry
    {
	final String langName;
	final Map<String, String> cmdTitles = new HashMap();
	LangEntry(String langName)
	{
	    NullCheck.notEmpty(langName, "langName");
	    this.langName = langName;
	}
    }
}
