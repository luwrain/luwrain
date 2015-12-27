/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls;

import java.nio.file.*;
import org.luwrain.core.*;

public class DefaultCommanderAppearance implements CommanderArea.Appearance
{
    private ControlEnvironment environment;

    public DefaultCommanderAppearance(ControlEnvironment environment)
    {
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
    }

    @Override public void introduceEntry(CommanderArea.Entry entry, boolean brief)
    {
	NullCheck.notNull(entry, "entry");
	environment.playSound(Sounds.NEW_LIST_ITEM);
	if (brief)
	    briefIntroduction(entry); else
	    fullIntroduction(entry);
    }

    private void briefIntroduction(CommanderArea.Entry entry)
    {
	final String name = entry.baseName();
	if (entry.parent())
	    environment.hint(environment.staticStr(LangStatic.COMMANDER_PARENT_DIRECTORY)); else
	    if (name.trim().isEmpty())
		environment.hint(Hints.EMPTY_LINE); else
		environment.say(entry.baseName());
    }

    private void fullIntroduction(CommanderArea.Entry entry)
    {
	final boolean selected = entry.selected();
	final String name = entry.baseName();
	final CommanderArea.Entry.Type type = entry.type();
	if (name.trim().isEmpty() && !selected && type == CommanderArea.Entry.Type.REGULAR)
	{
	    environment.hint(Hints.EMPTY_LINE);
	    return;
	}
	String res = name;
	switch(type)
	{
	case DIR:
	    res += (" " + environment.staticStr(LangStatic.COMMANDER_DIRECTORY));
	    break;
	case SYMLINK:
	    res += (" " + environment.staticStr(LangStatic.COMMANDER_SYMLINK));
	    break;
	case SOCKET:
	    res += (" " + environment.staticStr(LangStatic.COMMANDER_SOCKET));
	    break;
	case PIPE:
	    res += (" " + environment.staticStr(LangStatic.COMMANDER_PIPE));
	    break;
	case CHAR_DEVICE:
	    res += (" " + environment.staticStr(LangStatic.COMMANDER_CHAR_DEVICE));
	    break;
	case BLOCK_DEVICE:
	    res += (" " + environment.staticStr(LangStatic.COMMANDER_BLOCK_DEVICE));
	    break;
	case SPECIAL:
	    res += (" " + environment.staticStr(LangStatic.COMMANDER_SPECIAL));
	    break;
	case UNKNOWN:
	    res += (" " + environment.staticStr(LangStatic.COMMANDER_UNKNOWN));
	    break;
	}
	if (selected)
	    res = environment.staticStr(LangStatic.COMMANDER_SELECTED) + " " + res;
	environment.say(res);
    }

    @Override public  void introduceLocation(Path path)
    {
	if (path == null)
	    return;
	environment.playSound(Sounds.COMMANDER_NEW_LOCATION);
	environment.say(path.toString());
    }

    @Override public String getScreenLine(CommanderArea.Entry entry)
    {
	NullCheck.notNull(entry, "entry");
	final boolean selected = entry.selected();
	final CommanderArea.Entry.Type type = entry.type();
	final String name = entry.baseName();
	final StringBuilder b = new StringBuilder();
	b.append(selected?"*":" ");
	switch(type)
	{
	case REGULAR:
	    b.append(" ");
	    break;
	case DIR:
	    b.append("[");
	    break;
	case SPECIAL:
	    b.append("!");
	    break;
	case UNKNOWN:
	    b.append("?");
	    break;
	case SYMLINK:
	    b.append("{");
	    break;
	}
	b.append(name);
	switch(type)
	{
	case DIR:
	    b.append("]");
	    break;
	case SYMLINK:
	    b.append("}");
	    break;
	}
	return new String(b);
    }

    @Override public String getCommanderName(Path path)
    {
	return path.toString();
    }
}


