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
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.browser.*;

public final class View implements Lines
{
    private final Block[] blocks;
    private final String[] lines;

    View(BlockArea.Appearance appearance, Block[] blocks)
    {
	NullCheck.notNull(appearance, "appearance");
	NullCheck.notNullItems(blocks, "blocks");
	this.blocks = blocks;
	this.lines = buildLines(blocks, appearance);
    }

    boolean isEmpty()
    {
	return blocks.length == 0;
    }

    @Override public int getLineCount()
    {
	return lines.length > 0?lines.length:1;
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (index > lines.length)
	    return "";
	return lines[index];
    }

    Block getBlock(int index)
    {
	return blocks[index];
    }

    public int getBlockCount()
    {
	return blocks.length;
    }

    Iterator createIterator()
    {
	if (isEmpty())
	    return null;
	return new Iterator(this, 0);
    }

    static private String[] buildLines(Block[] blocks, BlockArea.Appearance appearance)
    {
	NullCheck.notNullItems(blocks, "blocks");
	NullCheck.notNull(appearance, "appearance");
	int lineCount = 0;
	for(Block c: blocks)
	    lineCount = Math.max(lineCount, c.textY + c.textHeight);
	//Log.debug(LOG_COMPONENT, "preparing " + lineCount + " lines");
	final String[] lines = new String[lineCount];
	for(int i = 0;i < lineCount;++i)
	    lines[i] = "";
	for(Block c: blocks)
	{
	    for(int i = 0;i < c.rows.length;++i)
	    {
		final BlockRow row = c.rows[i];
		final String text = appearance.getRowTextAppearance(row.getFragments());
		//if (text.length() > c.textWidth)
		    //Log.warning(LOG_COMPONENT, "row text \'" + text + "\' is longer than the width of the container (" + c.textWidth + ")");
		final int lineIndex = c.textY + i;
		lines[lineIndex] = putString(lines[lineIndex], text, c.textX);	
	    }
	}
	return lines;
    }

	static private String putString(String s, String fragment, int pos)
	{
	    NullCheck.notNull(s, "s");
	    NullCheck.notNull(fragment, "fragment");
	    final StringBuilder b = new StringBuilder();
	    if (pos > s.length())
	    {
		b.append(s);
		for(int i = s.length();i < pos;++i)
		    b.append(" ");
	    } else
		b.append(s.substring(0, pos));
	    b.append(fragment);
	    if (pos + fragment.length() < s.length())
		b.append(s.substring(pos + fragment.length()));
	    return new String(b);
	}

    static final class Iterator
    {
	private final View view;
	private int pos = 0;
	private Block block = null;

	Iterator(View view, int pos)
	{
	    NullCheck.notNull(view, "view");
	    if (pos < 0)
		throw new IllegalArgumentException("pos (" + pos + ") may not be negative");
	    this.view = view;
	    this.pos = pos;
	    this.block = view.getBlock(this.pos);
	}

	public Block getBlock()
	{
	    return block;
	}

	int getX()
	{
	    return block.textX;
	}

	int getY()
	{
	    return block.textY;
	}

	int getRowCount()
	{
	    return block.getRowCount();
	}

	BlockRowFragment[] getRow(int index)
	{
	    return block.getRow(index);
	}

	boolean isLastRow(int index)
	{
	    return block.getRowCount() > 0 && index + 1 == block.getRowCount();
	}

		boolean movePrev()
	{
	    if (pos == 0)
		return false;
	    --pos;
block = view.getBlock(pos);
	    return true;
	}

	boolean moveNext()
	{
	    if (pos + 1 >= view.getBlockCount())
		return false;
	    ++pos;
block = view.getBlock(pos);
	    return true;
	}
    }
}
