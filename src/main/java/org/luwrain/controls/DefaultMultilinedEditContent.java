/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.controls;

public class DefaultMultilinedEditContent implements MultilinedEditContent
{
    private String[] lines = new String[0];

    public DefaultMultilinedEditContent()
    {
    }

    public DefaultMultilinedEditContent(String[] lines)
    {
	this.lines = lines != null?lines:new String[0];
    }

    @Override public int getLineCount()
    {
	return !noProperLines()?lines.length:0;
    }

    @Override public String getLine(int index)
    {
	if (noProperLines())
	    return "";
	return index < lines.length?lines[index]:"";
    }

    public void setLines(String[] lines)
    {
	this.lines = lines != null?lines:new String[0];
    }

    public String[] getLines()
    {
	return !noProperLines()?lines:new String[0];
    }

    @Override public void setLine(int index, String line)
    {
	if (index < 0 || line == null)
	    return;
	if (noProperLines())
	{
	    lines = new String[index + 1];
	    if (index > 0)
		for(int i = 0;i < index - 1;++i)
		    lines[i] = "";
	    lines[index] = line;
	    return;
	}
	if (index >= lines.length)
	    return;
	lines[index] = line;
    }

    @Override public void addLine(String line)
    {
	if (line == null)
	    return;
	if (noProperLines())
	{
	    lines = new String[1];
	    lines[0] = line;
	    return;
	}
	String[] newLines = new String[lines.length + 1];
	for(int i = 0;i < lines.length;i++)
	    newLines[i] = lines[i];
	newLines[newLines.length - 1] = line;
	lines = newLines;
    }

    //index is the position of newly inserted line
    @Override public void insertLine(int index, String line)
    {
	if (index < 0 || line == null)
	    return;
	if (noProperLines())
	{
	    lines = new String[index + 1];
	    if (index > 0)
		for(int i = 0;i < index;++i)
		    lines[i] = "";
	    lines[index] = line;
	    return;
	}
	if (index > lines.length)
	    return;
	String[] newLines = new String[lines.length + 1];
	for(int i = 0;i < index;i++)
	    newLines[i] = lines[i];
	newLines[index] = line;
	for(int i = index;i < lines.length;i++)
	    newLines[i + 1] = lines[i];
	lines = newLines;
    }

    @Override public void removeLine(int index)
    {
	if (index < 0 || noProperLines())
	    return;
	if (index >= lines.length)
	    return;
	String[] newLines = new String[lines.length - 1];
	for(int i = 0;i < index;i++)
	    newLines[i] = lines[i];
	for(int i = index + 1;i < lines.length;i++)
	    newLines[i - 1] = lines[i];
	lines = newLines;
    }

    @Override public boolean beginEditTrans()
    {
	return true;
    }

    @Override public void endEditTrans()
    {
    }

    public void clear()
    {
	lines = new String[0];
    }

    public String getWholeText()
    {
	if (noProperLines())
	    return "";
	final StringBuilder sb = new StringBuilder();
	for(String line: lines)
	{
	    sb.append(line);
	    sb.append("\n");
	}
	return sb.toString();
    }

    private boolean noProperLines()
    {
	return lines == null || lines.length < 1;
    }
}
