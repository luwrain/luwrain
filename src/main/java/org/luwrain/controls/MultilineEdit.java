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

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class MultilineEdit
{
    private ControlEnvironment environment;
    private Region region;
    private MultilineEditModel model;

    public MultilineEdit(ControlEnvironment environment, MultilineEditModel model)
    {
	this.environment = environment;
	this.model = model;
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(model, "model");
	final MultilineEdit edit = this;
	region = new Region(new LinesRegionProvider(model){
		@Override public boolean insertRegion(int x, int y,
						      RegionContent data)
		{
		    return edit.insertRegion(x, y, data);
		}
		@Override public boolean deleteWholeRegion()
		{
		    return false;
		}
		@Override public boolean deleteRegion(int fromX, int fromY,
						      int toX, int toY)
		{
		    return edit.deleteRegion(fromX, fromY, toX, toY);
		}
	    });
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!event.isSpecial())
	    return onChar(event);
	if (event.isModified())
	    return false;
	switch(event.getSpecial())
	{
	case BACKSPACE:
return onBackspace(event);
	case DELETE:
return onDelete(event);
	case TAB:
return onTab(event);
	case ENTER:
return onEnter(event);
	default:
	    return false;
	}
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event == null)
	    throw new NullPointerException("event may not be null");
	return region.onEnvironmentEvent(event, model.getHotPointX(), model.getHotPointY());
    }

    public boolean onAreaQuery(AreaQuery query)
    {
	return region.onAreaQuery(query, model.getHotPointX(), model.getHotPointY());
    }

    private boolean onBackspace(KeyboardEvent event)
    {
	if (model.getHotPointY() >= model.getLineCount())
	    return false;
	if (model.getHotPointX() <= 0 && model.getHotPointY() <= 0)
	{
	    environment.hint(Hints.BEGIN_OF_TEXT);
	    return true;
	}
	if (model.getHotPointX() <= 0)
	{
	    model.mergeLines(model.getHotPointY() - 1);
	    environment.hint(Hints.END_OF_LINE);
	} else
	    environment.sayLetter(model.deleteChar(model.getHotPointX() - 1, model.getHotPointY()));
	return true;
    }

    private boolean onDelete(KeyboardEvent event)
    {
	if (model.getHotPointY() >= model.getLineCount())
	    return false;
	final String line = model.getLine(model.getHotPointY());
	if (line == null)
	    return false;
	if (model.getHotPointX() < line.length())
	{
	    environment.sayLetter(model.deleteChar(model.getHotPointX(), model.getHotPointY()));
	    return true;
	}
	if (model.getHotPointY() + 1 >= model.getLineCount())
	{
	    environment.hint(Hints.END_OF_TEXT);
	    return true;
	}
	model.mergeLines(model.getHotPointY());
	environment.hint(Hints.END_OF_LINE); 
	return true;
    }

    private boolean onTab(KeyboardEvent event)
    {
	final String tabSeq = model.getTabSeq();
	if (tabSeq == null)
	    return false;
	model.insertChars(model.getHotPointX(), model.getHotPointY(), tabSeq);
	    environment.hint(Hints.TAB);
	    return true;
    }

    private boolean onEnter(KeyboardEvent event)
    {
	final String line = model.splitLines(model.getHotPointX(), model.getHotPointY());
	if (line == null || line.isEmpty())
	    environment.hint(Hints.EMPTY_LINE); else
	    environment.say(line);
	return true;
    }

    private boolean onChar(KeyboardEvent event)
    {
	final char c = event.getChar();
	String line = model.getLine(model.getHotPointY());
	if (line == null)
	    line = "";
	model.insertChars(model.getHotPointX(), model.getHotPointY(), "" + c);
	if (Character.isSpace(c))
	{
	    int pos = model.getHotPointX();
	    if (pos > line.length())
		pos = line.length();
	    final String lastWord = TextUtils.getLastWord(line, pos);
		if (lastWord != null && !lastWord.isEmpty())
		    environment.say(lastWord); else
		    environment.hint(Hints.SPACE);
	} else
		environment.sayLetter(c);
	    return true;
    }

    private boolean deleteRegion(int fromX, int fromY,
					  int toX, int toY)
    {
	return model.deleteRegion(fromX, fromY, toX, toY);
}

    private boolean insertRegion(int x, int y,
					 RegionContent data)
    {
	return model.insertRegion(x, y, org.luwrain.util.Strings.notNullArray(data.strings));
    }
}
