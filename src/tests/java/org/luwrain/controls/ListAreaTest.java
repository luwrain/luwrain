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

public class ListAreaTest extends Assert
{
    @Test public void emptyRegionTextDefaultFlags()
    {
	final ListUtils.FixedModel model = new ListUtils.FixedModel(new String[]{"123", "456", "789"});
	RegionTextQuery query = new RegionTextQuery();
	final ListArea.Params params = new ListArea.Params();
	params.context = new TestingControlEnvironment();
	params.name = "test";
	params.model = model;
	params.appearance = new ListUtils.DefaultAppearance(params.context);
	final ListArea area = new ListArea(params);
	//Without region point at all
	assertFalse(area.onAreaQuery(query));
	assertTrue(area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.REGION_POINT)));
	query = new RegionTextQuery();
	//With the region point at initial position
	assertFalse(area.onAreaQuery(query));
	//Moving hot point right on three positions
	for(int i = 0;i < 3;++i)
	    assertTrue(area.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.ARROW_RIGHT)));
	assertTrue(area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.REGION_POINT)));
	query = new RegionTextQuery();
	//With the region point shift on three positions right
	assertFalse(area.onAreaQuery(query));
    }

        @Test public void singleLineRegionText()
    {
	final ListUtils.FixedModel model = new ListUtils.FixedModel(new String[]{"123", "456", "789"});
final ListArea.Params params = new ListArea.Params();
params.context = new TestingControlEnvironment();
params.name = "test";
params.model = model;
params.appearance = new ListUtils.DefaultAppearance(params.context);
final ListArea area = new ListArea(params);
assertTrue(area.onEnvironmentEvent(new EnvironmentEvent(EnvironmentEvent.Code.REGION_POINT)));
for(int i = 1;i <= 3;++i)
{
        assertTrue(area.onKeyboardEvent(new KeyboardEvent(KeyboardEvent.Special.ARROW_RIGHT)));
	final RegionTextQuery query = new RegionTextQuery();
    assertTrue(area.onAreaQuery(query));
    final String res = query.getAnswer();
    assertNotNull(res);
    assertTrue(res.equals("123".substring(0, i)));
}
    }

    //FIXME:region query multiple lines

    //FIXME:clipboard operations
    //FIXME:navigation
}
