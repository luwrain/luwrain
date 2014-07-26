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

package org.luwrain.app.diary;

import java.util.*;
import org.luwrain.core.Log;
import org.luwrain.controls.ListModel;
import org.luwrain.pim.*;

class EntriesModel implements ListModel
{
    static final public int MODE_TIMED = 1;
    static final public int MODE_NON_TIMED = 2;

    private DiaryStoring storing;
    private StoredDiaryEntry[] entries;
    private Date date;
    private int mode;

    public EntriesModel(DiaryStoring storing,
			Date date,
			int mode)
    {
	this.storing = storing;
	this.date = date;
	this.mode = mode;
    }

    public int getItemCount()
    {
	return entries != null?entries.length:0;
    }

    public Object getItem(int index)
    {
	if (entries == null || index >= entries.length)
	    return null;
	return entries[index];
    }

    public void refresh()
    {
	if (storing == null)
	{
	    entries = null;
	    return;
	}
	/*
	try {

	}
	catch(Exception e)
	{
	    Log.error("diary", "could not construct list of diary entries:" + e.getMessage());
	    e.printStackTrace();
	    entries = null;
	}
	*/
    }
}
