/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.cmdtool;

import java.util.*;
import java.io.*;

import org.luwrain.base.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class App implements Application, CommandLineTool.Listener
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private SimpleArea area = null;
    
    private CommandLineTool.Instance tool = null;
    private final String toolName;
    private final String[] toolArgs;

    public App(String toolName, String[] toolArgs)
    {
	NullCheck.notEmpty(toolName, "toolName");
	NullCheck.notNullItems(toolArgs, "toolArgs");
	this.toolName = toolName;
	this.toolArgs = toolArgs.clone();
    }

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	strings = (Strings)o;
	this.luwrain = luwrain;
	createArea();
	return new InitResult();
    }

    private void createArea()
    {
	area = new SimpleArea(new DefaultControlContext(luwrain), strings.appName()){
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    switch (event.getCode())
		    {
		    case CLOSE:
			closeApp();
			return true;
		    }
		    return false;
		}
	    };
    }

    @Override public void onStatusChange(CommandLineTool.Instance instance)
    {
    }

    @Override public void onSingleLineStateChange(CommandLineTool.Instance instance)
    {
    }

    @Override public void onMultilineStateChange(CommandLineTool.Instance instance)
    {
    }

    @Override public void onNativeStateChange(CommandLineTool.Instance instance)
    {
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(area);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }

        @Override public String getAppName()
    {
	return strings.appName();
    }
}
