/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls;

import java.util.*;

public class ByNameCommanderComparator implements Comparator
{
    @Override public int compare(Object o1, Object o2)
    {
	if (!(o1 instanceof CommanderArea.Entry) || !(o2 instanceof CommanderArea.Entry))
	    return 0;
	final CommanderArea.Entry i1 = (CommanderArea.Entry)o1;
	final CommanderArea.Entry i2 = (CommanderArea.Entry)o2;
	if (i1.file().getName().equals(CommanderArea.PARENT_DIR))
	    return i2.file().getName().equals(CommanderArea.PARENT_DIR)?0:-1;
	if (i2.file().getName().equals(CommanderArea.PARENT_DIR))
	    return i1.file().getName().equals(CommanderArea.PARENT_DIR)?0:1;
	if (i1.type() == CommanderArea.Entry.DIRECTORY && i2.type() == CommanderArea.Entry.DIRECTORY)
	    return i1.file().getName().compareTo(i2.file().getName());
	if (i1.type() == CommanderArea.Entry.DIRECTORY)
	    return -1;
	if (i2.type() == CommanderArea.Entry.DIRECTORY)
	    return 1;
	return i1.file().getName().compareTo(i2.file().getName());
    }
}
