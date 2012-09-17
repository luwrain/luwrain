/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.core;

import com.marigostra.luwrain.core.events.*;

public class EditArea extends SimpleArea
{
    public EditArea()
    {
    }

    public EditArea(String name)
    {
	super(name);
    }

    public EditArea(String name, String[] content)
    {
	super(name, content);
    }

    public void onUserKeyboardEvent(KeyboardEvent event)
    {
	if (event.withControl() || event.withAlt())
	    return;
	final int index = getHotPointY();
	if (index >= getLineCount())
	    return;
	String line = getLine(index);
	if (line == null)
	    return;
	final int pos = getHotPointX();
	if (pos > line.length())
	    return;
	if (event.isCommand())
	{
	    if (event.getCommand() == KeyboardEvent.BACKSPACE)
	    {
		if (pos < 1 && index < 1)
		{
		    Speech.say(Langs.staticValue(Langs.AREA_BEGIN), Speech.PITCH_HIGH);
		    return;
		}
		if (pos < 1)
		{
		    final int prevPos = getLine(index - 1).length();
		    setLine(index - 1, getLine(index - 1) + line);
		    removeLine(index);
		    setHotPoint(prevPos, index - 1);
		    Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
		    return;
		}
		String newLine = new String(line.substring(0, pos - 1) + line.substring(pos));
		setLine(index, newLine);
		setHotPoint(pos - 1, index);
		Speech.sayLetter(line.charAt(pos - 1));
		return;
	    }
	    if (event.getCommand() == KeyboardEvent.DELETE)
	    {
		if (index + 1>= getLineCount() && pos >= line.length())
		{
		    Speech.say(Langs.staticValue(Langs.AREA_END), Speech.PITCH_HIGH);
		    return;
		}
		if (pos == line.length())
		{
		    setLine(index, line + getLine(index + 1));
		    removeLine(index + 1);
		    Speech.say(Langs.staticValue(Langs.END_OF_LINE), Speech.PITCH_HIGH);
		    return;
		}
		if (pos == line.length() - 1)
		{
		    setLine(index, line.substring(0, pos));
		    Speech.sayLetter(line.charAt(pos));
		    return;
		}
		String newLine = new String(line.substring(0, pos) + line.substring(pos + 1));
		setLine(index, newLine);
		Speech.sayLetter(line.charAt(pos));
		return;
	    }
	    if (event.getCommand() == KeyboardEvent.TAB)
	    {
		String tabSeq = getTabSeq();
		if (tabSeq == null)
		    return;
		if (pos == line.length())
		{
	    setLine(index, line + tabSeq);
	    Speech.say(Langs.staticValue(Langs.TAB));
	    setHotPoint(pos + tabSeq.length(), index);
	    return;
	}
	String newLine = new String(line.substring(0, pos) + tabSeq + line.substring(pos));
	setLine(index, newLine);
	setHotPoint(pos + tabSeq.length(), index);
	Speech.say(Langs.staticValue(Langs.TAB));
	    return;
	    }
	    if (event.getCommand() == KeyboardEvent.ENTER)
	    {
		if (pos == line.length())
		{
		    insertLine(index + 1, "");
		    setHotPoint(0, index + 1);
		    Speech.say(Langs.staticValue(Langs.EMPTY_LINE), Speech.PITCH_HIGH);
		    return;
		}
		String newLine = line.substring(pos);
		line = line.substring(0, pos);
		setLine(index, line);
		insertLine(index + 1, newLine);
		setHotPoint(0, index + 1);
		Speech.say(newLine);
	    }
	    return;
	}
	if (pos == line.length())
	{
	    setLine(index, line + event.getCharacter());
	    setHotPoint(pos + 1, index);
	    if (event.getCharacter() == ' ')
	    {
		String lastWord = TextUtils.getLastWord(line, line.length());//Since we have attached exactly space, we can use old line value, nothing changes;
		if (lastWord != null && !lastWord.isEmpty())
		    Speech.say(lastWord); else
		    Speech.sayLetter(' ');
		    } else
		Speech.sayLetter(event.getCharacter());
	    return;
	}
	String newLine = new String(line.substring(0, pos) + event.getCharacter() + line.substring(pos));
	setLine(index, newLine);
	setHotPoint(pos + 1, index);
	if (event.getCharacter() == ' ')
	{
	    String lastWord = TextUtils.getLastWord(newLine, pos + 1);
	    if (lastWord != null && !lastWord.isEmpty())
		Speech.say(lastWord); else
		Speech.sayLetter(' ');
	} else
	    Speech.sayLetter(event.getCharacter());
    }

    public String getTabSeq()
    {
	return "\t";
    }
}
