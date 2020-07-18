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

package org.luwrain.base;

import java.net.*;
import java.util.*;

import org.luwrain.core.*;

public interface MediaResourcePlayer extends ExtensionObject 
{
    public enum Flags {}

    static public final class Result
    {
	public enum Type {OK, INACCESSIBLE_SOURCE};

	private final Type type;

	public Result()
	{
	    this.type = Type.OK;
	}

		public Result(Type type)
	{
	    NullCheck.notNull(type, "type");
	    this.type = Type.OK;
	}

	public Type  getType()
	{
	    return type;
	}

	public boolean isOk()
	{
	    return type == Type.OK;
	}
    }

    public interface Listener
    {
	void onPlayerTime(Instance instance, long msec);
	void onPlayerFinish(Instance instance);
	void onPlayerError(Exception e);//FIXME:instance
    }

    static public final class Params
    {
	public long playFromMsec = 0;
	public int volume = 100;
	public Set<Flags> flags = EnumSet.noneOf(Flags.class);
    }

    public interface Instance
    {
	Result play(URL url, Params params);
	void setVolume(int volume);
	void stop();
    }

    Instance newMediaResourcePlayer(Listener listener);
    String getSupportedMimeType();//FIXME:multiple types
}
