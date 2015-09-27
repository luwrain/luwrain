/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

public interface Registry
{
    public static final int INVALID = 0;
    public static final int INTEGER = 1;
    public static final int STRING = 2;
    public static final int BOOLEAN = 3;

    //Returns false if the directory alreayd exists
    boolean addDirectory(String path);
    boolean deleteDirectory(String path);
    boolean deleteValue(String path);
    boolean getBoolean(String path);
    String[] getDirectories(String path);
    int getInteger(String path);
    String getString(String path);
    String getStringDesignationOfType(int type);
    int getTypeOf(String path);
    String[] getValues(String path);
    boolean hasDirectory(String path);
    boolean hasValue(String path);
    boolean setBoolean(String path, boolean value);
    boolean setInteger(String path, int value);
    boolean setString(String path, String value);
}
