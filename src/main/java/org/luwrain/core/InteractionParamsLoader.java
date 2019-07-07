/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core;

import org.luwrain.base.*;

class InteractionParamsLoader extends org.luwrain.base.InteractionParams
{
    static private final int DEFAULT_MARGIN = 16;
    static private final int MAX_MARGIN = 64;
    static private final int DEFAULT_FONT_COLOR = 192;
    static private final int DEFAULT_FONT2_COLOR = 255;
    static private final int DEFAULT_BKG_COLOR = 0;
    static private final int DEFAULT_SPLITTER_COLOR = 256;

    void loadFromRegistry(Registry registry)
    {
	NullCheck.notNull(registry, "registry");
	final Settings.InteractionParams settings = Settings.createInteractionParams(registry);

	wndLeft = checkRange(settings.getWindowLeft(0), 0, -1);
	wndTop = checkRange(settings.getWindowTop(0), 0, -1);
	wndWidth = settings.getWindowWidth(-1);
	wndHeight = settings.getWindowHeight(-1);

	marginLeft = checkRange(settings.getMarginLeft(DEFAULT_MARGIN), 0, MAX_MARGIN);
	marginTop = checkRange(settings.getMarginTop(DEFAULT_MARGIN), 0, MAX_MARGIN);
	marginRight = checkRange(settings.getMarginRight(DEFAULT_MARGIN), 0, MAX_MARGIN);
	marginBottom = checkRange(settings.getMarginBottom(DEFAULT_MARGIN), 0, MAX_MARGIN);

	int red = checkRange(settings.getFontColorRed(DEFAULT_FONT_COLOR), 0, 255);
	int green = checkRange(settings.getFontColorGreen(DEFAULT_FONT_COLOR), 0, 255);
	int blue = checkRange(settings.getFontColorBlue(DEFAULT_FONT_COLOR), 0, 255);
	fontColor = new InteractionParamColor(red, green, blue);

	red = checkRange(settings.getFont2ColorRed(DEFAULT_FONT2_COLOR), 0, 255);
	green = checkRange(settings.getFont2ColorGreen(DEFAULT_FONT2_COLOR), 0, 255);
	blue = checkRange(settings.getFont2ColorBlue(DEFAULT_FONT2_COLOR), 0, 255);
	font2Color = new InteractionParamColor(red, green, blue);

	red = checkRange(settings.getBkgColorRed(DEFAULT_BKG_COLOR), 0, 255);
	green = checkRange(settings.getBkgColorGreen(DEFAULT_BKG_COLOR), 0, 255);
	blue = checkRange(settings.getBkgColorBlue(DEFAULT_BKG_COLOR), 0, 255);
	bkgColor =  new InteractionParamColor(red, green, blue);

	red = checkRange(settings.getSplitterColorRed(DEFAULT_SPLITTER_COLOR), 0, 255);
	green = checkRange(settings.getSplitterColorGreen(DEFAULT_SPLITTER_COLOR), 0, 255);
	blue = checkRange(settings.getSplitterColorBlue(DEFAULT_SPLITTER_COLOR), 0, 255);
	splitterColor =   new InteractionParamColor(red, green, blue);

	initialFontSize = checkRange(settings.getInitialFontSize(14), 0, -1);
	fontName = settings.getFontName("Monospaced");
    }

    static private int checkRange(int value,
				  int min, int max)
    {
	if (max < 0)
	    return value >= min?value:min;
	if (value < min)
	    return min;
	if (value > max)
	    return max;
	return value;
    }
}
