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

//LWR_API 1.0

package org.luwrain.controls;

import java.util.*;
import java.util.concurrent.atomic.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.script.*;

public class EditArea extends NavigationArea
{
public interface Appearance extends MultilineEdit.Appearance
{
    void announceLine(int index, String line);
}

    public interface ChangeListener
    {
	void onEditChange(EditArea editArea, MarkedLines lines, HotPoint hotPoint);
    }

    public interface EditUpdating
    {
	boolean editUpdate(MutableMarkedLines lines, HotPointControl hotPoint);
    }

    public interface InputEventListener
    {
	boolean onEditAreaInputEvent(EditArea area, InputEvent event);
    }

    public interface EditFactory
    {
	MultilineEdit newMultilineEdit(MultilineEdit.Params params);
    }

    static public final class Params
    {
	public Params() {}
	public Params(ControlContext context)
	{
	    NullCheck.notNull(context, "context");
	    this.context = context;
	    this.appearance = new EditUtils.DefaultEditAreaAppearance(context);
	}

	public ControlContext context = null;
	public Appearance appearance = null;
	public String name = "";
	public MutableMarkedLines content = null;
	public List<ChangeListener> changeListeners = null;
	public EditFactory editFactory = null;
	public List<InputEventListener> inputEventListeners = null;
    }

    protected final MutableMarkedLines content;
    protected final MultilineEditTranslator translator;
    protected final Appearance appearance;
    protected String areaName = "";
    protected final List<ChangeListener> changeListeners = new ArrayList<>();
    protected final MultilineEdit edit;
    protected final List<InputEventListener> inputEventListeners;

    public EditArea(Params params)
    {
	super(params.context);
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.appearance, "params.appearance");
	NullCheck.notNull(params.name, "params.name");
	this.areaName = params.name;
	this.content = params.content != null?params.content:new MutableMarkedLinesImpl();
	this.appearance = params.appearance;
	if (params.changeListeners != null)
	    this.changeListeners.addAll(params.changeListeners);
	this.translator = new MultilineEditTranslator(content, this);
	this.edit = createEdit(params);
	if (params.inputEventListeners != null)
	    this.inputEventListeners = new ArrayList<>(params.inputEventListeners); else
	    this.inputEventListeners = new ArrayList<>();
    }

public void setChangeListeners(List<ChangeListener> listeners)
{
NullCheck.notNull(listeners, "listeners");
this.changeListeners.clear();
this.changeListeners.addAll(listeners);
}

    protected MultilineEdit createEdit(Params areaParams)
    {
	NullCheck.notNull(areaParams, "areaParams");
	if (areaParams.editFactory != null)
	{
	    final MultilineEdit.Params params = new MultilineEdit.Params();
	    params.context = context;
	    params.model = translator;
	    params.appearance = areaParams.appearance;
	    params.regionPoint = regionPoint;
	    final MultilineEdit edit = areaParams.editFactory.newMultilineEdit(params);
	    if (edit != null)
		return edit;
	}
	final MultilineEdit.Params params = new MultilineEdit.Params();
	params.context = context;
	params.model = translator;
	params.appearance = areaParams.appearance;
	params.regionPoint = regionPoint;
	return new MultilineEdit(params);
    }

    public MultilineEdit getEdit()
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

    public String[] getText()
    {
	return content.getLines();
    }

    public void setText(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	content.setLines(lines);
	context.onAreaNewContent(this);
	setHotPoint(getHotPointX(), getHotPointY());
    }

    public boolean update(EditUpdating updating)
    {
	NullCheck.notNull(updating, "updating");
	if (!updating.editUpdate(content, this))
	{
	    redraw();
return false;
	}
	redraw();
notifyChangeListeners();
return true;
    }

    public void clear()
    {
	content.clear();
	context.onAreaNewContent(this);
	setHotPoint(0, 0);
    }

    public MutableMarkedLines getContent()
    {
	return content;
    }

    public void refresh()
    {
	redraw();
	context.onAreaNewName(this);
    }

    @Override public boolean onInputEvent(InputEvent event)
    {
	NullCheck.notNull(event, "event");
	if (inputEventListeners != null)
	    for(InputEventListener l: inputEventListeners)
		if (l.onEditAreaInputEvent(this, event))
		    return true;
	if (edit.onInputEvent(event))
	{
	    if (translator.commit())
	    {
		refresh();
		notifyChangeListeners();
	    }
	    return true;
	}
	return super.onInputEvent(event);
    }

    @Override public boolean onSystemEvent(SystemEvent event)
    {
	NullCheck.notNull(event, "event");
	if (edit.onSystemEvent(event))
	{
	    if (translator.commit())
	    {
		refresh();
		notifyChangeListeners();
	    }
	    return true;
	}
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

    public Appearance getEditAppearance()
    {
	return this.appearance;
    }

    protected String getTabSeq()
    {
	return "\t";
    }

    protected void notifyChangeListeners()
    {
	for(ChangeListener l: this.changeListeners)
	    l.onEditChange(this, content, this);
    }
}
