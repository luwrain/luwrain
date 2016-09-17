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

package org.luwrain.core.queries;

import org.luwrain.core.*;

public class VoicedFragmentQuery extends AreaQuery
{
    private String text = "";
    private int nextPointX = 0;
    private int nextPointY = 0;

    public VoicedFragmentQuery()
    {
	super(VOICED_FRAGMENT);
    }

    public void answer(String text,
		       int nextPointX, int nextPointY)
    {
	NullCheck.notNull(text, "text");
	this.text = text;
	this.nextPointX = nextPointX;
	this.nextPointY = nextPointY;
	answerTaken();
    }

    @Override public String getAnswer()
    {
	return text;
    }

    public int nextPointX()
    {
	return nextPointX;
    }

    public int nextPointY()
    {
	return nextPointY;
    }
}
