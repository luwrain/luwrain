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

package org.luwrain.core;

abstract public class AreaQuery extends Event
{
    static public final int REGION = 0;
    static public final int OBJECT_UNIREF = 1;
    static public final int CURRENT_DIR = 2;
    static public final int CUT = 3;
    static public final int VOICED_FRAGMENT = 4;

    private int code;
    private boolean hasAnswer = false;

    public AreaQuery(int code)
    {
	//	super(AREA_QUERY_EVENT);
	this.code = code;
    }

    public final int getQueryCode()
    {
	return code;
    }

    abstract public Object getAnswer();

    public boolean hasAnswer()
    {
	return hasAnswer;
    }

    protected void answerTaken()
    {
	hasAnswer = true;
    }

    protected void secondAnswerCheck()
    {
	if (hasAnswer())
	    throw new IllegalArgumentException("Answer may not be made twice");
    } 
}
