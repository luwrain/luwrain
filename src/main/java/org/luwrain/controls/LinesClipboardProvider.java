/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

public class LinesClipboardProvider implements ClipboardTranslator.Provider
{
    public interface ClipboardSource
    {
	Clipboard getClipboard();
    }

    protected final Lines lines;
    protected final ClipboardSource clipboardSource;

    public LinesClipboardProvider(Lines lines, ClipboardSource clipboardSource)
    {
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(clipboardSource, "clipboardSource");
	this.lines = lines;
	this.clipboardSource = clipboardSource;
    }

    @Override public boolean onClipboardCopyAll()
    {
	final List<String> res = new LinkedList<String>();
	final int count = lines.getLineCount();
	if (count < 1)
	{
	    clipboardSource.getClipboard().set(new String[0]);
	    return true;
	}
	for(int i = 0;i < count;++i)
	{
	    final String line = lines.getLine(i);
	    if (line == null)
		return false;
	    res.add(line);
	}
	clipboardSource.getClipboard().set(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
    {
	if (withDeleting)
	    return false;
	final int count = lines.getLineCount();
	if (count < 1)
	    return false;
	if (fromY >= count || toY > count || fromY > toY)
	    return false;
	if (fromY == toY)
	{
	    final String line = lines.getLine(fromY);
	    if (line == null)
		return false;
	    final int fromPos = Math.min(fromX, line.length());
	    final int toPos = Math.min(toX, line.length());
	    if (fromPos >= toPos)
		return false;
	    clipboardSource.getClipboard().set(line.substring(fromPos, toPos));
	    return true;
	}
	final List<String> res = new LinkedList<String>();
	final String firstLine = lines.getLine(fromY);
	if (firstLine == null)
	    return false;
	res.add(firstLine.substring(Math.min(fromX, firstLine.length())));
	for(int i = fromY + 1;i < toY;++i)
	{
	    final String line = lines.getLine(i);
	    if (line == null)
		return false;
	    res.add(line);
	}
	final String lastLine = lines.getLine(toY);
	if (lastLine == null)
	    return false;
	res.add(lastLine.substring(0, Math.min(toX, lastLine.length())));
	clipboardSource.getClipboard().set(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onDeleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return false;
    }
}
