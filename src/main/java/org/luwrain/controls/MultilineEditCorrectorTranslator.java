/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>
clas
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

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.controls.MultilineEdit.ModificationResult;
import org.luwrain.util.*;

// Expects that hot point is not related to the content 
// Hot point position may be adjusted to the content changes only on endEditTrans 

//Keeps lines empty if it is possible providing a fake  first line to be consistent, as it is required by MultilineEdit.Model
public class MultilineEditCorrectorTranslator implements MultilineEditCorrector2
{
    protected final MutableLines lines;
    protected final HotPointControl hotPoint;
    protected String tabSeq = "\t";

    public MultilineEditCorrectorTranslator(MutableLines lines, HotPointControl hotPoint)
    {
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(hotPoint, "hotPoint");
	this.lines = lines;
	this.hotPoint = hotPoint;
    }

    public MultilineEditCorrectorTranslator(MutableLines lines, HotPointControl hotPoint,
					String tabSeq)
    {
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(hotPoint, "hotPoint");
	NullCheck.notNull(tabSeq, "tabSeq");
	this.lines = lines;
	this.hotPoint = hotPoint;
	this.tabSeq = tabSeq;
    }

    @Override public int getHotPointX()
    {
	return hotPoint.getHotPointX();
    }

    @Override public int getHotPointY()
    {
	return hotPoint.getHotPointY();
    }

    @Override public int getLineCount()
    {
	final int count = lines.getLineCount();
	return count > 0?count:1;
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (index == 0 && lines.getLineCount() == 0)
	    return "";
	return lines.getLine(index);
    }

    @Override public String getTabSeq()
    {
	return tabSeq;
    }

    @Override public ModificationResult deleteChar(int pos, int lineIndex)
    {
	if (pos < 0 || lineIndex < 0)
	    throw new IllegalArgumentException("pos (" + pos + ") and lineIndex (" + lineIndex + ") may not be negative");
	final int lineCount = lines.getLineCount();
	if (lineIndex >= lineCount)
	    throw new IllegalArgumentException("lineIndex (" + lineIndex + ") must be less than the number of lines (" + lineCount + ")");
	final String line = lines.getLine(lineIndex);
	NullCheck.notNull(line, "line");
	if (pos >= line.length())
	    throw new IllegalArgumentException("pos (" + pos + ") must be less than the length of the line (" + line.length() + ")");
	beginEditTrans();
	lines.setLine(lineIndex, line.substring(0, pos) + line.substring(pos + 1));
	if (hotPoint.getHotPointY() == lineIndex && hotPoint.getHotPointX() > pos)
	    hotPoint.setHotPointX(hotPoint.getHotPointX() - 1);
	endEditTrans(true);
	return new ModificationResult(true, line.charAt(pos));
    }

