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

package org.luwrain.app.commander;

import java.io.*;
import org.luwrain.core.*;

class Operations
{
    static public void copy(Luwrain luwrain,
			    StringConstructor stringConstructor,
			    TasksArea tasks,
			    File[] filesToCopy,
			    File copyTo)
    {
	Task task = new Task(stringConstructor.copying(filesToCopy));
	tasks.addTask(task);
	DirCopyOperation op = new DirCopyOperation(luwrain, tasks, task, filesToCopy, copyTo);
	Thread t = new Thread(op);
	t.start();
    }
}
