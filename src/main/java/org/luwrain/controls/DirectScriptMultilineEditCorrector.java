/*
   Copyright 2012-2019 Michael Pozhidaev <msp@luwrain.org>

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

import java.util.*;
import java.util.concurrent.atomic.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.script.*;
import org.luwrain.util.*;


import org.luwrain.controls.MultilineEdit2.ModificationResult;

public class DirectScriptMultilineEditCorrector implements MultilineEditCorrector2
{
    static protected final String LOG_COMPONENT = "controls";

    protected final ControlContext context;
    protected final MultilineEditCorrector2 base;
    protected final String hookNameBase;

    public DirectScriptMultilineEditCorrector(ControlContext context, MultilineEditCorrector2 base, String hookNameBase)
    {
	NullCheck.notNull(context, "context");
	NullCheck.notNull(base, "base");
	NullCheck.notEmpty(hookNameBase, "hookNameBase");
	this.context = context;
	this.base = base;
	this.hookNameBase = hookNameBase;
    }

    @Override public String getLine(int index)
    {
	return base.getLine(index);
    }

    @Override public int getLineCount()
    {
	return base.getLineCount();
    }

    @Override public int getHotPointX()
    {
	return base.getHotPointX();
    }

    @Override public int getHotPointY()
    {
	return base.getHotPointY();
    }

    @Override public String getTabSeq()
    {
	return base.getTabSeq();
    }

    @Override public ModificationResult deleteChar(int pos, int lineIndex)
    {
	if (!runPreHook(hookNameBase + ".delete.char.pre"))
	    return new ModificationResult(false);
final ModificationResult res = base.deleteChar(pos, lineIndex);
if (!res.isPerformed())
    return res;
runPostHook(hookNameBase + ".delete.char.post");
return res;
    }

    @Override public ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	if (!runPreHook(hookNameBase + ".delete.region.pre"))
	    return new ModificationResult(false);
final ModificationResult res = base.deleteRegion(fromX, fromY, toX, toY);
if (!res.isPerformed())
    return res;
runPostHook(hookNameBase + ".delete.region.post");
return res;
    }

    @Override public ModificationResult insertRegion(int x, int y, String[] lines)
    {
		NullCheck.notNullItems(lines, "lines");
		if (!runPreHook(hookNameBase + ".insert.region.pre"))
		    return new ModificationResult(false);
final ModificationResult res = base.insertRegion(x, y, lines);
runPostHook(hookNameBase + ".insert.region.post");
		    return res;
    }

    @Override public ModificationResult putChars(int pos, int lineIndex, String str)
    {
	NullCheck.notNull(str, "str");
	if (!runPreHook(hookNameBase + ".insert.chars.pre"))
	    return new ModificationResult(false);
	final ModificationResult res = base.putChars(pos, lineIndex, str);
	if (res.isPerformed())
	runPostHook(hookNameBase + ".insert.chars.post");
	return res;
    }

    @Override public ModificationResult mergeLines(int firstLineIndex)
    {
				if (!runPreHook(hookNameBase + ".merge.lines.pre"))
		    return new ModificationResult(false);
final ModificationResult res = base.mergeLines(firstLineIndex);
if (!res.isPerformed())
    return res;
runPostHook(hookNameBase + ".merge.lines.post");
				    return res;
    }

    @Override public ModificationResult splitLine(int pos, int lineIndex)
    {
			if (!runPreHook(hookNameBase + ".split.lines.pre"))
		    return new ModificationResult(false);
	final ModificationResult res = base.splitLine(pos, lineIndex);
	if (!res.isPerformed())
	return res;
	runPostHook(hookNameBase + ".split.post");
	return res;
    }

    @Override public ModificationResult doEditAction(TextEditAction action)
    {
	NullCheck.notNull(action, "action");
	return base.doEditAction(action);
    }

    protected boolean runPreHook(String hookName)
    {
	NullCheck.notEmpty(hookName, "hookName");
	final AtomicBoolean mayContinue = new AtomicBoolean(true);
	doEditAction((lines, hotPoint)->{
		final HookObject linesObj = new MutableLinesHookObject(lines);
		final HookObject hotPointObj = new HotPointControlHookObject(hotPoint);
		final HookObject arg = new EmptyHookObject(){
			@Override public Object getMember(String name)
			{
			    NullCheck.notNull(name, "name");
			    switch(name)
			    {
			    case "lines":
				return linesObj;
			    case "hotPoint":
				return hotPointObj;
			    default:
				return super.getMember(name);
			    }
			}
		    };
		final AtomicReference ex = new AtomicReference();
		context.runHooks(hookName, (hook)->{
			final Object res;
			try {
			    res = hook.run(new Object[]{arg});
			}
			catch(RuntimeException e)
			{
			    ex.set(e);
			    return Luwrain.HookResult.BREAK;
			}
			if (res == null || !(res instanceof Boolean))
			    return Luwrain.HookResult.CONTINUE;
			final Boolean b = (Boolean)res;
			if (!b.booleanValue())
			{
			    mayContinue.set(false);
			    return Luwrain.HookResult.BREAK;
			}
			return Luwrain.HookResult.CONTINUE;
		    });
		if (ex.get() != null)
		{
		    final RuntimeException e = (RuntimeException)ex.get();
		    Log.error(LOG_COMPONENT, "unable to run the hook " + hookName + ":" + e.getClass().getName() + ":" + e.getMessage());
		    mayContinue.set(false);
		    return;
		}
	    });
	return mayContinue.get();
    }

        protected boolean runPostHook(String hookName)
    {
	NullCheck.notEmpty(hookName, "hookName");
	final AtomicBoolean success = new AtomicBoolean(true);
	doEditAction((lines, hotPoint)->{
		final HookObject linesObj = new MutableLinesHookObject(lines);
		final HookObject hotPointObj = new HotPointControlHookObject(hotPoint);
		final HookObject arg = new EmptyHookObject(){
			@Override public Object getMember(String name)
			{
			    NullCheck.notNull(name, "name");
			    switch(name)
			    {
			    case "lines":
				return linesObj;
			    case "hotPoint":
				return hotPointObj;
			    default:
				return super.getMember(name);
			    }
			}
		    };
		final AtomicReference ex = new AtomicReference();
		context.runHooks(hookName, (hook)->{
			try {
hook.run(new Object[]{arg});
			}
			catch(RuntimeException e)
			{
			    ex.set(e);
			    return Luwrain.HookResult.BREAK;
			}
			    return Luwrain.HookResult.CONTINUE;
		    });
		if (ex.get() != null)
		{
		    final RuntimeException e = (RuntimeException)ex.get();
		    Log.error(LOG_COMPONENT, "unable to run the hook " + hookName + ":" + e.getClass().getName() + ":" + e.getMessage());
		    success.set(false);
		    return;
		}
	    });
	return success.get();
    }
}
