/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

//LWR_API 1.0

package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.util.*;

//Completely skips EnvironmentEvent.CLEAR
public class MultilineEdit
{
    //FIXME:getLineCount() never returns zero
    /**
     * The model for {@link MultilineEdit}. It is supposed that this
     * interface is a front-end for {@link MutableLines} in conjunction with
     * {@link HotPointControl}, but you may use it freely as it is
     * necessary for a particular purpose. See 
     * {@link MultilineEditModelTranslator} for a default implementation.
     * <p>
     * {@code MultilineEdit} guarantees that each user action results exactly in
     * a single call of some method of this interface.  This allows substitution
     * of any method, which makes changes in the model, by any number of
     * other methods in any order, and this will keep all structures
     * consistent.
     * <p>
     * If some operation is addressed at the position outside of the stored
     * text, the result may be undefined. The implementation of this
     * interface should not issue any speech output.
     *
     * @see MultilineEditModelTranslator
     */
    public interface Model extends Lines
    {
	int getHotPointX();
	int getHotPointY();
	String getTabSeq();
	//Processes only chars within line bounds,  neither end of line not end of text not processed
	char deleteChar(int pos, int lineIndex);
	//Expects ending point always after starting
	boolean deleteRegion(int fromX, int fromY, int toX, int toY);
	boolean insertRegion(int x, int y, String[] lines);
    //Adds empty line with pos=0 and line=0 if previously there were no lines at all
	boolean insertChars(int pos, int lineIndex, String str);
	boolean mergeLines(int firstLineIndex);

	/**
	 * Splits the specified line at the specified position. This method
	 * removes on the line all the content after the specified position and puts
	 * the deleted fragment on new line which is inserted just after
	 * modified. If the position is given outside of the stored text, the
	 * behaviour of this method is undefined.
	 *
	 * @param pos The 0-based position to split line at
	 * @param lineIndex The 0-based index of the line to split
	 * @return The fragment moved onto newly inserted line
	 */
	String splitLine(int pos, int lineIndex);
    }

    protected final ControlContext environment;
    protected final AbstractRegionPoint regionPoint;
    protected final ClipboardTranslator clipboardTranslator;
    protected final RegionTextQueryTranslator regionTextQueryTranslator;
    protected final Model model;

    public MultilineEdit(ControlContext environment, Model model, AbstractRegionPoint regionPoint)
    {
	NullCheck.notNull(environment, "environment");
	NullCheck.notNull(model, "model");
	NullCheck.notNull(regionPoint, "regionPoint");
	this.environment = environment;
	this.regionPoint = regionPoint;
	this.model = model;
	this.clipboardTranslator = new ClipboardTranslator(new LinesClipboardProvider(model, ()->environment.getClipboard()){
		@Override public boolean onClipboardCopy(int fromX, int fromY, int toX, int toY, boolean withDeleting)
		{
		    if (!super.onClipboardCopy(fromX, fromY, toX, toY, false))
			return false;
		    if (!withDeleting)
			return true;
		    return model.deleteRegion(fromX, fromY, toX, toY);
		}
		@Override public boolean onDeleteRegion(int fromX, int fromY, int toX, int toY)
		{
		    return model.deleteRegion(fromX, fromY, toX, toY);
		}
	    }, regionPoint, EnumSet.noneOf(ClipboardTranslator.Flags.class));
	this.regionTextQueryTranslator = new RegionTextQueryTranslator(new LinesRegionTextQueryProvider(model), regionPoint, EnumSet.noneOf(RegionTextQueryTranslator.Flags.class));
    }

    public boolean onInputEvent(KeyboardEvent event)
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

    public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.getType() != EnvironmentEvent.Type.REGULAR)
	    return false;
	switch(event.getCode())
	{
	case CLEAR:
	    return false;
	case CLIPBOARD_PASTE:
	    return onClipboardPaste();
	default:
	    if (clipboardTranslator.onSystemEvent(event, model.getHotPointX(), model.getHotPointY()))
		return true;
	return regionTextQueryTranslator.onSystemEvent(event, model.getHotPointX(), model.getHotPointY());
	}
    }

    public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	return regionTextQueryTranslator.onAreaQuery(query, model.getHotPointX(), model.getHotPointY());
    }

    protected boolean onBackspace(KeyboardEvent event)
    {
	if (model.getHotPointY() >= model.getLineCount())
	    return false;
	if (model.getHotPointX() <= 0 && model.getHotPointY() <= 0)
	{
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.BEGIN_OF_TEXT));
	    return true;
	}
	if (model.getHotPointX() <= 0)
	{
	    if (!model.mergeLines(model.getHotPointY() - 1))
		return false;
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE));
	} else
	{
	    final char res = model.deleteChar(model.getHotPointX() - 1, model.getHotPointY());
	    if (res == '\0')
		return false;
	    environment.sayLetter(res);
	}
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
	    final char res = model.deleteChar(model.getHotPointX(), model.getHotPointY());
	    if (res == '\0')
		return false;
	    environment.sayLetter(res);
	    return true;
	}
	if (model.getHotPointY() + 1 >= model.getLineCount())
	{
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_TEXT));
	    return true;
	}
	if (!model.mergeLines(model.getHotPointY()))
	    return false;
	environment.setEventResponse(DefaultEventResponse.hint(Hint.END_OF_LINE)); 
	return true;
    }

    protected boolean onTab(KeyboardEvent event)
    {
	final String tabSeq = model.getTabSeq();
	if (tabSeq == null)
	    return false;
	if (!model.insertChars(model.getHotPointX(), model.getHotPointY(), tabSeq))
	    return false;
	environment.setEventResponse(DefaultEventResponse.hint(Hint.TAB));
	    return true;
    }

    protected boolean onEnter(KeyboardEvent event)
    {
	final String line = model.splitLine(model.getHotPointX(), model.getHotPointY());
	if (line == null)
	    return false;
	if (line.isEmpty())
	    environment.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE)); else
	    environment.say(line);
	return true;
    }

    protected boolean onChar(KeyboardEvent event)
    {
	final char c = event.getChar();
	final String line = model.getLine(model.getHotPointY());
	NullCheck.notNull(line, "line");
	if (!model.insertChars(model.getHotPointX(), model.getHotPointY(), "" + c))
	    return false;
	if (Character.isSpace(c))
	{
	    final String newLine = model.getLine(model.getHotPointY());
	    final int pos = Math.min(model.getHotPointX(), newLine.length());
	    final String lastWord = TextUtils.getLastWord(newLine, pos);
	    NullCheck.notNull(lastWord, "lastWord");
		if (!lastWord.isEmpty())
		    environment.say(lastWord); else
		    environment.setEventResponse(DefaultEventResponse.hint(Hint.SPACE));
	} else
		environment.sayLetter(c);
	    return true;
    }

    protected boolean onClipboardPaste()
    {
	if (environment.getClipboard().isEmpty())
	    return false;
	return model.insertRegion(model.getHotPointX(), model.getHotPointY(), environment.getClipboard().getStrings());
    }
}
