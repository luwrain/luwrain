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

package org.luwrain.app.console;

import java.util.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.*;
import org.apache.logging.log4j.core.config.plugins.*;

@Plugin(
	name = "LogAppender",
	category = "Core",
		elementType = Appender.ELEMENT_TYPE)
	public class LogAppender extends AbstractAppender
	{
	    //	    static public ArrayList<LogEvent> events = new ArrayList<>();

                protected LogAppender(String name, Filter filter)
	    {
                    super(name, filter, null);
                }

                @PluginFactory
                public static LogAppender createAppender
		    (
                  @PluginAttribute("name") String name, 
                  @PluginElement("Filter") Filter filter) {
                    return new LogAppender(name, filter);
                }
            
                @Override
                public void append(LogEvent event)
	    {
		App.events.add(new Entry(event));
		//		System.out.println(App.events.get(App.events.size() - 1).getMessage().getFormattedMessage());
		if (!event.getLoggerName().startsWith("org.luwrain.core"))
		    return;
		switch(event.getLevel().getStandardLevel())
		{
		case INFO:
		    System.out.println(event.getMessage().getFormattedMessage() + " (" + event.getLoggerName() + ")");
		    break;
		    		case WARN:
		    System.out.println("WARN: " + event.getMessage().getFormattedMessage() + " (" + event.getLoggerName() + ")");
		    break;
		    		    		case ERROR:
		    System.out.println("ERROR: " + event.getMessage().getFormattedMessage() + " (" + event.getLoggerName() + ")");
		    break;
		}
                }
            }
