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

package org.luwrain.core;

public final class InitResult 
{
    public enum Type {
	OK,
	FAILURE,
	EXCEPTION,
	NO_STRINGS_OBJ,
	NETWORK_SERVICE_INACCESSIBLE,
    };

    private final Type type;
    private final Object arg;

    public InitResult()
    {
	this.type = Type.OK;
	this.arg = null;
    }

    public InitResult(Type type)
    {
	NullCheck.notNull(type, "type");
	this.type = type;
	this.arg = null;
    }

    public InitResult(Type type, Object arg)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(arg, "arg");
	this.type = type;
	this.arg = arg;
    }

    public InitResult(Throwable e)
    {
	NullCheck.notNull(e, "e");
	this.type = Type.EXCEPTION;
	this.arg = e;
    }

    public Type getType()
    {
	return type;
    }

    public Object getArg()
    {
	return arg;
    }

    public Throwable getException()
    {
	if (arg != null && arg instanceof Throwable)
	    return (Throwable)arg;
	return null;
    }

    public String getMessage()
    {
	if (arg != null && arg instanceof String)
	    return (String)arg;
	return null;
    }

    public boolean isOk()
    {
	return type == Type.OK;
    }

    @Override public String toString()
    {
	if (arg == null)
	    return "[" + type.toString() + "]";
	if (arg instanceof Throwable)
	{
	    final Throwable e = (Throwable)arg;
	    return "[" + type.toString() + "] " + e.getClass().getName() + ":" + e.getMessage();
	}
	return "[" + type.toString() + "] " + arg.toString();
    }
}
