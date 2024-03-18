/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls.edit;

import org.luwrain.core.*;
import org.luwrain.controls.edit.MultilineEdit.ModificationResult;
import org.luwrain.util.*;
import org.luwrain.controls.edit.MultilineCorrector.*;
    
import static org.luwrain.core.NullCheck.*;

// Expects that hot point is not related to the content 
// Hot point position may be adjusted to the content changes only on endEditTrans 
//Keeps lines empty if it is possible providing a fake  first line to be consistent, as it is required by MultilineEdit.Model
public class MultilineTranslator
{
    protected final MutableLines lines;
    protected final HotPointControl hotPoint;
    //    protected String tabSeq = "\t";
    protected final boolean adjustHotPoint;

    MultilineTranslator(MutableLines lines, HotPointControl hotPoint, boolean adjustHotPoint)
    {
	notNull(lines, "lines");
	notNull(hotPoint, "hotPoint");
	this.lines = lines;
	this.hotPoint = hotPoint;
	this.adjustHotPoint = adjustHotPoint;
    }

    public MultilineTranslator(MutableLines lines, HotPointControl hotPoint)
    {
	this(lines, hotPoint, true);
    }

    /*
    public MultilineTranslator(MutableLines lines, HotPointControl hotPoint, String tabSeq)
    {
	notNull(lines, "lines");
	notNull(hotPoint, "hotPoint");
	notNull(tabSeq, "tabSeq");
	this.lines = lines;
	this.hotPoint = hotPoint;
	this.tabSeq = tabSeq;
    }
    */

    public void change(MultilineCorrector.Change c)
    {
	notNull(c, "c");
	if (c instanceof DeleteCharChange dc)
	{
	    dc.setResult(deleteChar(dc.getLine(), dc.getPos()));
	    return;
	}

	if (c instanceof InsertCharsChange ic)
	{
	    ic.setResult(insertChars(ic.getLine(), ic.getPos(), ic.getChars()));
	    return;
	    	}

	    	if (c instanceof MergeLinesChange ml)
	{
	    ml.setResult(mergeLines(ml.getLine()));
	    return;
	}

			    	if (c instanceof SplitLineChange sl)
	{
	    sl.setResult(splitLine(sl.getLine(), sl.getPos()));
	    return;
	}
    }

    //Added
    protected ModificationResult deleteChar(int line, int pos)
    {
	if (pos < 0 || line < 0)
	    throw new IllegalArgumentException("pos (" + pos + ") and line (" + line + ") may not be negative");
	final int lineCount = lines.getLineCount();
	if (line >= lineCount)
	    throw new IllegalArgumentException("line (" + line + ") must be less than the number of lines (" + lineCount + ")");
	final String l = lines.getLine(line);
	if (l == null)
	    throw new NullPointerException("null line in the Lines object at position " + line);
	if (pos >= l.length())
	    throw new IllegalArgumentException("pos (" + pos + ") must be less than the length of the line (" + l.length() + ")");
	try (var op = operation(true)){
	    lines.setLine(line, l.substring(0, pos) + l.substring(pos + 1));
	    if (hotPoint.getHotPointY() == line && hotPoint.getHotPointX() > pos)
		hotPoint.setHotPointX(hotPoint.getHotPointX() - 1);
	}
	return new ModificationResult(true, l.charAt(pos));
    }

