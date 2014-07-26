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

package org.luwrain.pim;

import java.util.*;

public class DiaryEntry implements Comparable<DiaryEntry>
{
    public String title = "";
    public String comment = "";
    public Date dateTime = new Date();
    public int duration = 0;
    public int type = 0;
    public int status = 0;
    public int importance = 0;
    public String attributes = "";
    public String attributesType = "";

    public String toString()
    {
	return title != null?title:"";
    } 

    public int compareTo(DiaryEntry entry)
    {
	if (dateTime == null || entry.dateTime == null)
	    return 0;
	return dateTime.compareTo(entry.dateTime);
    }
}
