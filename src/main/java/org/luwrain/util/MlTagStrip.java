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

package org.luwrain.util;

import java.util.*;

public class MlTagStrip
{
    static private final String[] nonClosingTags = new String[]{
	"!doctype",
	"input",
	"br",
	"hr",
	"link",
	"img",
	"meta"
    }; 

    static private class Strip implements MlReaderConfig, MlReaderListener
    {
	private final StringBuilder builder = new StringBuilder();
	private int currentLineLen = 0;
	private int maxLineLen = 60;

	Strip(int maxLineLen)
	{
	    this.maxLineLen = maxLineLen;
	}

	@Override public boolean mlTagMustBeClosed(String tag)
	{
	    final String adjusted = tag.toLowerCase().trim();
	    for(String s: nonClosingTags)
		if (s.equals(adjusted))
		    return false;
	    return true;
	}

	@Override public boolean mlAdmissibleTag(String tagName, LinkedList<String> tagsStack)
	{
	    //May not open a tag inside of a script;
	    if (!tagsStack.isEmpty() &&tagsStack.getLast().toLowerCase().trim().equals("script"))
		return false;
	    final String adjusted = tagName.toLowerCase().trim();
	    for(int i = 0;i < adjusted.length();++i)
	    {
		final char c = adjusted.charAt(i);
		if (!Character.isLetter(c) && !Character.isDigit(c) &&
		    c != '_' && c != '-')
		    return false;
	    }
	    return true;
	}

	@Override public void onMlTagOpen(String tagName, Map<String, String> attrs)
	{
	    final String adjusted = tagName.toLowerCase().trim();
	    switch(adjusted)
	    {
	    case "P":
		newPara();
		break;
	    case "br":
		newPara();
		break;
	    }
	}

	@Override public void onMlText(String text, LinkedList<String> tagsStack)
	{
	    final String adjusted = text.trim();
	    if (adjusted.isEmpty())
		return;
	    int pos = 0;
	    while (pos < adjusted.length())
	    {
		int i = pos + 1;
		while (i < adjusted.length() && !Character.isSpace(adjusted.charAt(i)))
		    ++i;
		final String s = adjusted.substring(pos, i).trim();
		if (!s.isEmpty())
		    newWord(s);
		pos = i;
	    }
	}

	@Override public void onMlTagClose(String tagName)
	{
	    final String adjusted = tagName.toLowerCase().trim();
	    if (adjusted.equals("p"))
		newPara();
	}

	@Override public boolean isMlAutoClosingNeededOnTagOpen(String newTagName, LinkedList<String> tagsStack)
	{
	    return false;
	}

    @Override public boolean mayMlAnticipatoryTagClose(String tagName,
						       LinkedList<String> anticipatoryTags, LinkedList<String> tagsStack)
	{
	    return false;
	}

	private void newWord(String word)
	{
	    if (currentLineLen + word.length() + 1 > maxLineLen)
	    {
		builder.append("\n");
		currentLineLen = 0;
	    }
	    if (currentLineLen > 0)
	    {
		builder.append(" ");
		++currentLineLen;
	    }
	    builder.append(word);
	    currentLineLen += word.length();
	}

	private void newPara()
	{
	    if (currentLineLen < 1)
		return;
	    builder.append("\n\n");
	    currentLineLen = 0;
	}

	@Override public String toString()
	{
	    return builder.toString();
	}
    }

    static public String run(String text)
    {
	if (text == null || text.trim().isEmpty())
	    return "";
	final Strip s = new Strip(60);
	new MlReader(s, s, text).read();
	return s.toString();
    }
}
