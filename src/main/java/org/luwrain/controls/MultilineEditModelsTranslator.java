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

// Expects that hot point is not related to the content 
// Hot point position may be adjusted to the content changes only on endEditTrans 

public class MultilineEditModelsTranslator implements MultilineEditHighLevelModel
{
    private MultilineEditLowLevelModel model;

    public MultilineEditModelsTranslator(MultilineEditLowLevelModel model)
    {
	this.model = model;
	NullCheck.notNull(model, "model");
    }

    @Override public int getHotPointX()
    {
	return model.getHotPointX();
    }

    @Override public int getHotPointY()
    {
	return model.getHotPointY();
    }

    @Override public int getLineCount()
    {
	return model.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return model.getLine(index);
    }

    @Override public String getTabSeq()
    {
	return model.getTabSeq();
    }

    @Override public char deleteChar(int pos, int lineIndex)
    {
	final String line = model.getLine(lineIndex);
	if (line == null ||
	    pos < 0 || pos>= line.length())
	{
	    if (model.getHotPointY() == lineIndex && model.getHotPointX() > pos)
	    {
		model.beginEditTrans();
		model.setHotPointX(model.getHotPointX() - 1);
		model.endEditTrans();
	    }
	    return '\0';
	}
	model.beginEditTrans();
	model.setLine(lineIndex, line.substring(0, pos) + line.substring(pos + 1));
	if (model.getHotPointY() == lineIndex && model.getHotPointX() > pos)
	    model.setHotPointX(model.getHotPointX() - 1);
	model.endEditTrans();
	return line.charAt(pos);
    }

    @Override public boolean deleteRegion(int fromX, int fromY,
					  int toX, int toY)
    {
	if (model.getLineCount() < 1 ||
	    fromY > toY ||
	    (fromY == toY && fromX > toX) ||
	    toY >= model.getLineCount())
	    return false;
	if (fromY == toY)
	{
	    final String line = model.getLine(fromY);
	    if (line == null || line.isEmpty())
		return false;
	    final int fromPos = fromX < line.length()?fromX:line.length();
	    final int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		return false;
	    model.beginEditTrans();
	    model.setLine(fromY, line.substring(0, fromPos) + line.substring(toPos));
	    if (model.getHotPointY() == fromY)
	    {
		if (model.getHotPointX() >= fromPos && model.getHotPointX() < toPos)
		    model.setHotPointX(fromPos); else
		    if (model.getHotPointX() >= toPos)
			model.setHotPointX(model.getHotPointX() - (toPos - fromPos));
	    }
	    model.endEditTrans();
	    return true;
	}
	final String firstLine = model.getLine(fromY);
	if (firstLine == null)
	    return false;
	final int fromPos = fromX < firstLine.length()?fromX:firstLine.length();
	final String endingLine = model.getLine(toY);
	if (endingLine == null)
	    return false;
	final int toPos = toX <endingLine.length()?toX:endingLine.length();
	model.beginEditTrans();
	model.setLine(fromY, firstLine.substring(0, fromPos) + endingLine.substring(toPos));
	for(int i = fromY + 1;i <= toY;++i)
	    model.removeLine(fromY + 1);
	if ((model.getHotPointY() == fromY && model.getHotPointX() >= fromPos) ||
	    (model.getHotPointY() > fromY && model.getHotPointY() < toY) ||
	    (model.getHotPointY() == toY && model.getHotPointX() < toX))
	{
	    model.setHotPointY(fromY);
	    model.setHotPointX(fromX);
	} else
	    if (model.getHotPointY() == toY && model.getHotPointX() >= toPos)
	    {
		model.setHotPointY(fromY);
		model.setHotPointX(model.getHotPointX() - toPos + fromPos);
	    } else
		if (model.getHotPointY() > toY)
		    model.setHotPointY(model.getHotPointY() - toY + fromY);
	model.endEditTrans();
	return true;
    }

