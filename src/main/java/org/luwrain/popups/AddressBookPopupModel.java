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

package org.luwrain.popups;

public interface AddressBookPopupModel
{
    boolean switchToNextSection();
    boolean switchToPrevSection();
    String getCurrentSectionName();
    int getContactCount();
    String getContactTitle(int index);
    boolean isContactMarked(int index);
    boolean toggleContactMark(int index);
    String getContactDetails(int index);
    boolean markContactWithDetails(int index, String[] details);
}
