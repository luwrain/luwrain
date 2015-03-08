/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class MultilinedEdit implements CopyCutRequest
{
    //    private final String tabMessage = Langs.staticValue(Langs.TAB);
    //    private final String emptyLineMessage = Langs.staticValue(Langs.EMPTY_LINE);
    //    private final String textBeginMessage = Langs.staticValue(Langs.AREA_BEGIN);
    //    private final String textEndMessage = Langs.staticValue(Langs.AREA_END);
    //    private final String lineEndMessage = Langs.staticValue(Langs.END_OF_LINE);

    private ControlEnvironment environment;
    private MultilinedEditModel model;
    private CopyCutInfo copyCutInfo = new CopyCutInfo(this);

    public MultilinedEdit(ControlEnvironment environment, MultilinedEditModel model)
    {
	this.environment = environment;
	this.model = model;
	if (environment == null)
	    throw new NullPointerException("environment may not be null");
	if (model == null)
	    throw new NullPointerException("model may not be null");
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	boolean res = false;
	if (!event.isCommand())
	{
	    if (!model.beginEditTrans())
		return false;
	    res = onChar(event);
	    model.endEditTrans();
	    return res;
	}
	if (event.isModified())
	    return false;
	switch(event.getCommand())
	{
	case KeyboardEvent.BACKSPACE:
	    if (!model.beginEditTrans())
		return false;
	    res = onBackspace(event);
	    model.endEditTrans();
	    return res;
	case KeyboardEvent.DELETE:
	    if (!model.beginEditTrans())
		return false;
	    res = onDelete(event);
	    model.endEditTrans();
	    return res;
	case KeyboardEvent.TAB:
	    if (!model.beginEditTrans())
		return false;
	    res = onTab(event);
	    model.endEditTrans();
	    return res;
	case KeyboardEvent.ENTER:
	    if (!model.beginEditTrans())
		return false;
	    res = onEnter(event);
	    model.endEditTrans();
	    return res;
	default:
	    return false;
	}
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	boolean res = false;
	switch(event.getCode())
	{
	case EnvironmentEvent.COPY_CUT_POINT:
	    return copyCutInfo.copyCutPoint(model.getHotPointX(), model.getHotPointY());
	case EnvironmentEvent.COPY:
	    return copyCutInfo.copy(model.getHotPointX(), model.getHotPointY());
	case EnvironmentEvent.CUT:
	    if (!model.beginEditTrans())
		return false;
	    res = copyCutInfo.cut(model.getHotPointX(), model.getHotPointY());
	    model.endEditTrans();
	    return res;
	case EnvironmentEvent.INSERT:
	    if (!(event instanceof InsertEvent) || !model.beginEditTrans())
		return false;
res = onInsert((InsertEvent)event);
model.endEditTrans();
return res;
	default:
	    return false;
	}
    }

    private boolean onBackspace(KeyboardEvent event)
    {
	//Preparing;
	final int index = model.getHotPointY();
	final int count = model.getLineCount();
	if (count < 1)
	{
	    environment.hint(Hints.BEGIN_OF_TEXT);
	    return true;
	}
	if (index >= count)
	    return false;
	final String line = model.getLine(index);
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos > line.length())
	    return false;
	//Nothing to eliminate with backspace;
	if (pos < 1 && index < 1)
	    environment.hint(Hints.BEGIN_OF_TEXT); else
	    //Jumping to previous line;
	    if (pos < 1)
	    {
		final int prevLineIndex = index - 1;
		final String prevLine = model.getLine(prevLineIndex);
		if (prevLine == null)
		    return false;
		final int prevLinePos = prevLine.length();
		model.setLine(prevLineIndex, prevLine + line);
		model.removeLine(index);
		model.setHotPoint(prevLinePos, prevLineIndex);
		environment.hint(Hints.END_OF_LINE);
	    } else
		//Eliminating just the previous char;
	    {
		final String newLine = line.substring(0, pos - 1) + line.substring(pos);
		model.setLine(index, newLine);
		model.setHotPoint(pos - 1, index);
		environment.sayLetter(line.charAt(pos - 1));
	    }
	//Removing first empty line  if it is the only line in model;
	if (model.getLineCount() == 1 && model.getLine(0).isEmpty())
	    model.removeLine(0);
	return true;
    }

    private boolean onDelete(KeyboardEvent event)
    {
	final int index = model.getHotPointY();
	final int count = model.getLineCount();
	if (count < 1)
	{
	    environment.hint(Hints.END_OF_TEXT);
	    return true;
	}
	if (index >= count)
	    return false;
	final String line = model.getLine(index);
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos > line.length())
	    return false;
	//Nothing to eliminate with delete;
	if (index + 1>= count && pos >= line.length())
	    environment.hint(Hints.END_OF_TEXT); else
	    //Eliminating new line position;
	    if (pos == line.length())
	    {
		final int nextLineIndex = index + 1;
		final String nextLine = model.getLine(nextLineIndex);
		if (nextLine == null)
		    return false;
		model.setLine(index, line + nextLine);
		model.removeLine(nextLineIndex);
		environment.hint(Hints.END_OF_LINE);
	    } else
		//eliminating last character of line;
		if (pos + 1 == line.length())
		{
		    model.setLine(index, line.substring(0, pos));
		    environment.sayLetter(line.charAt(pos));
		} else
		    //Eliminating just last character of line;
		{
		    String newLine = line.substring(0, pos) + line.substring(pos + 1);
		    model.setLine(index, newLine);
		    environment.sayLetter(line.charAt(pos));
		}
	//Removing first empty line  if it is the only line in model;
	if (model.getLineCount() == 1 && model.getLine(0).isEmpty())
	    model.removeLine(0);
	return true;
    }

    private boolean onTab(KeyboardEvent event)
    {
	final int index = model.getHotPointY();
	int count = model.getLineCount();//Should not be final;
	if (index > count)
	    return false;
	if (index == count)
	{
	    model.addLine("");
	    ++count;
	}
	final String line = model.getLine(index);
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos > line.length())
	    return false;
	final String tabSeq = model.getTabSeq();
	if (tabSeq == null)
	    return false;
	if (pos == line.length())
	{
	    model.setLine(index, line + tabSeq);
	    environment.hint(Hints.TAB);
	    model.setHotPoint(pos + tabSeq.length(), index);
	    return true;
	}
	final String newLine = line.substring(0, pos) + tabSeq + line.substring(pos);
	model.setLine(index, newLine);
	model.setHotPoint(pos + tabSeq.length(), index);
	environment.hint(Hints.TAB);
	return true;
    }

    private boolean onEnter(KeyboardEvent event)
    {
	final int index = model.getHotPointY();
	int count = model.getLineCount();//Should not be final;
	if (index > count)
	    return false;
	if (index == count)
	{
	    model.addLine("");
	    ++count;
	}
	final String line = model.getLine(index);
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos > line.length())
	    return false;
	if (pos >= line.length())
	{
	    model.insertLine(index + 1, "");
	    model.setHotPoint(0, index + 1);
	    environment.hint(Hints.EMPTY_LINE);
	    return true;
	}
	model.setLine(index, line.substring(0, pos));
	final String newLine = line.substring(pos);
	model.insertLine(index + 1, newLine);
	model.setHotPoint(0, index + 1);
	environment.say(newLine);
	return true;
    }

    private boolean onChar(KeyboardEvent event)
    {
	final int index = model.getHotPointY();
	int count = model.getLineCount();//Should not be final;
	if (index > count)
	    return false;
	if (index == count)
	{
	    model.addLine("");
	    ++count;
	}
	final String line = model.getLine(index);
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos > line.length())
	    return false;
	final char c = event.getCharacter();
	if (pos == line.length())
	{
	    model.setLine(index, line + c);
	    model.setHotPoint(pos + 1, index);
	    if (Character.isSpace(c))
	    {
		String lastWord = TextUtils.getLastWord(line, line.length());//Since we have attached exactly space, we can use old line value, nothing changes;
		if (lastWord != null && !lastWord.isEmpty())
		    environment.say(lastWord); else
		    environment.hint(Hints.SPACE);
	    } else
		environment.sayLetter(c);
	    return true;
	}
	final String newLine = line.substring(0, pos) + c + line.substring(pos);
	model.setLine(index, newLine);
	model.setHotPoint(pos + 1, index);
	if (Character.isSpace(c))
	{
	    String lastWord = TextUtils.getLastWord(newLine, pos + 1);
	    if (lastWord != null && !lastWord.isEmpty())
		environment.say(lastWord); else
		environment.hint(Hints.SPACE);
	} else
	    environment.sayLetter(c);
	return true;
    }

    @Override public boolean onCopyAll()
    {
	if (model.getLineCount() < 1)
	    return false;
	Vector<String> res = new Vector<String>();
	final int count = model.getLineCount();
	for(int i = 0;i < count;++i)
	{
	    final String line = model.getLine(i);
	    if (line == null)
		return false;
	    res.add(line);
	}
	if (res.size() == 1)
	    environment.say(res.get(0)); else
	    environment.say(environment.staticStr(Langs.COPIED_LINES) + res.size());
	environment.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	if (model.getLineCount() == 0)
	    return false;
	if (toY >= model.getLineCount())
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
	    String res = line.substring(fromPos, toPos);
	    environment.say(res);
	    environment.setClipboard(new String[]{res});
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
	environment.hint(environment.staticStr(Langs.COPIED_LINES) + res.size());
	environment.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    @Override public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	if (model.getLineCount() < 1)
	    return false;
	if (toY >= model.getLineCount())
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
	    String[] res = new String[]{line.substring(fromPos, toPos)};
	    model.setLine(fromY, line.substring(0, fromPos) + line.substring(toPos));
	    environment.say(res[0]);
	    environment.setClipboard(res);
	    return true;
	}
	Vector<String> res = new Vector<String>();
	final String firstLine = model.getLine(fromY);
	if (firstLine == null)
	    return false;
	final int fromPos = fromX < firstLine.length()?fromX:firstLine.length();
	res.add(firstLine.substring(fromPos));
	for(int i = fromY + 1;i < toY;++i)
	{
	    final String line = model.getLine(i);
	    if (line == null)
		return false;
	    res.add(line);
	}
	final String endingLine = model.getLine(toY);
	if (endingLine == null)
	    return false;
	final int toPos = toX <endingLine.length()?toX:endingLine.length();
	res.add(endingLine.substring(0, toPos));
	model.setLine(fromY, firstLine.substring(0, fromPos) + endingLine.substring(toPos));
	for(int i = fromY + 1;i <= toY;++i)
	    model.removeLine(fromY + 1);
	environment.hint(environment.staticStr(Langs.CUT_LINES) + res.size());
	environment.setClipboard(res.toArray(new String[res.size()]));
	return true;
    }

    private boolean onInsert(InsertEvent event)
    {
	if (event.getData() == null || !(event.getData() instanceof String[]))
	    return false;
	final String[] text = (String[])event.getData();
	if (text.length < 1)
	    return false;
	if (model.getHotPointY() >= model.getLineCount())
	{
	    for(String s: text)
		model.addLine(s != null?s:"");
	    model.setHotPoint(text[text.length - 1].length(), model.getLineCount() - 1);
	    environment.say(text[0]);
	    return true;
	}
	if (text.length == 1)
	{
	    int index = model.getHotPointY();
	    String line = model.getLine(index);
	    final int pos = model.getHotPointX() < line.length()?model.getHotPointX():line.length();
	    line = line.substring(0, pos) + text[0] + line.substring(pos);
	    model.setLine(index, line);
	    model.setHotPoint(pos + text[0].length(), index);
	    environment.say(text[0]);
	    return true;
	}
	//Multilined new text;
	int index = model.getHotPointY();
	String line = model.getLine(index);
	final int pos = model.getHotPointX() < line.length()?model.getHotPointX():line.length();
	model.setLine(index, line.substring(0, pos) + text[0]);
	for(int i = 1;i < text.length - 1;++i)
	    model.insertLine(index + i, text[i]);
	model.insertLine(index + text.length - 1, text[text.length - 1] + line.substring(pos));
	model.setHotPoint(text[text.length - 1].length(), index + text.length - 1);
	environment.say(text[0]);
	return true;
    }
}
