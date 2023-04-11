/*
   Copyright 2012-2023 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.io.json;

import java.util.*;
import java.lang.reflect.*;

import com.google.gson.*;
import com.google.gson.reflect.*;
import com.google.gson.annotations.*;

import org.luwrain.core.*;

public class FileType
{
    public enum Type {
	SHORTCUT, JOB
    };

    static private Gson gson = null;

    @SerializedName("type")
    private Type type= null;

    @SerializedName("name")
    private String name = null;

    public FileType()
    {
    }

    public FileType(Type type)
    {
	NullCheck.notNull(type, "type");
	this.type = type;
    }

    public Type getType()
    {
	return this.type;
    }

    public void setType(Type type)
    {
	this.type = type;
    }

    public String getName()
    {
	return this.name;
    }

    public String getNameNotNull()
    {
	return name != null?name:"";
    }

    public void setBName(String namealue)
    {
	 this.name = name;
	 }

    static public String toJson(FileType fileType)
    {
	if (gson == null)
	    gson = new Gson();
	return gson.toJson(fileType);
    }

    static public FileType  fromJson(String s)
    {
	NullCheck.notNull(s, "s");
	if (gson == null)
	    gson = new Gson();
	return gson.fromJson(s, FileType.class);
    }
}
