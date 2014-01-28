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

package org.luwrain.core.registry;

public class PathParser
{
    public static Path parse(String str)
    {
	//FIXME:
	return null;
    }

    public static Path parseAsDir(String str)
    {
	//FIXME:
	return null;
}

    //FIXME:Maybe it is a good idea to remove any slash doubling;
/*
    private String getDirectoryPath(String path)
    {
	if (path == null)
	    return new String();
	String res = path.trim();
	if (res.isEmpty() || res.equals("/"))
	    return "/";
	int ending = res.length() - 1;
	while (ending > 0 && res.charAt(ending) != '/')
	    ending--;
	if (ending < 1)
	    return "/";
	return res.substring(0, ending);
    }

    private String getValueName(String path)
    {
	if (path == null)
	    return new String();
	String res = path.trim();
	if (res.isEmpty() || res.equals("/"))
	    return new String();
	int ending = res.length() - 1;
	while (ending > 0 && res.charAt(ending) != '/')
	    ending--;
	if (res.charAt(ending) == '/')
	    return res.substring(ending + 1, res.length());
	return res.substring(ending, res.length());
    }
*/
}
