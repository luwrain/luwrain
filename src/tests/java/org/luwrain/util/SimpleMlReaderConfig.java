/*
   Copyright 2012-2015 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.util;

class SimpleMlReaderConfig implements MlReaderConfig
{
    private String[] nonClosingTags = new String[]{
	"br",
	"meta",
	"link",
"img"
    };

    @Override public boolean mlAdmissibleTag(String tagName)
    {
	return true;
    }

    @Override public boolean mlTagMustBeClosed(String tagName)
    {
	for(String s: nonClosingTags)
	    if (s.equals(tagName.toLowerCase()))
		return false;
	return true;
    }
}
