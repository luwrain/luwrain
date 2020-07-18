/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls.block;

import java.util.*;

import org.luwrain.core.*;

final class BlockRowsBuilder
{
    //static private final String LOG_COMPONENT = BlockArea.LOG_COMPONENT;

    static private final int SPECIAL_MIN_WIDTH = 10;
        static private final int SPECIAL_MAX_WIDTH = 15;

    private final int rowLen;

    /** Objects of the incomplete row*/
    final List<BlockRowFragment> res = new LinkedList();
    /** Number of characters on the current (incomplete) row*/
    private int offset = 0;
    /** Complete rows*/
    final List<BlockRow> rows = new LinkedList();

    BlockRowsBuilder(int rowLen)
    {
	//	if (rowLen < 5)
	//	    Log.warning(LOG_COMPONENT, "too short row:" + rowLen);
	this.rowLen = rowLen;
    }

    boolean process(BlockObject obj)
    {
	NullCheck.notNull(obj, "obj");
	/*
	if (contentItem.isText())
	{
	    onText(contentItem, rowLen);
	    return true;
	}
	if (contentItem.isBr())
	{
	    commitRow();
	    return true;
	}
	if (contentItem.isTextInput())
	{
	    onTextInput(contentItem, rowLen);
	    return true;
	}
		if (contentItem.isButton())
	{
	    onButton(contentItem, rowLen);
	    return true;
	}
				if (contentItem.isImage())
	{
	    onImage(contentItem);
	    return true;
	}
	*/
	return false;
    }

    void commitRow()
    {
	if (res.isEmpty())
	    return;
	rows.add(new BlockRow(res.toArray(new BlockRowFragment[res.size()])));
	res.clear();
	offset = 0;
    }

    private void onTextInput(BlockObject obj, int maxRowLen)
    {
	NullCheck.notNull(obj, "obj");
	/*
	if (!item.isTextInput())
	    throw new IllegalArgumentException("The item must be of text input type");
	if (maxRowLen < SPECIAL_MAX_WIDTH)
	    throw new IllegalArgumentException("maxRowLen (" + maxRowLen + ") may not be less than SPECIAL_MAX_WIDTH (" + SPECIAL_MAX_WIDTH);
	final int room = maxRowLen - offset;
	final int width;
	if (room > SPECIAL_MAX_WIDTH)
	    width = SPECIAL_MAX_WIDTH; else
	    if (room >= SPECIAL_MIN_WIDTH)
		width = SPECIAL_MIN_WIDTH; else
	    {
		commitRow();
		width = SPECIAL_MAX_WIDTH;
	    }
	res.add(new WebTextInput(item, width));
	*/
    }

    /*
        private void onButton(ContentItem item, int maxRowLen)
    {
	NullCheck.notNull(item, "item");
	if (!item.isButton())
	    throw new IllegalArgumentException("The item must be of button type");
	if (maxRowLen < SPECIAL_MAX_WIDTH)
	    throw new IllegalArgumentException("maxRowLen (" + maxRowLen + ") may not be less than SPECIAL_MAX_WIDTH (" + SPECIAL_MAX_WIDTH);
		final int width;
	final int room = maxRowLen - offset;
	final int needed = item.getButtonTitle().length() + 2;
	if (needed <= room)
	    width = needed; else
	if (needed >= SPECIAL_MIN_WIDTH)
	    width = room; else
	    {
		commitRow();
		width = Math.min(needed, maxRowLen);
	    }
	res.add(new WebButton(item, width));
    }

            private void onImage(ContentItem item)
    {
	NullCheck.notNull(item, "item");
	if (!item.isImage())
	    throw new IllegalArgumentException("The item must be of an image type");
	if (rowLen - offset < 8)
	    commitRow();
	final String comment = item.getImageComment();
	final int room = rowLen - offset;
	if (comment.length() + 2 <= room)
	    res.add(new WebImage(item, Math.min(comment.length() + 2, 10))); else
	    	    res.add(new WebImage(item, Math.min(room, 10)));
	}
    */


    //Removes spaces only on row breaks and only if after the break there are non-spacing chars
    void onText(BlockObject obj, int maxRowLen)
    {
	NullCheck.notNull(obj, "obj");
	/*
	if (!item.isText())
	    throw new IllegalArgumentException("Given content item must be of text type");
	*/
	final String text = obj.getText();
	final int boundTo = text.length();
	//Log.debug("proba", "" + offset + ":" + text);
	if (offset > maxRowLen)
	    throw new RuntimeException("offset (" + offset + ") may not be greater than maxRowLen (" + maxRowLen + ")");
	if (text.isEmpty())
	    return;
	int nextStepFrom = 0;
	while (nextStepFrom < boundTo)
	{
	    //Log.debug("proba", "step:from " + nextStepFrom + ":" + text.substring(nextStepFrom));
	    final int stepFrom = nextStepFrom;
	    final int roomOnLine = maxRowLen - offset;//Available space on the current line
	    	    final int needed = boundTo - stepFrom;
	    if (roomOnLine == 0)
	    {
		//Try again on the next line
		commitRow();
		continue;
	    }
	    //Both needed and roomOnLine are greater than zero
	    if (needed <= roomOnLine)
	    {
		//Everything fits on the current line
		//Log.debug("proba", "Adding " + text.substring(stepFrom, boundTo));
		//FIXME:res.add(new BlockRowFragment(obj, stepFrom, boundTo));
		offset += needed;
		return;
	    }
	    int stepTo = findWordsFittingOnLIne(text, stepFrom, boundTo, roomOnLine);
	    //Log.debug("proba", "stepTo=" + stepTo);
	    if (stepTo == stepFrom)//No word ends before the end of the row
	    {
		if (offset > 0)
		{
		    //Trying to do the same once again from the beginning of the next line in hope a whole line will be enough
		    commitRow();
		    continue;
		}
		//The only thing we can do is split the line in the middle of the word, no another way
		stepTo = stepFrom + roomOnLine;
	    } //no fitting words
	    	    if (stepTo <= stepFrom)
			throw new RuntimeException("stepTo (" + stepTo + ") == stepFrom (" + stepFrom + ")");
	    	    if (stepTo - stepFrom > roomOnLine)
			throw new RuntimeException("Exceeding room on line (" + roomOnLine + "), stepFrom=" + stepFrom + ", stepTo=" + stepTo);
		    //Log.debug("proba", "splitting " + text.substring(stepFrom, stepTo));

		    //FIXME:res.add(new WebText(item, stepFrom, stepTo));
	    offset += (stepTo - stepFrom);
	    commitRow();
	    nextStepFrom = findNextWord(stepTo, text);
	} //main loop;
    }

    private int findWordsFittingOnLIne(String text, int posFrom, int boundTo, int lenRestriction)
    {
	int pos = 0;
		    int nextWordEnd = posFrom;
	    while (nextWordEnd - posFrom <= lenRestriction)
	    {
		pos = nextWordEnd;//It is definitely before the row end
		while (nextWordEnd < boundTo && Character.isSpace(text.charAt(nextWordEnd)))//FIXME:nbsp
		    ++nextWordEnd;
		while (nextWordEnd < boundTo && !Character.isSpace(text.charAt(nextWordEnd)))//FIXME:nbsp
		    ++nextWordEnd;
		if (nextWordEnd == pos)
		    return pos;
			    }
	    return pos;
    }

    private int findNextWord(int pos, String text)
    {
	NullCheck.notNull(text, "text");
	int i = pos;
	while (i < text.length() && Character.isSpace(text.charAt(i)))
		++i;
	if (i >= text.length())
		return pos;
	    return i;
    }

    }
