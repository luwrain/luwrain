/*
   Copyright 2012-2019 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.controls.block;

import org.luwrain.core.*;
import org.luwrain.browser.*;

abstract public class BlockRowFragment
{
    protected BlockObject blockObj;
    protected final int posFrom;
    protected final int posTo;

    BlockRowFragment(BlockObject blockObj)
    {
	NullCheck.notNull(blockObj, "blockObj");
	this.blockObj = blockObj;
	this.posFrom = -1;
	this.posTo = -1;
    }

    BlockRowFragment(BlockObject blockObj, int posFrom, int posTo)
    {
	NullCheck.notNull(blockObj, "blockObj");
	this.blockObj = blockObj;
	this.posFrom = posFrom;
	this.posTo = posTo;
    }


    public BlockObject getBlockObj()
    {
	return blockObj;
    }

    abstract public int getWidth();
}
