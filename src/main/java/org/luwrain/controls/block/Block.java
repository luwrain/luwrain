/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls.block;

import java.util.*;
import java.awt.Rectangle;

import org.luwrain.core.*;
import org.luwrain.browser.*;

public class Block
{
    int textX = -1;
    int textY = -1;
    int textWidth = -1;
    int textHeight = -1;

    protected final BlockObject[] objs;
    protected BlockRow[] rows = new BlockRow[0];

        final List<Block> vertDepOn = new LinkedList();
    boolean actualTextY = false;

    public Block(BlockObject[] objs)
    {
	NullCheck.notNullItems(objs, "objs");
	this.objs = objs;
    }

    public BlockObject[] getObjs()
    {
	return objs;
    }

    public void setRows(BlockRow[] rows)
    {
	NullCheck.notNullItems(rows, "rows");
	this.rows = rows.clone();
	this.textHeight = rows.length;
    }

    BlockRow[] getRows()
    {
	return rows.clone();
    }

    int getRowCount()
    {
	return rows.length;
    }

    BlockRowFragment[] getRow(int index)
    {
	return rows[index].getFragments();
    }

        boolean intersectsText(Block c)
    {
	NullCheck.notNull(c, "c");
	final int sq1 = getTextSquare();
	final int sq2 = c.getTextSquare();
	if (sq1 == 0 && sq2 == 0)
	    return textX == c.textX && textY == c.textY;
	if (sq1 == 0)
	    return between(textX, c.textX, c.textX + c.textWidth) && between(textY, c.textY, c.textY + c.textHeight);
	if (sq2 == 0)
	    return between(c.textX, textX, textX + textWidth) && between(c.textY, textY, textY + textHeight);
	return intersects(textX, textWidth, c.textX, c.textWidth) &&
	intersects(textY, textHeight, c.textY, c.textHeight);
    }

    static public boolean between(int pos, int from, int to)
    {
	return pos >= from && pos < to;
    }

    static public boolean intersects(int start1, int len1, int start2, int len2)
    {
	if (start1 < start2)
	    return start2 >= start1 && start2 < start1 + len1; else
	    return start1 >= start2 && start1 < start2 + len2;
    }

        public int getTextSquare()
    {
	return textWidth * textHeight;
    }

    void calcActualTextY()
    {
	if (actualTextY)
	    return;
	for(Block c: vertDepOn)
	    c.calcActualTextY();
	int maxPos = 0;
	for(Block c: vertDepOn)
	    maxPos = Math.max(maxPos, c.textY + c.textHeight);
	this.textY = maxPos + 1;
		actualTextY = true;
    }
}