    @Override public boolean insertRegion(int x, int y, String[] lines)
    {
	final String[] text = org.luwrain.util.Strings.notNullArray(lines);
	if (text.length < 1)
	    return true;
	final String firstLine = text[0];
	final String lastLine = text[text.length - 1];
	if (y >= model.getLineCount())
	{
	    final boolean needToMoveHotPoint = model.getHotPointY() > model.getLineCount();
	    model.beginEditTrans();
	    while(model.getLineCount() < y)
		model.addLine("");
	    if (x > 0)
		model.addLine(org.luwrain.util.Strings.sameCharString(' ', x) + text[0]); else
		model.addLine(text[0]);
	    for(int i = 1;i < text.length;++i)
		model.addLine(text[i]);
	    if (needToMoveHotPoint)
	    model.setHotPointX(text[text.length - 1].length());
	    model.setHotPointY(model.getLineCount() - 1);
	    model.endEditTrans();
	    return true;
	}
	if (text.length == 1)
	{
	    String line = model.getLine(y);
	    if (line == null)
		line = "";
	    while (line.length() < x)
		line += ' ';
	    final boolean needToMoveHotPoint = (model.getHotPointY() == y && x >= model.getHotPointX());
	    model.beginEditTrans();
	    model.setLine(y, line.substring(0, x) + firstLine + line.substring(x));
			  if (needToMoveHotPoint)
			      model.setHotPointX(model.getHotPointX() + firstLine.length());
	    model.endEditTrans();
	    return true;
	}
	//New text has multiple lines
	    String line = model.getLine(y);
	    if (line == null)
		line = "";
	    while (line.length() < x)
		line += ' ';
	    model.beginEditTrans();
	model.setLine(y, line.substring(0, x) + text[0]);
	for(int i = 1;i < text.length - 1;++i)
	    model.insertLine(y + i, text[i]);
	model.insertLine(y+ text.length - 1, text[text.length - 1] + line.substring(x));
	if (model.getHotPointY() > y)
	    model.setHotPointY(model.getHotPointY() + text.length - 1); else
	    if (model.getHotPointY() == y && model.getHotPointX() >= x)
	    {
		model.setHotPointY(y + text.length - 1);
		model.setHotPointX(model.getHotPointX() - x + lastLine.length());
	    }
	model.endEditTrans();
	return true;
    }

    @Override public void insertChars(int pos, int lineIndex, String str)
    {
	String line = model.getLine(lineIndex);
    if (line == null)
	line = "";
    while(line.length() < pos)
	line += " ";
    model.beginEditTrans();
	model.setLine(lineIndex, line.substring(0, pos) + (str != null?str:"") + line.substring(pos));
    if (model.getHotPointY() == lineIndex && model.getHotPointX() >= pos)
	model.setHotPointX(model.getHotPointX() + (str != null?str.length():0));
    model.endEditTrans();
       }

    @Override public void mergeLines(int firstLineIndex)
    {
	if (firstLineIndex < 0 || firstLineIndex + 1 >= model.getLineCount())
	    return;
	model.beginEditTrans();
	final int origLineLen = model.getLine(firstLineIndex).length();
	model.setLine(firstLineIndex, model.getLine(firstLineIndex) + model.getLine(firstLineIndex + 1));
	model.removeLine(firstLineIndex + 1);
	if (model.getHotPointY() == firstLineIndex + 1)
	{
	    model.setHotPointY(model.getHotPointY() - 1);
	    model.setHotPointX(model.getHotPointX() + origLineLen);
	} else
	if (model.getHotPointY() > firstLineIndex + 1)
	    model.setHotPointY(model.getHotPointY() - 1);
	model.endEditTrans();
    }

    @Override public String splitLines(int pos, int lineIndex)
    {
	if (lineIndex < 0 || lineIndex >= model.getLineCount())
	    return "";
	model.beginEditTrans();
	String line = model.getLine(lineIndex);
	if (line == null)
	    line = "";
	while (line.length() < pos)
	    line += ' ';
	model.setLine(lineIndex, line.substring(0, pos));
	model.insertLine(lineIndex + 1, line.substring(pos));
	if (model.getHotPointY() == lineIndex && model.getHotPointX() >= pos)
	{
	    model.setHotPointY(lineIndex + 1);
	    model.setHotPointX(model.getHotPointX() - pos);
	} else
	    if (model.getHotPointY() > lineIndex)
		model.setHotPointY(model.getHotPointY() + 1);
	model.endEditTrans();
	return model.getLine(lineIndex + 1);
    }
}
