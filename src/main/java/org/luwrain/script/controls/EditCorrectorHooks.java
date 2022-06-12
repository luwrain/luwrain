/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

//LWR_API 2.0

package org.luwrain.script.controls;

import java.util.*;
import java.util.concurrent.atomic.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.script.*;
import org.luwrain.script.core.*;
import org.luwrain.util.*;
import org.luwrain.controls.MultilineEdit.ModificationResult;

/**
 * A translator of text correcting operations to hooks actions. This
 * class can wrap any instance of {@link MultilineEditCorrector} with
 * calls of the hooks prior and after of the corresponding action of the
 * underlying corrector.
 * <p>
 * The call of the hook prior to the operation may cancel the operation,
 * if it returns {@code false}. If it returns any other value , including
 * {@code null} and {@code undefined}, the processing will be
 * continued. The hooks called after the operation may return any value,
 * it is never taken into account in any way.
 * <p>
 * All hooks handlers must expect the single argument with members,
 * which set depends on the purpose of the particular operation. In the
 * meantime, the argument always has the {@code lines} and {@code
 * hotPoint} members, since they are actual for any operation.
 * <p>
 * Here is the list of the hooks created by this class where {@code base}
 * is a string value provided with the constructor:
 * <ul>
 *   <li>{@code base.delete.char.pre}</li>
 *   <li>{@code base.delete.char.post}</li>
 *   <li>{@code base.delete.region.pre}</li>
 *   <li>{@code base.delete.region.post}</li>
 *   <li>{@code base.insert.region.pre}</li>
 *   <li>{@code base.insert.region.post}</li>
 *   <li>{@code base.insert.chars.pre}</li>
 *   <li>{@code base.insert.chars.post}</li>
 *   <li>{@code base.merge.lines.pre}</li>
 *   <li>{@code base.merge.lines.post}</li>
 *   <li>{@code base.split.lines.pre}</li>
 *   <li>{@code base.split.lines.post}</li>
 * </ul>
 */
public class EditCorrectorHooks implements MultilineEditCorrector
{
    protected final HookContainer context;
    protected final MultilineEditCorrector base;
    protected final String hookNameBase;

    public EditCorrectorHooks(HookContainer hookContainer, MultilineEditCorrector base, String hookNameBase)
    {
	NullCheck.notNull(hookContainer, "hookContainer");
	NullCheck.notNull(base, "base");
	NullCheck.notEmpty(hookNameBase, "hookNameBase");
	this.context = hookContainer;
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
	final Map<String, Object> values = new HashMap<>();
	if (!runPre(hookNameBase + ".delete.char.pre", values))
	    return new ModificationResult(false);
	final ModificationResult res = base.deleteChar(pos, lineIndex);
	if (res.isPerformed())
	    runPost(hookNameBase + ".delete.char.post", values);
	return res;
    }

    @Override public ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	final Map<String, Object> values = new HashMap<>();
	if (!runPre(hookNameBase + ".delete.region.pre", values))
	    return new ModificationResult(false);
	final ModificationResult res = base.deleteRegion(fromX, fromY, toX, toY);
	if (res.isPerformed())
	    runPost(hookNameBase + ".delete.region.post", values);
	return res;
    }

    @Override public ModificationResult insertRegion(int x, int y, String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	final Map<String, Object> values = new HashMap<>();
	if (!runPre(hookNameBase + ".insert.region.pre", values))
	    return new ModificationResult(false);
	final ModificationResult res = base.insertRegion(x, y, lines);
	if (res.isPerformed())
	    runPost(hookNameBase + ".insert.region.post", values);
	return res;
    }

    @Override public ModificationResult putChars(int pos, int lineIndex, String str)
    {
	NullCheck.notNull(str, "str");
	final Map<String, Object> values = new HashMap<>();
	values.put("chars", str);
	values.put("x", new Integer(pos));
	values.put("y", new Integer(lineIndex));
	if (!runPre(hookNameBase + ".insert.chars.pre", values))
	    return new ModificationResult(false);
	final ModificationResult res = base.putChars(pos, lineIndex, str);
	if (res.isPerformed())
	    runPost(hookNameBase + ".insert.chars.post", values);
	return res;
    }

    @Override public ModificationResult mergeLines(int firstLineIndex)
    {
	final Map<String, Object> values = new HashMap<>();
	if (!runPre(hookNameBase + ".merge.lines.pre", values))
	    return new ModificationResult(false);
	final ModificationResult res = base.mergeLines(firstLineIndex);
	if (res.isPerformed())
	    runPost(hookNameBase + ".merge.lines.post", values);
	return res;
    }

    @Override public ModificationResult splitLine(int pos, int lineIndex)
    {
	final Map<String, Object> values = new HashMap<>();
	if (!runPre(hookNameBase + ".split.lines.pre", values))
	    return new ModificationResult(false);
	final ModificationResult res = base.splitLine(pos, lineIndex);
	if (res.isPerformed())
	    runPost(hookNameBase + ".split.post", values);
	return res;
    }

    @Override public ModificationResult doEditAction(TextEditAction action)
    {
	NullCheck.notNull(action, "action");
	final Map readONlyValues = new HashMap();
	final Map values = new HashMap();
	return base.doEditAction(action);
    }

    protected boolean runPre(String hookName, Map<String, Object> values)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNull(values, "readOnlyValues");
	final AtomicBoolean res = new AtomicBoolean(true);
	doEditAction((lines, hotPoint)->{
		final Map<String, Object> arg = new HashMap<>(values);
		arg.put("lines", new MutableLinesArray(lines));
		arg.put("hotPoint", new HotPointObj(hotPoint));
		res.set(Hooks.permission(context, hookName, new Object[]{new MapScriptObject(arg)}));
	    });
	return res.get();
    }

    protected void runPost(String hookName, Map<String, Object> values)
    {
	NullCheck.notEmpty(hookName, "hookName");
	NullCheck.notNull(values, "readOnlyValues");
	doEditAction((lines, hotPoint)->{
		final Map<String, Object> arg = new HashMap<>(values);
		arg.put("lines", new MutableLinesArray(lines));
		arg.put("hotPoint", new HotPointObj(hotPoint));
		Hooks.notification(context, hookName, new Object[]{new MapScriptObject(arg)});
	    });
    }
}
