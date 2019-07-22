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

package org.luwrain.base;

import java.io.IOException;
import java.nio.file.Path;

import org.luwrain.core.*;

public interface FilesOperation extends Runnable
{
    public enum ConfirmationChoices
    {
	OVERWRITE,
	SKIP,
	CANCEL
    };

    public interface Listener
    {
	void onOperationProgress(FilesOperation operation);
	ConfirmationChoices confirmOverwrite(Path path);
    }

    public final class Result 
    {
	public enum Type { OK, INTERRUPTED, EXCEPTION, MOVE_DEST_NOT_DIR, SOURCE_PARENT_OF_DEST};

	private final Type type;
	private final String extInfo;
	private final Exception exception;

	public Result()
	{
	    this.type = Type.OK;
	    this.extInfo = null;
	    this.exception = null;
	}

	public Result(Type type)
	{
	    NullCheck.notNull(type, "type");
	    this.type = type;
	    this.extInfo = null;
	    this.exception = null;
	}

	public Result(Type type, String extInfo)
	{
	    NullCheck.notNull(type, "type");
	    this.type = type;
	    this.extInfo = extInfo;
	    this.exception = null;
	}

	public Result(Type type, Exception exception)
	{
	    NullCheck.notNull(type, "type");
	    this.type = type;
	    this.extInfo = null;
	    this.exception = exception;
	}

	public Result(Type type, String extInfo, Exception exception)
	{
	    NullCheck.notNull(type, "type");
	    this.type = type;
	    this.extInfo = extInfo;
	    this.exception = exception;
	}

	public boolean isOk()
	{
	    return type == Type.OK;
	}

	public Type getType()
	{
	    return type;
	}

	public String getExtInfo()
	{
	    return extInfo;
	}

	public Exception getException()
	{
	    return exception;
	}

	@Override public String toString()
	{
	    return type.toString() + ", " +
	    (extInfo != null?extInfo:"[no extended info]") + ", " +
	    (exception != null?(exception.getClass().getName() + ":" + exception.getMessage()):"[no exception]");
	}
    }

    String getOperationName();
    int getPercents();
    void interrupt();
    boolean isFinished();
    Result getResult();
    boolean finishingAccepted();
}
