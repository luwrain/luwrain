/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

import java.awt.Color;

public class InteractionParams
{
    private static final int MAX_MARGIN = 64;

    public int wndLeft = 0;
    public int wndTop = 0;
    public int wndWidth = -1;//-1 means screen with;
    public int wndHeight = -1;//-1 means screen height;
    public int marginLeft = 16;
    public int marginTop = 16;
    public int marginRight = 16;
    public int marginBottom = 16;
    public Color fontColor = new Color(255, 255, 255);
    public Color bkgColor = new Color(0, 0, 0);
    public Color splitterColor = new Color(128, 128, 128);
    public int initialFontSize = 14;
    public String fontName = java.awt.Font.MONOSPACED;

    public void loadFromRegistry(Registry registry, String logger)
    {
	if (registry == null)
	    throw new NullPointerException("Registry cannot be null");
	RegistryValueCheck check= new RegistryValueCheck(registry, logger != null?logger:"interaction");
	wndLeft = check.intPositive("", wndLeft);
	wndTop = check.intPositive("", intTop);
	wndWidth = check.intAny("", wndWidth);
	wndHeight = chekc.intPositive("", wndHeight);
	marginLeft = check.intRange("", 0, MAX_MARGIN, marginLeft);
	marginTop = check.intRange("", 0, MAX_MARGIN, marginTop);
	marginRight = check.intRange("", 0, MAX_MARGIN, marginRight);
	marginBottom = check.intRange("", 0, MAX_MARGIN, marginBotoom);
	//FIXME:colors;
	initialFontSize = check.intPositiveNotZero("", initialFontSize);
	fontName = check.strNotEmpty("", fontName);
    }
}
