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

package org.luwrain.controls;

import org.luwrain.core.*;

//Should not issue any speech feedback
public interface MultilineEditHighLevelModel extends Lines
{
    int getHotPointX();
    int getHotPointY();
    int getLineCount();
    String getLine(int index);
    String getTabSeq();
    char deleteChar(int pos, int lineIndex);
    //Expects ending point always after starting
    boolean deleteRegion(int fromX, int fromY, int toX, int toY);
    boolean insertRegion(int x, int y, String[] lines);
    void insertChars(int pos, int lineIndex, String str);
    void mergeLines(int firstLineIndex);
    String splitLines(int pos, int lineIndex);
}
