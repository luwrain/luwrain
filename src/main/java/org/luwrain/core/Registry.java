/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.core;

public interface Registry
{
    boolean addDirectory(String path);
    boolean deleteDir(String path);
    boolean deleteValue(String path);
    boolean getBoolean(String path);
    String[] getDirectories(String path);
    int getInteger(String path);
    String getString(String path);
    int getTypeOf(String path);
    String[] getValues(String path);
    boolean hasDirectory(String path);
    boolean hasValue(String path);
    boolean setBoolean(String path, boolean value);
    boolean setInteger(String path, int value);
    boolean setString(String path, String value);
}
