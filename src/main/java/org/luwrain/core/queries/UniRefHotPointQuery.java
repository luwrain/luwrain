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

package org.luwrain.core.queries;

import org.luwrain.core.*;

public final class UniRefHotPointQuery extends AreaQuery
{
    private String uniRef = null;

    public UniRefHotPointQuery()
    {
	super(UNIREF_HOT_POINT);
    }

    public void answer(String uniRef)
    {
	NullCheck.notEmpty(uniRef, "uniRef");
	secondAnswerCheck();
	this.uniRef = uniRef;
answerTaken();
    }

    @Override public String getAnswer()
    {
	return uniRef;
    }
}
