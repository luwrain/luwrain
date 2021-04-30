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

public interface Interaction
{
    public interface GraphicalModeControl
    {
	void close();
    }

public interface GraphicalMode
{
    Object getGraphicalObj(GraphicalModeControl control);
}
    
    boolean init(InteractionParams params,OperatingSystem os);
    void close();
    void startInputEventsAccepting(EventConsumer eventConsumer);
    void stopInputEventsAccepting();
    boolean setDesirableFontSize(int size);
    int getFontSize();
    int getWidthInCharacters();
    int getHeightInCharacters();
    void startDrawSession();
    void clearRect(int left, int top, int right, int bottom);
    void drawText(int x, int y, String text);
	void drawText(int x,int y,String text,boolean font2);
    void endDrawSession();
    void setHotPoint(int x, int y);
    void drawVerticalLine(int top, int bottom, int x);
    void drawHorizontalLine(int left, int right, int y);
    void showGraphical(GraphicalMode graphicalMode);
}
