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

package org.luwrain.core.queries;

import org.luwrain.core.*;

public class RegionTextQuery extends AreaQuery
{
    private String regionText = null;

    public RegionTextQuery()
    {
	super(REGION_TEXT);
    }

    public void answer(String text)
    {
	NullCheck.notNull(text, "text");
	secondAnswerCheck();
	this.regionText = text;
answerTaken();
    }

    @Override public String getAnswer()
    {
	return regionText;
    }
}
