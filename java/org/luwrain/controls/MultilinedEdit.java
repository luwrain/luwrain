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

//FIXME:ControlEnvironment interface support;

package org.luwrain.controls;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class MultilinedEdit implements CopyCutRequest
{
    private final String tabMessage = Langs.staticValue(Langs.TAB);
    private final String emptyLineMessage = Langs.staticValue(Langs.EMPTY_LINE);
    private final String textBeginMessage = Langs.staticValue(Langs.AREA_BEGIN);
    private final String textEndMessage = Langs.staticValue(Langs.AREA_END);
    private final String lineEndMessage = Langs.staticValue(Langs.END_OF_LINE);

    private MultilinedEditModel model;
    private CopyCutInfo copyCutInfo;

    public MultilinedEdit(MultilinedEditModel model)
    {
	this.model = model;
	this.copyCutInfo = new CopyCutInfo(this);
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	final int index = model.getHotPointY();
	if (index >= model.getLineCount())
	    return false;
	final String line = model.getLine(index);
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos > line.length())
	    return false;
	if (!event.isCommand())
	    return onChar(event.getCharacter(), line, index, pos);
	if (event.isModified())
	    return false;
	switch(event.getCommand())
	{
	case KeyboardEvent.BACKSPACE:
	    return onBackspace(line, index, pos);
	case KeyboardEvent.DELETE:
	    return onDelete(line, index, pos);
	case KeyboardEvent.TAB:
	    return onTab(line, index, pos);
	case KeyboardEvent.ENTER:
	    return onEnter(line, index, pos);
	default:
	    return false;
	}
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	switch(event.getCode())
	{
	case EnvironmentEvent.COPY_CUT_POINT:
	    return copyCutInfo.doCopyCutPoint(model.getHotPointX(), model.getHotPointY());
	case EnvironmentEvent.COPY:
	    return copyCutInfo.doCopy(model.getHotPointX(), model.getHotPointY());
	case EnvironmentEvent.CUT:
	    return copyCutInfo.doCut(model.getHotPointX(), model.getHotPointY());
	case EnvironmentEvent.INSERT:
	    if (event instanceof InsertEvent)
		return onInsert((InsertEvent)event);
	    return false;
	default:
	    return false;
	}
    }

    public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	if (toY >= model.getLineCount())
	    return false;
	if (fromY == toY)
	{
	    String line = model.getLine(fromY);
	    if (line == null || line.isEmpty())
		return false;
	    int fromPos = fromX < line.length()?fromX:line.length();
	    int toPos = toX < line.length()?toX:line.length();
	    if (fromPos >= toPos)
		return false;
	    String[] res = new String[1];
	    res[0] = line.substring(fromPos, toPos);
	    Luwrain.setClipboard(res);
	    return true;
	}
	Vector<String> res = new Vector<String>();
	String line = model.getLine(fromY);
	if (line == null)
	    return false;
	res.add(line.substring(fromX < line.length()?fromX:line.length()));
	for(int i = fromY + 1;i < toY;++i)
	{
	    line = model.getLine(i);
	    if (line == null)
		return false;
	    res.add(line);
	}
	line = model.getLine(toY);
	if (line == null)
	    return false;
	res.add(line.substring(0, toX <line.length()?toX:line.length()));
	Luwrain.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	//FIXME:
	return false;
    }

    private boolean onInsert(InsertEvent event)
    {
	if (event.getData() == null || !(event.getData() instanceof String[]))
	    return false;
	String[] text = (String[])event.getData();
	if (text.length < 1)
	    return false;
	if (model.getHotPointY() >= model.getLineCount())
	{
	    for(String s: text)
		model.addLine(s);
	    model.setHotPoint(text[text.length - 1].length(), model.getLineCount() - 1);
	    return true;
	}
	if (text.length == 1)
	{
	    int index = model.getHotPointY();
	    String line = model.getLine(index);
	    int pos = model.getHotPointX() < line.length()?model.getHotPointX():line.length();
	    line = line.substring(0, pos) + text[0] + line.substring(pos);
	    model.setLine(index, line);
	    model.setHotPoint(pos + text[0].length(), index);
	    return true;
	}
	//Multilined new text;
	int index = model.getHotPointY();
	String line = model.getLine(index);
	int pos = model.getHotPointX() < line.length()?model.getHotPointX():line.length();
	model.setLine(index, line.substring(0, pos) + text[0]);
	for(int i = 1;i < text.length - 1;++i)
	    model.insertLine(index + i, text[i]);
	model.insertLine(index + text.length - 1, text[text.length - 1] + line.substring(pos));
	model.setHotPoint(text[text.length - 1].length(), index + text.length - 1);
	return true;
    }



    private boolean onBackspace(String line, int index, int pos)
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

    private boolean onDelete(String line, int index, int pos)
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

    private boolean onTab(String line, int index, int pos)
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

    private boolean onEnter(String line, int index, int pos)
    {
	if (pos >= line.length())
	{
	    model.insertLine(index + 1, "");
	    model.setHotPoint(0, index + 1);
	    Speech.say(emptyLineMessage, Speech.PITCH_HIGH);
	    return true;
	}
				      model.setLine(index, line.substring(0, pos));
	String newLine = line.substring(pos);
	model.insertLine(index + 1, newLine);
	model.setHotPoint(0, index + 1);
	Speech.say(newLine);
	return true;
    }

    private boolean onChar(char c, String line, int index, int pos)
    {
	if (pos == line.length())
	{
	    model.setLine(index, line + c);
	    model.setHotPoint(pos + 1, index);
	    if (c == ' ')
	    {
		String lastWord = TextUtils.getLastWord(line, line.length());//Since we have attached exactly space, we can use old line value, nothing changes;
		if (lastWord != null && !lastWord.isEmpty())
		    Speech.say(lastWord); else
		    Speech.sayLetter(' ');
	    } else
		Speech.sayLetter(c);
	    return true;
	}
	String newLine = new String(line.substring(0, pos) + c + line.substring(pos));
	model.setLine(index, newLine);
	model.setHotPoint(pos + 1, index);
	if (c == ' ')
	{
	    String lastWord = TextUtils.getLastWord(newLine, pos + 1);
	    if (lastWord != null && !lastWord.isEmpty())
		Speech.say(lastWord); else
		Speech.sayLetter(' ');
	} else
	    Speech.sayLetter(c);
	return true;
    }
}
