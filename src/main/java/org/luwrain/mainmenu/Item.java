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

package org.luwrain.mainmenu;

import org.luwrain.core.Luwrain;

/**
 * General interface for the main menu item. The instances of this
 * interface are provided by Luwrain extensions and used for construction
 * of the main menu. Items can be one of the two types: general
 * information item or an action item. Information item only shows some
 * information to the user but the Enter button does nothing on
 * it. Action item can do some action using provided 
 * {@code org.luwrain.core.Luwrain} object, if the user presses the Enter button
 * on it.
 */
public interface Item 
{
    boolean isMMItemEnabled();
    String getMMItemText();
    void introduceMMItem(Luwrain luwrain);
    boolean isMMAction();
    void doMMAction(Luwrain luwrain);
}
