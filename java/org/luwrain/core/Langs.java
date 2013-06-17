/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

public class Langs
{
    public static final int SPACE = 1;
    public static final int TAB = 2;
    public static final int BEGIN_OF_LINE = 3;
    public static final int END_OF_LINE = 4;
    public static final int EMPTY_LINE = 5;
    public static final int THE_FIRST_LINE = 6;
    public static final int THE_LAST_LINE = 7;
    public static final int AREA_BEGIN = 8;
    public static final int AREA_END = 9;
    public static final int TREE_AREA_BEGIN = 10;
    public static final int TREE_AREA_END = 11;

    public static final int NO_REQUESTED_ACTION = 100;
    public static final int NO_ACTIVE_AREA = 101;
    public static final int APPLICATION_INTERNAL_ERROR = 102;
    public static final int APPLICATION_CLOSE_ERROR_HAS_POPUP = 103;
    public static final int INSUFFICIENT_MEMORY_FOR_APP_LAUNCH = 104;
    public static final int UNEXPECTED_ERROR_AT_APP_LAUNCH = 105;

    private static Language currentLang = new org.luwrain.langs.ru.Language();

    public static String staticValue(int id)
    {
	if (currentLang == null)
	    return "#NO LANGUAGE#";
	String value = currentLang.getStaticStrings().getString(id);
	if (value == null || value.isEmpty())
	    return "#NO VALUE#";
	return value;
    }

public static     Object requestStringConstructor(String id)
    {
	if (currentLang == null)
	    return null;
	return currentLang.requestStringConstructor(id);
    }
}
