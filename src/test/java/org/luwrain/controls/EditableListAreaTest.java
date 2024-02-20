/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

public class EditableListAreaTest
{
    @Disabled @Test public void clear()
    {
	final EditableListArea.Model<Object> model = new ListUtils.DefaultEditableModel<Object>(Object.class, new String[]{"1", "2", "3"});
	final EditableListArea.Params params = new EditableListArea.Params();
	params.context = new TestingControlContext();
	params.model = model;
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "test";
	final EditableListArea area = new EditableListArea(params);
	assertTrue(area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLEAR)));
	assertTrue(model.getItemCount() == 0);
    }

        @Disabled @Test public void clearRegionNoRegionPoint()
    {
	for(int i = 0;i < 5;i++)
	{
	    final EditableListArea.Model<Object> model = new ListUtils.DefaultEditableModel<Object>(Object.class, new String[]{"0", "1", "2", "3", "4"});
	final EditableListArea.Params params = new EditableListArea.Params();
	final TestingControlContext context = new TestingControlContext();
	params.context = context;
	params.model = model;
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "test";
	final EditableListArea area = new EditableListArea(params);
	for(int k = 0;k < i;++k)
	    assertTrue(area.onInputEvent(new InputEvent(InputEvent.Special.ARROW_DOWN)));
	assertTrue(area.selected().equals("" + i));
	assertTrue(area.selectedIndex() == i);
	assertTrue(area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLEAR_REGION)));
	assertTrue(model.getItemCount() == 4);
	for(int k = 0;k < model.getItemCount();++k)
	    assertTrue(!model.getItem(k).equals("" + i));
	}
    }

    //FIXME:deleteRegionWithRegionPoint

            @Disabled @Test public void cutNoRegionPoint()
    {
	for(int i = 0;i < 5;i++)
	{
	    final EditableListArea.Model<Object> model = new ListUtils.DefaultEditableModel<Object>(Object.class, new String[]{"0", "1", "2", "3", "4"});
	final EditableListArea.Params params = new EditableListArea.Params();
	final TestingControlContext context = new TestingControlContext();
	params.context = context;
	params.model = model;
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "test";
	final EditableListArea area = new EditableListArea(params);
	for(int k = 0;k < i;++k)
	    assertTrue(area.onInputEvent(new InputEvent(InputEvent.Special.ARROW_DOWN)));
	assertTrue(area.selected().equals("" + i));
	assertTrue(area.selectedIndex() == i);
	assertTrue(area.onSystemEvent(new SystemEvent(SystemEvent.Code.CLIPBOARD_CUT)));
	assertTrue(model.getItemCount() == 4);
	for(int k = 0;k < model.getItemCount();++k)
	    assertTrue(!model.getItem(k).equals("" + i));
	assertTrue(context.clipboard.get().length == 1);
	assertTrue(context.clipboard.get()[0].equals("" + i));
	}
    }

    //FIXME:cutWithRegionPoint
    //FIXME:copy*
	   //FIXME:paste
}
