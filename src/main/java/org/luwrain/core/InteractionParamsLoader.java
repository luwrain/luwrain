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

package org.luwrain.core;

import org.luwrain.util.RegistryAutoCheck;

class InteractionParamsLoader extends InteractionParams
{
    static private final int MAX_MARGIN = 64;
    final RegistryKeys keys = new RegistryKeys();

    void loadFromRegistry(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	final RegistryAutoCheck  check= new RegistryAutoCheck (registry, "interaction");
	wndLeft = check.intPositive(keys.interactionWndLeft(), wndLeft);
	wndTop = check.intPositive(keys.interactionWndTop(), wndTop);
	wndWidth = check.intAny(keys.interactionWndWidth(), wndWidth);
	wndHeight = check.intAny(keys.interactionWndHeight(), wndHeight);
	marginLeft = check.intRange(keys.interactionMarginLeft(), 0, MAX_MARGIN, marginLeft);
	marginTop = check.intRange(keys.interactionMarginTop(), 0, MAX_MARGIN, marginTop);
	marginRight = check.intRange(keys.interactionMarginRight(), 0, MAX_MARGIN, marginRight);
	marginBottom = check.intRange(keys.interactionMarginBottom(), 0, MAX_MARGIN, marginBottom);

	int red = check.intRange(keys.interactionFontColorRed(), 0, 255, 255);
	int green = check.intRange(keys.interactionFontColorGreen(), 0, 255, 255);
	int blue = check.intRange(keys.interactionFontColorBlue(), 0, 255, 255);
	fontColor = new InteractionParamColor(red, green, blue);

	red = check.intRange(keys.interactionBkgColorRed(), 0, 255, 0);
	green = check.intRange(keys.interactionBkgColorGreen(), 0, 255, 0);
	blue = check.intRange(keys.interactionBkgColorBlue(), 0, 255, 0);
	bkgColor = new InteractionParamColor(red, green, blue);

	red = check.intRange(keys.interactionSplitterColorRed(), 0, 255, 128);
	green = check.intRange(keys.interactionSplitterColorGreen(), 0, 255, 128);
	blue = check.intRange(keys.interactionSplitterColorBlue(), 0, 255, 128);
	splitterColor = new InteractionParamColor(red, green, blue);

	initialFontSize = check.intPositiveNotZero(keys.interactionInitialFontSize(), initialFontSize);
	fontName = check.stringNotEmpty(keys.interactionFontName(), fontName);
    }
}
