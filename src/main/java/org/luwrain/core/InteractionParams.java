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
import org.luwrain.util.RegistryAutoCheck;

public class InteractionParams
{
    private static final int MAX_MARGIN = 64;

    public String backend = "awt";
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

    public void loadFromRegistry(Registry registry)
    {
	if (registry == null)
	    throw new NullPointerException("Registry may not be null");
	RegistryAutoCheck  check= new RegistryAutoCheck (registry, "interaction");
	RegistryKeys keys = new RegistryKeys();
	wndLeft = check.intPositive(keys.interactionWndLeft(), wndLeft);
	wndTop = check.intPositive(keys.interactionWndTop(), wndTop);
	wndWidth = check.intAny(keys.interactionWndWidth(), wndWidth);
	wndHeight = check.intPositive(keys.interactionWndHeight(), wndHeight);
	marginLeft = check.intRange(keys.interactionMarginLeft(), 0, MAX_MARGIN, marginLeft);
	marginTop = check.intRange(keys.interactionMarginTop(), 0, MAX_MARGIN, marginTop);
	marginRight = check.intRange(keys.interactionMarginRight(), 0, MAX_MARGIN, marginRight);
	marginBottom = check.intRange(keys.interactionMarginBottom(), 0, MAX_MARGIN, marginBottom);

	int red = check.intRange(keys.interactionFontColorRed(), 0, 255, 255);
	int green = check.intRange(keys.interactionFontColorGreen(), 0, 255, 255);
	int blue = check.intRange(keys.interactionFontColorBlue(), 0, 255, 255);
	fontColor = new Color(red, green, blue);

	red = check.intRange(keys.interactionBkgColorRed(), 0, 255, 0);
	green = check.intRange(keys.interactionBkgColorGreen(), 0, 255, 0);
	blue = check.intRange(keys.interactionBkgColorBlue(), 0, 255, 0);
	bkgColor = new Color(red, green, blue);

	red = check.intRange(keys.interactionSplitterColorRed(), 0, 255, 128);
	green = check.intRange(keys.interactionSplitterColorGreen(), 0, 255, 128);
	blue = check.intRange(keys.interactionSplitterColorBlue(), 0, 255, 128);
	splitterColor = new Color(red, green, blue);

	initialFontSize = check.intPositiveNotZero(keys.interactionInitialFontSize(), initialFontSize);
	fontName = check.stringNotEmpty(keys.interactionFontName(), fontName);
    }
}
