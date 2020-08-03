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

package org.luwrain.core.shell.desktop;

import java.util.*;
import java.io.File;
import java.net.URL;
import com.google.gson.annotations.*;

import org.luwrain.core.*;


final class DesktopItem
{
    static final String TYPE_UNIREF = "uniref";

    @SerializedName("type")
    private String type = null;

    @SerializedName("value")
    private String value = null;

    @SerializedName("title")
    private String title = null;

    private transient UniRefInfo uniRefInfo = null;

    DesktopItem()
    {
    }

    DesktopItem(UniRefInfo uniRefInfo)
    {
	NullCheck.notNull(uniRefInfo, "uniRefInfo");
	this.type = TYPE_UNIREF;
	this.value = uniRefInfo.toString();
	this.title = null;
	this.uniRefInfo = uniRefInfo;
    }

    DesktopItem(Luwrain luwrain, File file)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(file, "file");
	this.type = TYPE_UNIREF;
	this.value = "file:" + file.getAbsolutePath();
	this.title = null;
	this.uniRefInfo = luwrain.getUniRefInfo(this.value);
    }

        DesktopItem(Luwrain luwrain, URL url)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(url, "url");
	this.type = TYPE_UNIREF;
	this.value = "url:" + url.toString();
	this.title = null;
	this.uniRefInfo = luwrain.getUniRefInfo(this.value);
    }

            DesktopItem(Luwrain luwrain, String str)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(str, "str");
	this.type = TYPE_UNIREF;
	if (!str.trim().isEmpty())
	    this.value = "static:" + str.trim(); else
	    this.value = "empty:";
	this.title = null;
	this.uniRefInfo = luwrain.getUniRefInfo(this.value);
    }

    UniRefInfo getUniRefInfo(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	if (this.uniRefInfo != null)
	    return this.uniRefInfo;
	this.uniRefInfo = luwrain.getUniRefInfo(getValue());
	return this.uniRefInfo;
    }

    String getValue()
    {
	return value != null?value:"";
    }
}