    @Override public ModificationResult deleteRegion(int fromX, int fromY,
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
	    beginEditTrans();
	    lines.setLine(fromY, line.substring(0, fromPos) + line.substring(toPos));
	    if (hotPoint.getHotPointY() == fromY)
	    {
		if (hotPoint.getHotPointX() >= fromPos && hotPoint.getHotPointX() < toPos)
		    hotPoint.setHotPointX(fromPos); else
		    if (hotPoint.getHotPointX() >= toPos)
			hotPoint.setHotPointX(hotPoint.getHotPointX() - (toPos - fromPos));
	    }
	    endEditTrans(true);
	    return new ModificationResult(true);
	}
	final String firstLine = lines.getLine(fromY);
					 NullCheck.notNull(firstLine, "firstLine");
					 final int fromPos = Math.min(fromX, firstLine.length());
	final String endingLine = lines.getLine(toY);
					 NullCheck.notNull(endingLine, "endingLine");
					 final int toPos = Math.min(toX, endingLine.length());
	beginEditTrans();
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
	endEditTrans(true);
	return new ModificationResult(true);
    }

    @Override public ModificationResult insertRegion(int x, int y, String[] text)
    {
	NullCheck.notNullItems(text, "text");
	checkPos(x, y);
	if (text.length == 0)
	    return new ModificationResult(true);
	final String firstLine = text[0];
	final String lastLine = text[text.length - 1];
	if (y == 0 && x == 0 && lines.getLineCount() == 0)
	{
	    beginEditTrans();
	    for(int i = 0;i < text.length;++i)
		lines.addLine(text[i]);
	    hotPoint.setHotPointX(text[text.length - 1].length());
	    hotPoint.setHotPointY(lines.getLineCount() - 1);
	    endEditTrans(false);
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

    //??Adds empty line with pos=0 and line=0 if previously there were no lines at all
    @Override public ModificationResult putChars(int pos, int lineIndex, String str)
    {
	NullCheck.notNull(str, "str");
	checkPos(pos, lineIndex);
	beginEditTrans();
	try {
	    if (pos == 0 && lineIndex == 0 && lines.getLineCount() == 0)
		lines.addLine("");
	    final int count = lines.getLineCount();
	    if (lineIndex >= count)
		throw new IllegalArgumentException("lineIndex (" + lineIndex + ") must be less then the number of lines (" + count + ")");
	    final String line = lines.getLine(lineIndex);
	    if (pos > line.length())
		throw new IllegalArgumentException("pos (" + pos + ") may not be greater than the length of the line (" + line.length() + ")");
	    lines.setLine(lineIndex, line.substring(0, pos) + str + line.substring(pos));
	    if (hotPoint.getHotPointY() == lineIndex && hotPoint.getHotPointX() >= pos)
		hotPoint.setHotPointX(hotPoint.getHotPointX() + (str != null?str.length():0));
	}
	finally {
	    endEditTrans(false);
	}
	if (str.length() == 1 && Character.isSpace(str.charAt(0)))
	{
	    final String word = getWordPriorTo(pos, lineIndex);
	    if (!word.isEmpty())
		return new ModificationResult(true, word, str.charAt(0));
	    return new ModificationResult(true, str.charAt(0));
	}
	if (str.length() == 1)
	    return new ModificationResult(true, str.charAt(0));
	return new ModificationResult(true, str);
    }

    @Override public ModificationResult mergeLines(int firstLineIndex)
    {
	if (firstLineIndex < 0)
	    throw new IllegalArgumentException("firstLineIndex (" + firstLineIndex + ") may not be negative");
	final int lineCount = lines.getLineCount();
	if (firstLineIndex + 1 >= lineCount)
	    throw new IllegalArgumentException("firstLineIndex (" + firstLineIndex + ") + 1 must be less than the number of lines (" + lineCount + ")");
	beginEditTrans();
	final String firstLine = lines.getLine(firstLineIndex);
	NullCheck.notNull(firstLine, "firstLine");
	final int origLineLen = firstLine.length();
	lines.setLine(firstLineIndex, firstLine + lines.getLine(firstLineIndex + 1));
	lines.removeLine(firstLineIndex + 1);
	if (hotPoint.getHotPointY() == firstLineIndex + 1)
	{
	    hotPoint.setHotPointY(hotPoint.getHotPointY() - 1);
	    hotPoint.setHotPointX(hotPoint.getHotPointX() + origLineLen);
	} else
	    if (hotPoint.getHotPointY() > firstLineIndex + 1)
		hotPoint.setHotPointY(hotPoint.getHotPointY() - 1);
	endEditTrans(true);
	return new ModificationResult(true);
    }

    @Override public ModificationResult splitLine(int pos, int lineIndex)
    {
	checkPos(pos, lineIndex);
	beginEditTrans();
	final String newLine;
	try {
	    if (pos == 0 && lineIndex == 0 && lines.getLineCount() == 0)
		lines.addLine("");
	    final int lineCount = lines.getLineCount();
	    if (lineIndex >= lineCount)
		throw new IllegalArgumentException("lineIndex (" + lineIndex + ") must be less than the number of lines (" + lineCount + ")");
	    final String line = lines.getLine(lineIndex);
	    NullCheck.notNull(line, "line");
	    if (pos > line.length())
		throw new IllegalArgumentException("pos (" + pos + ") may not be negative than the length of the line (" + line.length() + ")");
	    lines.setLine(lineIndex, line.substring(0, pos));
	    newLine = line.substring(pos);
	    lines.insertLine(lineIndex + 1, newLine);
	    if (hotPoint.getHotPointY() == lineIndex && hotPoint.getHotPointX() >= pos)
	    {
		hotPoint.setHotPointY(lineIndex + 1);
		hotPoint.setHotPointX(hotPoint.getHotPointX() - pos);
	    } else
		if (hotPoint.getHotPointY() > lineIndex)
		    hotPoint.setHotPointY(hotPoint.getHotPointY() + 1);
	}
	finally {
	    endEditTrans(false);
	}
	return new ModificationResult(true, newLine);
    }

    @Override public ModificationResult doEditAction(TextEditAction action)
    {
	NullCheck.notNull(action, "action");
	action.doTextEdit(lines, hotPoint);
	return new ModificationResult(true);
    }

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
	lines.beginLinesTrans();
	hotPoint.beginHotPointTrans();
    }

    protected void endEditTrans(boolean cleanSingleEmptyLine)
    {
	if (cleanSingleEmptyLine)
	    if (lines.getLineCount() == 1 && lines.getLine(0).isEmpty())
		lines.removeLine(0);
	hotPoint.endHotPointTrans();
	lines.endLinesTrans();
    }
}
