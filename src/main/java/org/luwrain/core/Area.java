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

//LWR_API 1.0

package org.luwrain.core;

import org.luwrain.core.events.*;

/**
 *Area is a fundamental entity of Luwrain interface. It is an
 * abstraction of any working object, which may be an editable text, a
 * table, a tree view, a form and so on.  Area constructs visual
 * representation of its content for low-vision users and behave as an
 * event recipient.
 * <p>
 * Area has a special point inside, which is called "hot point". It is
 * some generalization of a cursor on the screen. The hot point is
 * meaningful for visual content only and marks for low-vision users
 * where is a point inside of the area he/she should be focused at.
 * <p>
 * Dealing with the areas, Luwrain core usually doesn't produce any
 * speech announcements automatically. All areas should do it on their
 * own, depending on the desired application behaviour.  Roughly
 * speaking, if some change occurred in the area, this area should change
 * its visual representation returned through {@code getLine()} and 
 * {@code getLineCount()} methods, doing speech announcements separately. 
 * <p>
 * If the area changes its content, its name or its hot point position it
 * must notify about these changes the core using methods 
 * {@code onAreaNewContent()}, {@code onAreaNewName()} and 
 * {@code onAreaNewHotPoint()} of {@link Luwrain} class. Otherwise, it could result in application
 * inconsistency.  Generally, all areas should conform to the following
 * rules:
 * <ul>
 * <li>
 * Area objects never know whether they are visible on the screen or not
 * (meaning, they should expect that they are always visible)
 * </li>
 * <li>
 * The same is also applicable for the activity status (the area should
 * expect that it is also active)
 * </li>
 * <li>
 * Areas may not be nested or composite
 * </li>
 * <li>
 * The area may present on the screen only through showing in single
 * window; if the same document should be shown in different windows on
 * the screen the developer should use different areas
 * </li>
 * <li>
 * Area may not return zero number of lines for visual representation; if
 * the area is empty, it means that it contains single empty line
 * </li>
 * <li>
 * Area may request how many lines it should expect visible on the screen
 * (it is needed for "page down" and "page up" processing), but anyway it
 * never knows which exact lines are visible
 * </li>
 * <li>
 * Real length of the line on the screen is unknown (evidently, it
 * depends on the length of tabs)
 * </li>
 * </ul>
 */
public interface Area extends Lines, HotPoint
{
    String getAreaName();
    int getHotPointX();
    int getHotPointY();
    boolean onKeyboardEvent(KeyboardEvent event);
    boolean onSystemEvent(EnvironmentEvent event);
    boolean onAreaQuery(AreaQuery areaQuery);
    Action[] getAreaActions();
}
