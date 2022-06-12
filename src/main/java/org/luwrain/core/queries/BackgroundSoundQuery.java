/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

import java.nio.file.*;

import org.luwrain.core.*;

public final class BackgroundSoundQuery extends AreaQuery
{
    protected Answer answer;

    public BackgroundSoundQuery()
    {
	super(BACKGROUND_SOUND);
    }

    public void answer(Answer answer)
    {
	NullCheck.notNull(answer, "answer");
	secondAnswerCheck();
	this.answer = answer;
	answerTaken();
    }

    public Answer getAnswer()
    {
	return answer;
    }

    static public class Answer
    {
	private BkgSounds bkgSound = null;
	private String url = null;

	public Answer()
	{
	    bkgSound = null;
	    url = null;
	}

	public Answer(BkgSounds bkgSound)
	{
	    NullCheck.notNull(bkgSound, "bkgSound");
	    this.bkgSound = bkgSound;
	    this.url = null;
	}

	public Answer(String url)
	{
	    NullCheck.notEmpty(url, "url");
	    this.url = url;
	    this.bkgSound = null;
	}

	public boolean isEmpty()
	{
	    return bkgSound == null && url == null;
	}

	public boolean isUrl()
	{
	    return url != null;
	}

	public BkgSounds getBkgSound()
	{
	    return bkgSound;
	}

	public String getUrl()
	{
	    return url;
	}
    }
}
