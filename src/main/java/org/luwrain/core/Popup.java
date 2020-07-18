/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 1.0

package org.luwrain.core;

import java.util.*;

/**
 * Provides all necessary additional data about the area shown to user in
 * model mode. The popups in Luwrain are the areas of a special type
 * which main distinguish is that their appearance initiates new instance
 * of the message loop. 
 *
 * The popups can be shown as a single method invocation but since that
 * freezes processing of input and environment events this feature is
 * implemented with launching new temporary copy of event loop
 * procedure. So the key attribute is a flag which signals that new event
 * loop can be stopped. If the popup object is prepared its real start
 * should be done with Luwrain.popup() method.
 */
public interface Popup extends Area
{
    public enum Flags {
	NO_MULTIPLE_COPIES, WEAK, STRONG,
    };

    public enum Position{TOP, BOTTOM, LEFT, RIGHT};

    static public final int NO_MULTIPLE_COPIES = 1;
    static public final int WEAK = 2;

    boolean isPopupActive();
    Luwrain getLuwrainObject();
    //    EventLoopStopCondition getStopCondition();
    Set<Flags> getPopupFlags();
}