    ModificationResult deleteRegion(int fromX, int fromY,
				    int toX, int toY)
    {
	if (lines.getLineCount() < 1 || fromY > toY ||
	    (fromY == toY && fromX > toX) ||
	    toY >= lines.getLineCount())
	    return new ModificationResult(false);
	if (fromY == toY)
	{
	    final String line = lines.getLine(fromY);
	    NullCheck.notNull(line, "line");
	    if (line.isEmpty())
		return new ModificationResult(false);
	    final int fromPos = Math.min(fromX, line.length());
	    final int toPos = Math.min(toX, line.length());
	    if (fromPos >= toPos)
		return new ModificationResult(false);
	    try(var op = operation(true)){
		lines.setLine(fromY, line.substring(0, fromPos) + line.substring(toPos));
		if (hotPoint.getHotPointY() == fromY)
		{
		    if (hotPoint.getHotPointX() >= fromPos && hotPoint.getHotPointX() < toPos)
			hotPoint.setHotPointX(fromPos); else
			if (hotPoint.getHotPointX() >= toPos)
			    hotPoint.setHotPointX(hotPoint.getHotPointX() - (toPos - fromPos));
		}
	    }
	    return new ModificationResult(true);
	}
	final String firstLine = lines.getLine(fromY);
	NullCheck.notNull(firstLine, "firstLine");
	final int fromPos = Math.min(fromX, firstLine.length());
	final String endingLine = lines.getLine(toY);
	NullCheck.notNull(endingLine, "endingLine");
	final int toPos = Math.min(toX, endingLine.length());
	try (var op = operation(true)){
	    lines.setLine(fromY, firstLine.substring(0, fromPos) + endingLine.substring(toPos));
	    for(int i = fromY + 1;i <= toY;++i)
		lines.removeLine(fromY + 1);
	    if ((hotPoint.getHotPointY() == fromY && hotPoint.getHotPointX() >= fromPos) ||
		(hotPoint.getHotPointY() > fromY && hotPoint.getHotPointY() < toY) ||
		(hotPoint.getHotPointY() == toY && hotPoint.getHotPointX() < toX))
	    {
		hotPoint.setHotPointY(fromY);
		hotPoint.setHotPointX(fromX);
	    } else
		if (hotPoint.getHotPointY() == toY && hotPoint.getHotPointX() >= toPos)
		{
		    hotPoint.setHotPointY(fromY);
		    hotPoint.setHotPointX(hotPoint.getHotPointX() - toPos + fromPos);
		} else
		    if (hotPoint.getHotPointY() > toY)
			hotPoint.setHotPointY(hotPoint.getHotPointY() - toY + fromY);
	}
	return new ModificationResult(true);
    }

    ModificationResult insertRegion(int x, int y, String[] text)
    {
	NullCheck.notNullItems(text, "text");
	checkPos(x, y);
	if (text.length == 0)
	    return new ModificationResult(true);
	final String firstLine = text[0];
	final String lastLine = text[text.length - 1];
	if (y == 0 && x == 0 && lines.getLineCount() == 0)
	{
	    try (var op = operation(false)){
		for(int i = 0;i < text.length;++i)
		    lines.addLine(text[i]);
		hotPoint.setHotPointX(text[text.length - 1].length());
		hotPoint.setHotPointY(lines.getLineCount() - 1);
	    }
	    return new ModificationResult(true);
	} //no previous content
	//Checking if there is no need to split the line
	if (text.length == 1)
	{
	    final String line = lines.getLine(y);
	    //If the insertion happens before the current position of the hot point
	    final boolean needToMoveHotPoint = (hotPoint.getHotPointY() == y && x <= hotPoint.getHotPointX());
	    beginEditTrans();
	    lines.setLine(y, line.substring(0, x) + firstLine + line.substring(x));
	    if (needToMoveHotPoint)
		hotPoint.setHotPointX(hotPoint.getHotPointX() + firstLine.length());
	    endEditTrans(false);
	    return new ModificationResult(true);
	}
	//The new text has multiple lines
	final String line = lines.getLine(y);
	beginEditTrans();
	lines.setLine(y, line.substring(0, x) + text[0]);
	for(int i = 1;i < text.length - 1;++i)
	    lines.insertLine(y + i, text[i]);
	lines.insertLine(y+ text.length - 1, text[text.length - 1] + line.substring(x));
	if (hotPoint.getHotPointY() > y)
	    hotPoint.setHotPointY(hotPoint.getHotPointY() + text.length - 1); else
	    if (hotPoint.getHotPointY() == y && hotPoint.getHotPointX() >= x)
	    {
		hotPoint.setHotPointY(y + text.length - 1);
		hotPoint.setHotPointX(hotPoint.getHotPointX() - x + lastLine.length());
	    }
	endEditTrans(false);
	return new ModificationResult(true);
    }

