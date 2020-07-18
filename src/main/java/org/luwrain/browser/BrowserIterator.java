/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>
   Copyright 2015-2016 Roman Volovodov <gr.rPman@gmail.com>

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

package org.luwrain.browser;

import java.awt.Rectangle;
import java.util.*;

public interface BrowserIterator
{
	Browser getBrowser();
    int getPos();
    /** although it is strongly discouraged to use this method.*/ 
boolean setPos(int index);

        BrowserIterator getParent();
    boolean hasParent();

    //Never returns null
        String getTagName();
    String getClassName();
        String getText();
    String getAttr(String name);
    Map<String, String> getAttrs();
        Rectangle getRect();


    //Forms operations
    boolean isInput();
    String getInputType();
    boolean setInputText(String text);
        void setText(String text);
    String getComputedStyle(final String name);
    String getAllComputedStyles();
    void emulateClick();
	void emulateSubmit();
BrowserIterator clone();

    	String getAltText();
    //boolean isParent(BrowserIterator it);
        /** get list of values for HTML SELECT FIXME: it's awful */
    //String[] getMultipleText();
    //String getComputedText();
    //String getLink();
    //boolean isEditable();
}
