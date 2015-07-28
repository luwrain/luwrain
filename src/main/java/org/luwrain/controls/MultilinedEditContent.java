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

import org.luwrain.core.*;

/**
 * The interface to store multilined edit content and handle its
 * modifications. The empty state of the edit can be represented in two
 * ways: without lines at all (getLineCount() returns zero) and with
 * single empty line (getLineCount() returns 1 and getLine(0) returnes an
 * empty line). Both of these ways are equal and valid.
*/
public interface MultilinedEditContent extends MutableLines
{
    boolean beginEditTrans();
    void endEditTrans();
}
