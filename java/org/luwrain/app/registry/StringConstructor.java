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

package org.luwrain.app.registry;

public interface StringConstructor
{
    String dirsAreaName();
    String valuesAreaName();
    String rootItemTitle();
    String introduceStringValue(String name, String value);
    String introduceIntegerValue(String name, String value);
    String introduceBooleanValue(String name, boolean value);
    String yes();
    String no();
    String newDirectoryTitle();
    String newDirectoryPrefix(String parentName);
    String directoryNameMayNotBeEmpty();
    String directoryInsertionRejected(String parentName, String dirName);
    String newParameterTitle();
    String newParameterName();
    String 	newParameterType();
    String parameterNameMayNotBeEmpty();
    String invalidParameterType(String type);
    String parameterInsertionFailed();
}
