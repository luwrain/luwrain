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

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class SingleLineEdit implements CopyCutRequest
{
    //    private final String tabMessage = Langs.staticValue(Langs.TAB);
    //    private final String textBeginMessage = Langs.staticValue(Langs.AREA_BEGIN);
    //    private final String textEndMessage = Langs.staticValue(Langs.AREA_END);

    private ControlEnvironment environment;
    private SingleLineEditModel model;
    private CopyCutInfo copyCutInfo = new CopyCutInfo(this);

    public SingleLineEdit(ControlEnvironment environment, SingleLineEditModel model)
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
	if (event.withControl() || event.withAlt())
	    return false;
	if (event.isCommand())
	    switch (event.getCommand())
	    {
	    case KeyboardEvent.BACKSPACE:
		return onBackspace(event);
	    case KeyboardEvent.DELETE:
		return onDelete(event);
	    case KeyboardEvent.ALTERNATIVE_DELETE:
		return onAltDelete(event);
	    case KeyboardEvent.TAB:
		return onTab(event);
	    default:
		return false;
	    }
	return onCharacter(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	switch(event.getCode())
	{
	case EnvironmentEvent.COPY_CUT_POINT:
	    return copyCutInfo.copyCutPoint(model.getHotPointX(), 0);
	case EnvironmentEvent.COPY:
	    return copyCutInfo.copy(model.getHotPointX(), 0);
	case EnvironmentEvent.CUT:
	    return copyCutInfo.cut(model.getHotPointX(), 0);
	case EnvironmentEvent.INSERT:
	    if (event instanceof InsertEvent)
		return onInsert((InsertEvent)event);
	    return false;
	default:
	    return false;
	}
    }

    private boolean onBackspace(KeyboardEvent event)
    {
	final String line = model.getLine();
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos < 0 || pos > line.length())
	    return false;
	if (pos < 1)
	{
	    environment.hint(Hints.BEGIN_OF_TEXT);
	    return true;
	}
	final String newLine = new String(line.substring(0, pos - 1) + line.substring(pos));
	model.setLine(newLine);
	model.setHotPointX(pos - 1);
	environment.sayLetter(line.charAt(pos - 1));
	return true;
    }

    private boolean onDelete(KeyboardEvent event)
    {
	final String line = model.getLine();
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos < 0 || pos > line.length())
	    return false;
	if (pos >= line.length())
	{
	    environment.hint(Hints.END_OF_TEXT);
	    return true;
	}
	if (pos == line.length() - 1)
	{
	    model.setLine(line.substring(0, pos));
	    environment.sayLetter(line.charAt(pos));
	    return true;
	}
	final String newLine = new String(line.substring(0, pos) + line.substring(pos + 1));
	model.setLine(newLine);
	environment.sayLetter(line.charAt(pos));
	return true;
    }

    private boolean onAltDelete(KeyboardEvent event)
    {
	model.setHotPointX(0);
	model.setLine("");
	environment.hint(Hints.EMPTY_LINE);
	return true;
    }

    private boolean onTab(KeyboardEvent event)
    {
	final String line = model.getLine();
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos < 0 || pos > line.length())
	    return false;
	final String tabSeq = model.getTabSeq();
	if (tabSeq == null)
	    return false;
	if (pos < line.length())
	{
	    final String newLine = new String(line.substring(0, pos) + tabSeq + line.substring(pos));
	    model.setLine(newLine);
	} else
	    model.setLine(line + tabSeq);
	environment.hint(Hints.TAB);
	model.setHotPointX(pos + tabSeq.length());
	return true;
    }

    private boolean onCharacter(KeyboardEvent event)
    {
	String line = model.getLine();
	if (line == null)
	    return false;
	final int pos = model.getHotPointX();
	if (pos < 0 || pos > line.length())
	    return false;
	if (pos == line.length())
	{
	    model.setLine(line + event.getCharacter());
	    model.setHotPointX(pos + 1);
	    if (event.getCharacter() == ' ')
	    {
		final String lastWord = TextUtils.getLastWord(line, line.length());//Since we have attached exactly space, we can use old line value, nothing changes;
		if (lastWord != null && !lastWord.isEmpty())
		    environment.say(lastWord); else
		    environment.hint(Hints.SPACE);
	    } else
		environment.sayLetter(event.getCharacter());
	    return true;
	}
	final String newLine = new String(line.substring(0, pos) + event.getCharacter() + line.substring(pos));
	model.setLine(newLine);
	model.setHotPointX(pos + 1);
	if (event.getCharacter() == ' ')
	{
	    final String lastWord = TextUtils.getLastWord(newLine, pos + 1);
	    if (lastWord != null && !lastWord.isEmpty())
		environment.say(lastWord); else
		environment.hint(Hints.SPACE);
	} else
	    environment.sayLetter(event.getCharacter());
	return true;
    }

    @Override public boolean onCopyAll()
    {
	final String line = model.getLine();
	if (line != null)
	{
	    environment.say(line);
	    environment.setClipboard(new String[]{line});
	} else
	    environment.setClipboard(new String[]{""});
	return true;
    }

    @Override public boolean onCopy(int fromX, int fromY, int toX, int toY)
    {
	final String line = model.getLine();
	if (line == null || line.isEmpty())
	    return false;
	final int fromPos = fromX < line.length()?fromX:line.length();
	final int toPos = toX < line.length()?toX:line.length();
	if (fromPos >= toPos)
	    throw new IllegalArgumentException("fromPos should be less than toPos");
	final String res = line.substring(fromPos, toPos);
	environment.say(res);
	environment.setClipboard(new String[]{res});
	return true;
    }

    @Override public boolean onCut(int fromX, int fromY, int toX, int toY)
    {
	final String line = model.getLine();
	if (line == null || line.isEmpty())
	    return false;
	final int fromPos = fromX < line.length()?fromX:line.length();
	final int toPos = toX < line.length()?toX:line.length();
	if (fromPos >= toPos)
	    throw new IllegalArgumentException("fromPos should be less than toPos");
	final String[] res = new String[1];
	res[0] = line.substring(fromPos, toPos);
	environment.say(res[0]);
	environment.setClipboard(res);
	model.setLine(line.substring(0, fromPos) + line.substring(toPos));
	model.setHotPointX(fromPos);
	return true;
    }

    private boolean onInsert(InsertEvent event)
    {
	if (event.getData() == null || !(event.getData() instanceof String[]))
	    return false;
	final String[] lines = (String[])event.getData();
	if (lines.length < 1)
	    return false;
	String text = lines[0];
	for(int i = 1;i < lines.length;++i)
	    text += " " + lines[i];
	final String line = model.getLine();
	final int pos = model.getHotPointX() < line.length()?model.getHotPointX():line.length();
	model.setLine(line.substring(0, pos) + text + line.substring(pos));
	environment.say(text);
	model.setHotPointX(pos + text.length());
	return true;
    }
}
