
package org.luwrain.core;

//https://logging.apache.org/log4j/2.x/manual/extending.html

//import apache.logging.log4j.*;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.*;
import org.apache.logging.log4j.core.config.plugins.*;

@Plugin(
	name = "LogAppender",
	category = "Core",
		elementType = Appender.ELEMENT_TYPE)
	public class LogAppender extends AbstractAppender
	{
	    //                private ConcurrentMap<String, LogEvent> eventMap = new ConcurrentHashMap<>();

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
                public void append(LogEvent event) {
		    //                    eventMap.put(Instant.now().toString(), event);
		    System.out.println("internal " + event.toString());
                }
            }
           
