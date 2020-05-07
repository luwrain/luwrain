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

//LWR_API 1.0

package org.luwrain.i18n;

import java.util.*;

public interface I18n
{
    String staticStr(LangStatic id);
    String getStaticStr(String id);
    String hasSpecialNameOfChar(char ch);
    String getCommandTitle(String command);
    Object getStrings(String component);
    String getPastTimeBrief(Date date);
    String getNumberStr(int count, String entities);
    String getExceptionDescr(Exception e);
    Lang getActiveLang();
    Lang getLang(String langName);
}
