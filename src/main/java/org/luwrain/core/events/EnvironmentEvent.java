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

package org.luwrain.core.events;

import org.luwrain.core.*;

/**
 * An event addressed to one or several areas and not related to user
 * input. Environment events are delivered to areas through 
 * {@code onEnvironmentEvent()} method and notifies that area should do some
 * action which certainly not related to user input.  The area may decide
 * on its own whether it would like to perform the required action or to
 * ignore it. There can be both system-defined environment events and
 * extension-defined. Generally, there are three major types of
 * environment events:
 * <ul>
 * <li>Regular events</li>
 * <li>Broadcast events</li>
 * <li>Addressed events</li>
 * </ul>
 * If regular events are placed in main event loop, they are always
 * delivered to the currently active area. Meantime, it is also quite usual
 * practice to send to the area a regular event directly, if you have a
 * reference to this area. This means that you just want to ask the area
 * to do some action (for example, to refresh its content or to save the
 * unsaved data), and there are no problems with that.
 * <p>
 * Broadcast events are always delivered to the areas through main event
 * loop (otherwise they are pointless).  If LUWRAIN core finds such event
 * in main event loop, it tries to decide which areas should get it,
 * depending on the class name of the area or on the uniref value (see
 * {@code getBroadcastFilterAreaClassName()} and {@code
 * getBroadcastFilterUniRef()} methods). Broadcast events are delivered
 * over all launched applications.
 * <p>
 * Addressed events are always delivered to some particular area, even if
 * they are placed in main event loop. Usually they are useful for thread
 * synchronization because handling of the events is always done in main
 * system thread while they can be issued in any thread. The only
 * restriction is that the application which has issued this event must
 * be still launched when the event is processed. All addressed events must be
 * derived from {@link AddressedEnvironmentEvent} class.
 *
 * @see AddressedEnvironmentEvent
 */
public class EnvironmentEvent extends Event
{
    public enum Type {REGULAR, BROADCAST};

    public enum Code {OK,
		      ANNOUNCE_LINE,
		      CANCEL,
		      CLOSE,
		      SAVE,
		      REFRESH,
		      INTRODUCE,
		      HELP,
		      THREAD_SYNC,
		      MESSAGE,
		      ACTION,
		      OPEN,
		      MOVE_HOT_POINT,
		      READING_POINT,
		      LISTENING_FINISHED,
		      REGION_POINT,
		      INSERT,
		      DELETE,
		      PROPERTIES,
    };

    private Code code;
    private Type type;
    private String broadcastFilterAreaClassName = null;
    private String broadcastFilterUniRef = null;

    public EnvironmentEvent(Code code)
    {
	NullCheck.notNull(code, "code");
	this.type = Type.REGULAR;
	this.code = code;
    }

    public EnvironmentEvent(Type type, Code code)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(code, "code");
	this.type = type;
	this.code = code;
    }

    public EnvironmentEvent(Type type, Code code,
			    String broadcastFilterAreaClassName, String broadcastFilterUniRef)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(code, "code");
	this.type = type;
	this.code = code;
	this.broadcastFilterAreaClassName = broadcastFilterAreaClassName;
	this.broadcastFilterUniRef = broadcastFilterUniRef;
    }

    public final Code getCode()
    {
	return code;
    }

    public final Type getType()
    {
	return type != null?type:Type.REGULAR;
    }

    public String getBroadcastFilterAreaClassName()
    {
	return broadcastFilterAreaClassName != null?broadcastFilterAreaClassName:"";
    }

    public String getBroadcastFilterUniRef()
    {
	return broadcastFilterUniRef != null?broadcastFilterUniRef:"";
    }

    static public boolean resetRegionPoint(Area area)
    {
	NullCheck.notNull(area, "area");
	return false;
    }
}
