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

import java.util.*;
import java.io.*;

import org.luwrain.core.*;

public interface PropertiesProvider extends ExtensionObject
{
    public enum Flags {
	PUBLIC,
	READ_ONLY,
	FILE,
    };

    public interface Listener
    {
	void onNewPropertyValue(String propName, String propValue);
    }

    String[] getPropertiesRegex();
    Set<Flags> getPropertyFlags(String propName);
    String getProperty(String propName);
    boolean setProperty(String propName, String value);
    void setListener(Listener listener);
}