    //Added
    //??Adds empty line with pos=0 and line=0 if previously there were no lines at all
    ModificationResult insertChars(int line , int pos, String str)
    {
	notNull(str, "str");
	checkPos(pos, line);
	try (var op = operation(false)){
	    if (pos == 0 && line == 0 && lines.getLineCount() == 0)
		lines.addLine("");
	    final int count = lines.getLineCount();
	    if (line >= count)
		throw new IllegalArgumentException("line (" + line + ") must be less then the number of lines (" + count + ")");
	    final String l = lines.getLine(line);
	    if (pos > l.length())
		throw new IllegalArgumentException("pos (" + pos + ") may not be greater than the length of the line (" + l.length() + ")");
	    lines.setLine(line, l.substring(0, pos) + str + l.substring(pos));
	    if (hotPoint.getHotPointY() == line && hotPoint.getHotPointX() >= pos)
		hotPoint.setHotPointX(hotPoint.getHotPointX() + (str != null?str.length():0));
	}
	if (str.length() == 1 && Character.isWhitespace(str.charAt(0)))
	{
	    final String word = getWordPriorTo(pos, line);
	    if (!word.isEmpty())
		return new ModificationResult(true, word, str.charAt(0));
	    return new ModificationResult(true, str.charAt(0));
	}
	if (str.length() == 1)
	    return new ModificationResult(true, str.charAt(0));
	return new ModificationResult(true, str);
    }

    //Edited
    private ModificationResult mergeLines(int firstLine)
    {
	if (firstLine < 0)
	    throw new IllegalArgumentException("firstLine (" + String.valueOf(firstLine) + ") can't be negative");
	final int lineCount = lines.getLineCount();
	if (firstLine + 1 >= lineCount)
	    throw new IllegalArgumentException("firstLine (" + String.valueOf(firstLine) + ") + 1 must be less than the number of lines (" + String.valueOf(lineCount) + ")");
	try (var op = operation(true)) {
	    final String line = lines.getLine(firstLine);
	    NullCheck.notNull(line, "line");
	    final int origLineLen = line.length();
	    lines.setLine(firstLine, line + lines.getLine(firstLine + 1));
	    lines.removeLine(firstLine + 1);
	    if (hotPoint.getHotPointY() == firstLine + 1)
	    {
		hotPoint.setHotPointY(hotPoint.getHotPointY() - 1);
		hotPoint.setHotPointX(hotPoint.getHotPointX() + origLineLen);
	    } else
		if (hotPoint.getHotPointY() > firstLine + 1)
		    hotPoint.setHotPointY(hotPoint.getHotPointY() - 1);
	}
	return new ModificationResult(true);
    }

    private ModificationResult splitLine(int line, int pos)
    {
	checkPos(pos, line);
	try (var op = operation(false)){
		//Adding the line to the empty lines list
	    if (pos == 0 && line == 0 && lines.getLineCount() == 0)
		lines.addLine("");
	    final int lineCount = lines.getLineCount();
	    if (line >= lineCount)
		throw new IllegalArgumentException("The index of the line to split (" + String.valueOf(line) + ") must be less than the number of lines (" + String.valueOf(lineCount) + ")");
	    final String l = lines.getLine(line);
	    NullCheck.notNull(l, "l");
	    if (pos > l.length())
		throw new IllegalArgumentException("pos (" + String.valueOf(pos) + ") can't be greater than the length of the line (" + String.valueOf(l.length()) + ")");
	    lines.setLine(line, l.substring(0, pos));
	    final String newLine = l.substring(pos);
	    lines.insertLine(line + 1, newLine);
	    if (hotPoint.getHotPointY() == line && hotPoint.getHotPointX() >= pos)
	    {
		hotPoint.setHotPointY(line + 1);
		hotPoint.setHotPointX(hotPoint.getHotPointX() - pos);
	    } else
		if (hotPoint.getHotPointY() > line)
		    hotPoint.setHotPointY(hotPoint.getHotPointY() + 1);
	    	return new ModificationResult(true, newLine);
	}
    }

