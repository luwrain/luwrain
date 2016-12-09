
package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

public class MultilineEdit
{
    protected final ControlEnvironment environment;
    protected final RegionTranslator region;
    protected final MultilineEditModel model;

    public MultilineEdit(ControlEnvironment environment, MultilineEditModel model)
    {
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(model, "model");
	this.environment = environment;
	this.model = model;
	final MultilineEdit edit = this;
	region = new RegionTranslator(new LinesRegionProvider(model){
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
	if (!event.isSpecial())//&&
	    //	    (!event.isModified() || event.withShiftOnly()))
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
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return false;
	return region.onEnvironmentEvent(event, model.getHotPointX(), model.getHotPointY());
    }

    public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	return region.onAreaQuery(query, model.getHotPointX(), model.getHotPointY());
    }

    protected boolean onBackspace(KeyboardEvent event)
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

    protected boolean onDelete(KeyboardEvent event)
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

    protected boolean onTab(KeyboardEvent event)
    {
	final String tabSeq = model.getTabSeq();
	if (tabSeq == null)
	    return false;
	model.insertChars(model.getHotPointX(), model.getHotPointY(), tabSeq);
	    environment.hint(Hints.TAB);
	    return true;
    }

    protected boolean onEnter(KeyboardEvent event)
    {
	final String line = model.splitLines(model.getHotPointX(), model.getHotPointY());
	NullCheck.notNull(line, "line");
	if (line.isEmpty())
	    environment.hint(Hints.EMPTY_LINE); else
	    environment.say(line);
	return true;
    }

    protected boolean onChar(KeyboardEvent event)
    {
	final char c = event.getChar();
	final String line = model.getLine(model.getHotPointY());
	NullCheck.notNull(line, "line");
	model.insertChars(model.getHotPointX(), model.getHotPointY(), "" + c);
	if (Character.isSpace(c))
	{
	    final String newLine = model.getLine(model.getHotPointY());
	    final int pos = Math.min(model.getHotPointX(), newLine.length());
	    final String lastWord = TextUtils.getLastWord(newLine, pos);
	    NullCheck.notNull(lastWord, "lastWord");
		if (!lastWord.isEmpty())
		    environment.say(lastWord); else
		    environment.hint(Hints.SPACE);
	} else
		environment.sayLetter(c);
	    return true;
    }

    protected boolean deleteRegion(int fromX, int fromY,
				   int toX, int toY)
    {
	return model.deleteRegion(fromX, fromY, toX, toY);
}

    protected boolean insertRegion(int x, int y,
					 RegionContent data)
    {
	return model.insertRegion(x, y, data.strings());
    }
}
