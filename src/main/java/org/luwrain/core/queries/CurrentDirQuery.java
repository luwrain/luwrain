/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import java.nio.file.*;

import org.luwrain.core.*;

public class CurrentDirQuery extends AreaQuery
{
    protected String answer = null;

    public CurrentDirQuery()
    {
	super(CURRENT_DIR);
    }

    public void answer(String currentDir)
    {
	NullCheck.notEmpty(currentDir, "currentDir");
	secondAnswerCheck();
	if (!Paths.get(currentDir).isAbsolute())
	    throw new IllegalArgumentException("currentDir must be absolute");
	this.answer = currentDir;
answerTaken();
    }

    public String getAnswer()
    {
	return answer;
    }
}
