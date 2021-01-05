/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.base;

public class InteractionParams
{
    public int wndLeft = 0;
    public int wndTop = 0;
    public int wndWidth = -1;//-1 means screen with;
    public int wndHeight = -1;//-1 means screen height;
    public int marginLeft = 16;
    public int marginTop = 16;
    public int marginRight = 16;
    public int marginBottom = 16;
    public InteractionParamColor fontColor = new InteractionParamColor(InteractionParamColor.Predefined.GRAY);
    public InteractionParamColor font2Color = new InteractionParamColor(InteractionParamColor.Predefined.WHITE);
    public InteractionParamColor bkgColor = new InteractionParamColor(InteractionParamColor.Predefined.BLACK);
    public InteractionParamColor splitterColor = new InteractionParamColor(InteractionParamColor.Predefined.LIGHT_GRAY);
    public int initialFontSize = 14;
    public String fontName = "Monospaced";
}
