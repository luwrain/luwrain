/*
   Copyright 2012-2016 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/orspackage
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.controls;

import java.util.*;
import java.nio.file.*;

public class ByNameCommanderComparator implements Comparator
{
    @Override public int compare(Object o1, Object o2)
    {
	if (!(o1 instanceof CommanderArea.Entry) || !(o2 instanceof CommanderArea.Entry))
	    return 0;
	final CommanderArea.Entry i1 = (CommanderArea.Entry)o1;
	final CommanderArea.Entry i2 = (CommanderArea.Entry)o2;
	if (i1.parent())
	    return i2.parent()?0:-1;
	if (i2.parent())
	    return i1.parent()?0:1;
	if (Files.isDirectory(i1.path()) && Files.isDirectory(i2.path()))//We don't use Entry.type() because it  returns symlink even on a directory
	    return i1.baseName().compareTo(i2.baseName());
	    if (Files.isDirectory(i1.path()))
	    return -1;
	    if (Files.isDirectory(i2.path()))
	    return 1;
		return i1.baseName().compareTo(i2.baseName());
    }
}
