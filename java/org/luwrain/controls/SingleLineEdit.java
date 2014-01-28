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

public class SingleLineEdit
{
    private final String tabMessage = Langs.staticValue(Langs.TAB);
    private final String textBeginMessage = Langs.staticValue(Langs.AREA_BEGIN);
    private final String textEndMessage = Langs.staticValue(Langs.AREA_END);

    private SingleLineEditModel model;

    public SingleLineEdit(SingleLineEditModel model)
    {
	this.model = model;
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event.withControl() || event.withAlt())
	    return false;
	String line = model.getLine();
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
		if (pos < 1)
		{
		    Speech.say(textBeginMessage, Speech.PITCH_HIGH);
		    return true;
		}
		String newLine = new String(line.substring(0, pos - 1) + line.substring(pos));
		model.setLine(newLine);
		model.setHotPointX(pos - 1);
		Speech.sayLetter(line.charAt(pos - 1));
		return true;
	    }

	    //Delete;
	    if (event.getCommand() == KeyboardEvent.DELETE)
	    {
		if (pos >= line.length())
		{
		    Speech.say(textEndMessage, Speech.PITCH_HIGH);
		    return true;
		}
		if (pos == line.length() - 1)
		{
		    model.setLine(line.substring(0, pos));
		    Speech.sayLetter(line.charAt(pos));
		    return true;
		}
		String newLine = new String(line.substring(0, pos) + line.substring(pos + 1));
		model.setLine(newLine);
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
	    model.setLine(line + tabSeq);
	    Speech.say(tabMessage);
	    model.setHotPointX(pos + tabSeq.length());
	    return true;
	}
	String newLine = new String(line.substring(0, pos) + tabSeq + line.substring(pos));
	model.setLine(newLine);
	model.setHotPointX(pos + tabSeq.length());
	Speech.say(tabMessage);
	    return true;
	    }
	    return false;
	}

	//Character;
	if (pos == line.length())
	{
	    model.setLine(line + event.getCharacter());
	    model.setHotPointX(pos + 1);
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
	model.setLine(newLine);
	model.setHotPointX(pos + 1);
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
