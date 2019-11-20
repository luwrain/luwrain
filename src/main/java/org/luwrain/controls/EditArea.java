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

//LWR_API 1.0

package org.luwrain.controls;

import java.util.concurrent.atomic.*; 

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.script.*;

public class EditArea extends NavigationArea
{
    static public final String INPUT_EVENT_HOOK = "luwrain.edit.multiline.input";

public interface Appearance extends MultilineEdit2.Appearance
{
    void announceLine(int index, String line);
}

    public interface ChangeListener
    {
	void onEditChange();
    }

    public interface EditFactory
    {
	MultilineEdit2 newMultilineEdit(MultilineEdit2.Params params, MultilineEditCorrector2 corrector);
    }

    static public final class Params
    {
	public Params()
	{
	}

	public Params(ControlContext context)
	{
	    NullCheck.notNull(context, "context");
	    this.context = context;
	    this.appearance = new EditUtils.DefaultEditAreaAppearance(context);
	}

	public ControlContext context = null;
	public Appearance appearance = null;
	public String name = "";
	public MutableLines content = null;
	public ChangeListener changeListener = null;
	public EditFactory editFactory = null;
    }

    protected final MutableLines content;
    protected final MultilineEditCorrector2 basicCorrector;
    protected final Appearance appearance;
    protected String areaName = "";
    protected final ChangeListener changeListener;
    protected final MultilineEdit2 edit;

    public EditArea(Params params)
    {
	super(params.context);
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.name, "params.name");
	this.areaName = params.name;
	this.content = new MutableLinesChangeListener(params.content != null?params.content:new MutableLinesImpl()){
		@Override public void onMutableLinesChange()
		{
		    if (changeListener != null)
			changeListener.onEditChange();
		}
	    };
	this.appearance = params.appearance;
	this.changeListener = params.changeListener;
this.basicCorrector = createBasicCorrector();
	    this.edit = createEdit(params);
    }

    protected MultilineEditCorrector2 createBasicCorrector()
    {
return new MultilineEditCorrectorTranslator(content, this);
    }

    protected MultilineEdit2 createEdit(Params areaParams)
    {
	NullCheck.notNull(areaParams, "areaParams");
	final MultilineEdit2.Params params = new MultilineEdit2.Params();
	params.context = context;
	params.model = basicCorrector;
	params.appearance = areaParams.appearance;
	params.regionPoint = regionPoint;
	if (areaParams.editFactory != null)
	{
	final MultilineEdit2 edit = areaParams.editFactory.newMultilineEdit(params, basicCorrector);
	if (edit != null)
	    return edit;
    }
	return new MultilineEdit2(params);
    }

    public MultilineEdit2 getEdit()
    {
	return edit;
    }

    @Override public int getLineCount()
    {
	final int value = content.getLineCount();
	return value > 0?value:1;
    }

    @Override public String getLine(int index)
    {
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	if (index >= content.getLineCount())
	    return "";
	final String line = content.getLine(index);
	return line != null?line:"";
    }

    public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < 0)
	    throw new IllegalArgumentException("index (" + index + ") may not be negative");
	content.setLine(index, line);
	context.onAreaNewContent(this);
    }

    @Override public String getAreaName()
    {
	return areaName;
    }

    public void setAreaName(String areaName)
    {
	NullCheck.notNull(areaName, "areaName");
	this.areaName = areaName;
	context.onAreaNewName(this);
    }

    public String[] getLines()
    {
	return content.getLines();
    }

    public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	content.setLines(lines);
	context.onAreaNewContent(this);
	setHotPoint(getHotPointX(), getHotPointY());
    }

    public void clear()
    {
	content.clear();
	context.onAreaNewContent(this);
	setHotPoint(0, 0);
    }

    public MutableLines getContent()
    {
	return content;
    }

    @Override public boolean onInputEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (runInputEventHook(event))
	    return true;
	if (edit.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
    }

    protected boolean runInputEventHook(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	final MultilineEdit2.Model model = edit.getMultilineEditModel();
	if (model == null || !(model instanceof MultilineEditCorrector2))
	    return false;
	final MultilineEditCorrector2 corrector = (MultilineEditCorrector2)model;
	final AtomicReference res = new AtomicReference();
	corrector.doEditAction((lines, hotPoint)->{
		try {
		    res.set(new Boolean(context.runHooks("luwrain.edit.multiline.input", new Object[]{
				    ScriptUtils.createInputEvent(event),
				    TextScriptUtils.createTextEditHookObject(EditArea.this, lines, hotPoint, regionPoint)
				}, Luwrain.HookStrategy.CHAIN_OF_RESPONSIBILITY)));
		}
		catch(RuntimeException e)
		{
		    Log.error(LOG_COMPONENT, "the luwrain.edit.multiline.input hook failed:" + e.getClass().getName() + ":" + e.getMessage());
		}
	    });
	if (res.get() == null)
	    return false;
	return ((Boolean)res.get()).booleanValue();
    }

    @Override public boolean onSystemEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (edit.onSystemEvent(event))
	    return true;
	return super.onSystemEvent(event);
    }

    @Override public boolean onAreaQuery(AreaQuery query)
    {
	NullCheck.notNull(query, "query");
	if (edit.onAreaQuery(query))
	    return true;
	return super.onAreaQuery(query);
    }

        @Override public void announceLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	appearance.announceLine(index, line);
    }

    protected String getTabSeq()
    {
	return "\t";
    }

}
