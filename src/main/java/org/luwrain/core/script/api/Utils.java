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

package org.luwrain.core.script.api;

import org.luwrain.core.*;

final class Utils
{
        static String buildNameWithDashes(String name)
    {
	NullCheck.notNull(name, "name");
	if (name.isEmpty())
	    return "";
						      final StringBuilder b = new StringBuilder();
						      b.append(Character.toLowerCase(name.charAt(0)));
					      for(int i = 1;i < name.length();++i)
					      {
						  final char c = name.charAt(i);
						  if (Character.isUpperCase(c) && Character.isLowerCase(name.charAt(i - 1)))
						      b.append("-").append(Character.toLowerCase(c)); else
						      b.append(Character.toLowerCase(c));
					      }
					      return new String(b);
    }
}
