/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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

package com.marigostra.luwrain.core;

public class Langs
{
    public static final int SPACE = 1;
    public static final int END_OF_LINE = 2;

    private static Language currentLang = new com.marigostra.luwrain.langs.ru.Language();

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
