/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class SingleLineEdit implements RegionProvider
{
    protected final ControlEnvironment environment;
    protected final RegionTranslator region = new RegionTranslator(this);
    protected final SingleLineEditModel model;

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
	NullCheck.notNull(event, "event");
	if (event.withControl() || event.withAlt())
	    return false;
	if (event.isSpecial())
	    switch (event.getSpecial())
	    {
	    case BACKSPACE:
		return onBackspace(event);
	    case DELETE:
		return onDelete(event);
	    case TAB:
		return onTab(event);
	    default:
		return false;
	    }
	return onCharacter(event);
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	return region.onEnvironmentEvent(event, model.getHotPointX(), 0);
    }

    public boolean onAreaQuery(AreaQuery query)
    {
	return region.onAreaQuery(query, model.getHotPointX(), 0);
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
	    model.setLine(line + event.getChar());
	    model.setHotPointX(pos + 1);
	    if (event.getChar() == ' ')
	    {
		final String lastWord = TextUtils.getLastWord(line, line.length());//Since we have attached exactly space, we can use old line value, nothing changes;
		if (lastWord != null && !lastWord.isEmpty())
		    environment.say(lastWord); else
		    environment.hint(Hints.SPACE);
	    } else
		environment.sayLetter(event.getChar());
	    return true;
	}
	final String newLine = new String(line.substring(0, pos) + event.getChar() + line.substring(pos));
	model.setLine(newLine);
	model.setHotPointX(pos + 1);
	if (event.getChar() == ' ')
	{
	    final String lastWord = TextUtils.getLastWord(newLine, pos + 1);
	    if (lastWord != null && !lastWord.isEmpty())
		environment.say(lastWord); else
		environment.hint(Hints.SPACE);
	} else
	    environment.sayLetter(event.getChar());
	return true;
    }

    @Override public RegionContent getWholeRegion()
    {
	final String line = model.getLine();
	if (line != null)
	    return new RegionContent(new String[]{line});
	return new RegionContent(new String[]{""});
    }

    @Override public RegionContent getRegion(int fromX, int fromY,
					int toX, int toY)
    {
	final String line = model.getLine();
	if (line == null || line.isEmpty())
	    return null;
	final int fromPos = fromX < line.length()?fromX:line.length();
	final int toPos = toX < line.length()?toX:line.length();
	if (fromPos >= toPos)
	    return null;
	final String res = line.substring(fromPos, toPos);
	return new RegionContent(new String[]{res});
    }

    @Override public boolean deleteWholeRegion()
    {
	model.setHotPointX(0);
	model.setLine("");
	environment.hint(Hints.EMPTY_LINE);
	return true;
    }

    @Override public boolean deleteRegion(int fromX, int fromY,
					  int toX, int toY)
    {
	final String line = model.getLine();
	if (line == null || line.isEmpty())
	    return false;
	final int fromPos = fromX < line.length()?fromX:line.length();
	final int toPos = toX < line.length()?toX:line.length();
	if (fromPos >= toPos)
	    return false;
	model.setLine(line.substring(0, fromPos) + line.substring(toPos));
	model.setHotPointX(fromPos);
	return true;
    }

    @Override public boolean insertRegion(int x, int y,
					  RegionContent data)
    {
	if (data.isEmpty())
	    return false;
	final StringBuilder b = new StringBuilder();
	for(int i = 0;i < data.strings().length;++i)
	{
	    if (b.length() > 0)
		b.append(" ");
	    b.append(data.strings()[i]);
	}
	final String text = new String(b);
	if (text.isEmpty())
	    return true;
	final String line = model.getLine();
	final int pos = model.getHotPointX() < line.length()?model.getHotPointX():line.length();
	model.setLine(line.substring(0, pos) + text + line.substring(pos));
	model.setHotPointX(pos + text.length());
	return true;
    }
}
