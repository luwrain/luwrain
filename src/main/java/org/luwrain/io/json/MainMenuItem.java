/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

public class MainMenuItem
{
    static public final String TYPE_UNIREF = "uniref";
        static public final Type LIST_TYPE = new TypeToken<List<MainMenuItem>>(){}.getType();

    static private Gson gson = null;

    @SerializedName("type")
    private String type = null;

    @SerializedName("value")
    private String value = null;

    public MainMenuItem()
    {
    }

    public MainMenuItem(String type, String value)
    {
	this.type = type;
	this.value = value;
    }

    public String getType()
    {
	return this.type;
    }

    public String getTypeNotNull()
    {
	return type != null?type:"";
    }

    public void setType(String type)
    {
	this.type = type;
    }

    public String getValue()
    {
	return this.value;
    }

    public String getValueNotNull()
    {
	return value != null?value:"";
    }

    public void setValue(String value)
    {
	 this.value = value;
	 }

    static public String toJson(MainMenuItem[] items)
    {
	if (gson == null)
	    gson = new Gson();
	return gson.toJson(items);
    }

    static public MainMenuItem[]  fromJson(String s)
    {
	NullCheck.notNull(s, "s");
	if (gson == null)
	    gson = new Gson();
final List<MainMenuItem> res = new Gson().fromJson(s, LIST_TYPE);
return res != null?res.toArray(new MainMenuItem[res.size()]):new MainMenuItem[0];
    }
}