    protected int getLineCount()
    {
	final int count = lines.getLineCount();
	return count > 0?count:1;
    }

    protected String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (index == 0 && lines.getLineCount() == 0)
	    return "";
	return lines.getLine(index);
    }

    /*
    int getHotPointX() { return hotPoint.getHotPointX(); }
    int getHotPointY() {return hotPoint.getHotPointY(); }
    String getTabSeq() { return tabSeq; }
    */

    protected String getWordPriorTo(int pos, int lineIndex)
    {
	String res = TextUtils.getLastWord(getLine(lineIndex), pos);
	if (!res.isEmpty())
	    return res;
	if (lineIndex == 0)
	    return "";
	for(int i = lineIndex - 1;i >= 0;i--)
	{
	    final String line = getLine(i);
	    res = TextUtils.getLastWord(line, line.length());
	    if (!res.isEmpty())
		return res;
	}
	return ""; 
    }

    protected void checkPos(int pos, int lineIndex)
    {
	if (pos < 0 || lineIndex < 0)
	    throw new IllegalArgumentException("pos (" + pos + ") and lineIndex (" + lineIndex + ") may not be negative");
	if (lines.getLineCount() == 0 && pos == 0 && lineIndex == 0)
	    return;
	if (lineIndex >= lines.getLineCount())
	    throw new IllegalArgumentException("lineIndex (" + lineIndex + ") may not be equal or greater than " + lines.getLineCount());
	final String line = lines.getLine(lineIndex);
	NullCheck.notNull(line, "line");
	if (pos > line.length())
	    throw new IllegalArgumentException("pos (" + pos + ") may not be greater than " + line.length());
    }

    protected void beginEditTrans()
    {
	hotPoint.beginHotPointTrans();
    }

    protected void endEditTrans(boolean cleanSingleEmptyLine)
    {
	if (cleanSingleEmptyLine)
	    if (lines.getLineCount() == 1 && lines.getLine(0).isEmpty())
		lines.removeLine(0);
	hotPoint.endHotPointTrans();
    }

    protected OperationFinishing operation(boolean cleanEmptyLine)
    {
	if (adjustHotPoint)
	{
	//Validating the hot point
	if (hotPoint.getHotPointX() < 0)
	    hotPoint.setHotPointX(0);
	if (hotPoint.getHotPointY() < 0)
	    hotPoint.setHotPointY(0);
	//Hot point is below the last line
	if (hotPoint.getHotPointY() >= lines.getLineCount())
	{
	    if (lines.getLineCount() == 0)
		hotPoint.setHotPointY(0); else
		hotPoint.setHotPointY(lines.getLineCount() - 1);
	}
	//Checking the position on the line
	    if (lines.getLineCount() > 0)
	    {
		final String line = lines.getLine(hotPoint.getHotPointY());
		if (line == null)
		    throw new NullPointerException("The line with the index " + String.valueOf(hotPoint.getHotPointY()) + " is null");
		if (hotPoint.getHotPointX() > line.length())
		    hotPoint.setHotPointX(line.length());
	} else
	    if (hotPoint.getHotPointX() != 0)
		hotPoint.setHotPointX(0);
	}
	beginEditTrans();
	return new OperationFinishing(cleanEmptyLine);
    }

    protected final class OperationFinishing implements AutoCloseable
    {
	final boolean cleanEmptyLine;
	OperationFinishing(boolean cleanEmptyLine) { this.cleanEmptyLine = cleanEmptyLine; }
	@Override public void close() { endEditTrans(cleanEmptyLine); }
    }
}
