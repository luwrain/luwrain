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

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

import org.luwrain.controls.MultilineEdit2.ModificationResult;

public class ScriptMultilineEditCorrector implements MultilineEditCorrector2
{
    protected final MultilineEditCorrector2 base;

    public ScriptMultilineEditCorrector(MultilineEditCorrector2 base)
    {
	NullCheck.notNull(base, "base");
	this.base = base;
    }

    @Override public String getLine(int index)
    {
	return base.getLine(index);
    }

    @Override public int getLineCount()
    {
	return base.getLineCount();
    }

    @Override public int getHotPointX()
    {
	return base.getHotPointX();
    }

    @Override public int getHotPointY()
    {
	return base.getHotPointY();
    }

    @Override public String getTabSeq()
    {
	return base.getTabSeq();
    }

    @Override public ModificationResult deleteChar(int pos, int lineIndex)
    {
	return null;
    }

    @Override public ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return null;
    }

    @Override public ModificationResult insertRegion(int x, int y, String[] lines)
    {
	return null;
    }

    @Override public ModificationResult putChars(int pos, int lineIndex, String str)
    {
	return null;
    }

    @Override public ModificationResult mergeLines(int firstLineIndex)
    {
	return null;
    }

    @Override public ModificationResult splitLine(int pos, int lineIndex)
    {
	return null;
    }

    @Override public ModificationResult doEditAction(TextEditAction action)
    {
	return null;
    }
    }
