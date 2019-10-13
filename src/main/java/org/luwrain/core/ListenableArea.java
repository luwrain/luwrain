/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core;

public interface ListenableArea
{
    ListeningInfo onListeningStart();
    void onListeningFinish(ListeningInfo listeningInfo);

    static public class ListeningInfo
    {
	protected final String text;
	protected final int posX;
	protected final int posY;
	public ListeningInfo(String text, int posX, int posY)
	{
	    NullCheck.notNull(text, "text");
	    this.text = text;
	    this.posX = posX;
	    this.posY = posY;
	}
	public ListeningInfo(String text)
	{
	    this(text, -1, -1);
	}
	public ListeningInfo()
	{
	    this("", -1, -1);
	}
	public final String getText()
	{
	    return this.text;
	}
	public final int getPosX()
	{
	    return this.posX;
	}
	public final int getPosY()
	{
	    return this.posY;
	}
	public final boolean noMore()
	{
	    return text.isEmpty();
	}
	@Override public String toString()
	{
	    return this.text;
	}
    }
}
