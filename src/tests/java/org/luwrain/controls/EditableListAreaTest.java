/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

import org.junit.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.core.queries.*;

public class EditableListAreaTest extends Assert
{
    @Test public void clear()
    {
	final EditableListArea.Model model = new ListUtils.DefaultEditableModel(new String[]{"1", "2", "3"});
	final EditableListArea.Params params = new EditableListArea.Params();
	params.context = new TestingControlEnvironment();
	params.model = model;
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "test";
	final EditableListArea area = new EditableListArea(params);
	assertTrue(area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLEAR)));
	assertTrue(model.getItemCount() == 0);
    }

        @Test public void clearRegionNoRegionPoint()
    {
	for(int i = 0;i < 5;i++)
	{
	    final EditableListArea.Model model = new ListUtils.DefaultEditableModel(new String[]{"0", "1", "2", "3", "4"});
	final EditableListArea.Params params = new EditableListArea.Params();
	final TestingControlEnvironment context = new TestingControlEnvironment();
	params.context = context;
	params.model = model;
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "test";
	final EditableListArea area = new EditableListArea(params);
	for(int k = 0;k < i;++k)
	    assertTrue(area.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.ARROW_DOWN)));
	assertTrue(area.selected().equals("" + i));
	assertTrue(area.selectedIndex() == i);
	assertTrue(area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLEAR_REGION)));
	assertTrue(model.getItemCount() == 4);
	for(int k = 0;k < model.getItemCount();++k)
	    assertTrue(!model.getItem(k).equals("" + i));
	}
    }

    //FIXME:deleteRegionWithRegionPoint

            @Test public void cutNoRegionPoint()
    {
	for(int i = 0;i < 5;i++)
	{
	    final EditableListArea.Model model = new ListUtils.DefaultEditableModel(new String[]{"0", "1", "2", "3", "4"});
	final EditableListArea.Params params = new EditableListArea.Params();
	final TestingControlEnvironment context = new TestingControlEnvironment();
	params.context = context;
	params.model = model;
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	params.name = "test";
	final EditableListArea area = new EditableListArea(params);
	for(int k = 0;k < i;++k)
	    assertTrue(area.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.ARROW_DOWN)));
	assertTrue(area.selected().equals("" + i));
	assertTrue(area.selectedIndex() == i);
	assertTrue(area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.CLIPBOARD_CUT)));
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
