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
import org.apache.logging.log4j.*;

import static org.luwrain.core.NullCheck.*;

public final class Log
{
    static private final Logger LOG = LogManager.getLogger();

    
    public enum Level {DEBUG, INFO, WARNING, ERROR, FATAL};

    static public final class Message
    {
	public final Level level;
	public final String component;
	public final String message;
	Message(Level level, String component, String message)
	{
	    notNull(level, "level");
	    notNull(component, "component");
	    notNull(message, "message");
	    this.level = level;
	    this.component = component;
	    this.message = message;
	}
    }

    public interface Listener
    {
	void onLogMessage(Message message);
    }

    static private final List<Listener> listeners = new ArrayList<>();

    static public void addListener(Listener listener)
    {
	notNull(listener, "listener");
	for(int i = 0;i < listeners.size();++i)
	    if (listeners.get(i) == listener)
		return;
	listeners.add(listener);
    }

    static public void removeListener(Listener listener)
    {
	notNull(listener, "listener");
	for(int i = 0;i < listeners.size();++i)
	    if (listeners.get(i) == listener)
	    {
		listeners.remove(i);
		return;
	    }
    }

    static public void debug(String component, String message) { LOG.debug(message); }
    static public void info(String component, String message) { LOG.info(message); }
    static public void warning(String component, String message) { LOG.warn(message); }
    static public void error(String component, String message) { LOG.error(message); }
    static public void fatal(String component, String message) { LOG.fatal(message); }

    /*
    static private String cap(String str)
    {
	if (str.isEmpty())
	    return "";
	final char ch = str.charAt(0);
	if (Character.isLetter(ch) && Character.toUpperCase(ch) != ch)
	    return Character.toUpperCase(ch) + str.substring(1);
	return str;
    }
    */
}
