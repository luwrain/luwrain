/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.io.*;

class OsCommands
{
    static class OsCommand implements Command
    {
	private String name = "";
	private String command = "";
	private boolean showResultingMessage = true;

	boolean init(Settings.OsCommand settings)
	{
	    NullCheck.notNull(settings, "settings");
	    name = settings.getName("");
	    Log.debug("cmd", name);
	    if (name.trim().isEmpty())
		return false;
	    command = settings.getCommand("");
	    showResultingMessage = settings.getShowResultingMessage(showResultingMessage);
	    return true;
	}

	@Override public String getName()
	{
	    return name;
	}

	@Override public void onCommand(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    if (showResultingMessage)
		luwrain.runOsCommand(command, "", (line)->{}, (exitCode, lines)->{
			luwrain.runInMainThread(()->issueResultingMessage(luwrain, exitCode, lines));
		    }); else
		luwrain.runOsCommand(command);
	}
    }

    static class OsShortcut implements Shortcut
    {
	private final Luwrain luwrain;
	private String name = "";
	private String command = "";
	private boolean showResultingMessage = true;

	OsShortcut(Luwrain luwrain)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    this.luwrain = luwrain;
	}

	boolean init(Settings.OsShortcut settings)
	{
	    NullCheck.notNull(settings, "settings");
	    name = settings.getName("");
	    Log.debug("cmd", name);
	    if (name.trim().isEmpty())
		return false;
	    command = settings.getCommand("");
	    showResultingMessage = settings.getShowResultingMessage(showResultingMessage);
	    return true;
	}

	@Override public String getName()
	{
	    return name;
	}

	@Override public Application[] prepareApp(String[] args)
	{
	    NullCheck.notNullItems(args, "args");
	    final StringBuilder b = new StringBuilder();
	    b.append(command);
	    for(String a: args)
		b.append(" \'" + a.replaceAll("\'", "\'\\\'\'") + "\'");
	    if (showResultingMessage)
		luwrain.runOsCommand(new String(b), "", (line)->{}, (exitCode, lines)->{
			luwrain.runInMainThread(()->issueResultingMessage(luwrain, exitCode, lines));
		    }); else
		luwrain.runOsCommand(new String(b));
	    return null;
	}
    }

    static void issueResultingMessage(Luwrain luwrain, int exitCode, String[] lines)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNullItems(lines, "lines");
	final StringBuilder b = new StringBuilder();
	if (lines.length >= 1)
	{
	    b.append(lines[0]);
	    for(int i = 1;i < lines.length;++i)
		b.append(" " + lines[i]);
	}
	final String text = new String(b).trim();
	if (!text.isEmpty())
	    luwrain.message(text, exitCode == 0?Luwrain.MESSAGE_DONE:Luwrain.MESSAGE_ERROR); else
	    if (exitCode == 0)
		luwrain.message(luwrain.i18n().getStaticStr("OsCommandFinishedSuccessfully"), Luwrain.MESSAGE_DONE); else
		luwrain.message(luwrain.i18n().getStaticStr("OsCommandFailed"), Luwrain.MESSAGE_ERROR);
    }
}
