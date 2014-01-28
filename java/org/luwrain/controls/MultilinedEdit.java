/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

import org.luwrain.core.*;
import org.luwrain.core.events.KeyboardEvent;

public class MultilinedEdit
{
    private final String tabMessage = Langs.staticValue(Langs.TAB);
    private final String emptyLineMessage = Langs.staticValue(Langs.EMPTY_LINE);
    private final String textBeginMessage = Langs.staticValue(Langs.AREA_BEGIN);
    private final String textEndMessage = Langs.staticValue(Langs.AREA_END);
    private final String lineEndMessage = Langs.staticValue(Langs.END_OF_LINE);

    private MultilinedEditModel model;

    public MultilinedEdit(MultilinedEditModel model)
    {
	this.model = model;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.withControl() || event.withAlt())
	    return false;
	final int index = model.getHotPointY();
	if (index >= model.getLineCount())
	    return false;
	String line = model.getLine(index);
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos > line.length())
	    return false;

	if (event.isCommand())
	{

	    //Backspace;
	    if (event.getCommand() == KeyboardEvent.BACKSPACE)
	    {
		if (pos < 1 && index < 1)
		{
		    Speech.say(textBeginMessage, Speech.PITCH_HIGH);
		    return true;
		}
		if (pos < 1)
		{
		    final int prevPos = model.getLine(index - 1).length();
		    model.setLine(index - 1, model.getLine(index - 1) + line);
		    model.removeLine(index);
		    model.setHotPoint(prevPos, index - 1);
		    Speech.say(lineEndMessage, Speech.PITCH_HIGH);
		    return true;
		}
		String newLine = new String(line.substring(0, pos - 1) + line.substring(pos));
		model.setLine(index, newLine);
		model.setHotPoint(pos - 1, index);
		Speech.sayLetter(line.charAt(pos - 1));
		return true;
	    }

	    //Delete;
	    if (event.getCommand() == KeyboardEvent.DELETE)
	    {
		if (index + 1>= model.getLineCount() && pos >= line.length())
		{
		    Speech.say(textEndMessage, Speech.PITCH_HIGH);
		    return true;
		}
		if (pos == line.length())
		{
		    model.setLine(index, line + model.getLine(index + 1));
		    model.removeLine(index + 1);
		    Speech.say(lineEndMessage, Speech.PITCH_HIGH);
		    return true;
		}
		if (pos == line.length() - 1)
		{
		    model.setLine(index, line.substring(0, pos));
		    Speech.sayLetter(line.charAt(pos));
		    return true;
		}
		String newLine = new String(line.substring(0, pos) + line.substring(pos + 1));
		model.setLine(index, newLine);
		Speech.sayLetter(line.charAt(pos));
		return true;
	    }

	    //Tab;
	    if (event.getCommand() == KeyboardEvent.TAB)
	    {
		String tabSeq = model.getTabSeq();
		if (tabSeq == null)
		    return false;
		if (pos == line.length())
		{
	    model.setLine(index, line + tabSeq);
	    Speech.say(tabMessage);
	    model.setHotPoint(pos + tabSeq.length(), index);
	    return true;
	}
	String newLine = new String(line.substring(0, pos) + tabSeq + line.substring(pos));
	model.setLine(index, newLine);
	model.setHotPoint(pos + tabSeq.length(), index);
	Speech.say(tabMessage);
	    return true;
	    }

	    //Enter;
	    if (event.getCommand() == KeyboardEvent.ENTER)
	    {
		if (pos == line.length())
		{
		    model.insertLine(index + 1, "");
		    model.setHotPoint(0, index + 1);
		    Speech.say(emptyLineMessage, Speech.PITCH_HIGH);
		    return true;
		}
		String newLine = line.substring(pos);
		line = line.substring(0, pos);
		model.setLine(index, line);
		model.insertLine(index + 1, newLine);
		model.setHotPoint(0, index + 1);
		Speech.say(newLine);
	    return true;
	    }
	    return false;
	}

	//Character;
	if (pos == line.length())
	{
	    model.setLine(index, line + event.getCharacter());
	    model.setHotPoint(pos + 1, index);
	    if (event.getCharacter() == ' ')
	    {
		String lastWord = TextUtils.getLastWord(line, line.length());//Since we have attached exactly space, we can use old line value, nothing changes;
		if (lastWord != null && !lastWord.isEmpty())
		    Speech.say(lastWord); else
		    Speech.sayLetter(' ');
		    } else
		Speech.sayLetter(event.getCharacter());
	    return true;
	}
	String newLine = new String(line.substring(0, pos) + event.getCharacter() + line.substring(pos));
	model.setLine(index, newLine);
	model.setHotPoint(pos + 1, index);
	if (event.getCharacter() == ' ')
	{
	    String lastWord = TextUtils.getLastWord(newLine, pos + 1);
	    if (lastWord != null && !lastWord.isEmpty())
		Speech.say(lastWord); else
		Speech.sayLetter(' ');
	} else
	    Speech.sayLetter(event.getCharacter());
	return true;
    }
}
