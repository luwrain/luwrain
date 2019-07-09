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

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class EditArea2 extends NavigationArea
{
    public interface ChangeListener
    {
	void onEditChange();
    }

    public interface MultilineEditFactory
    {
	MultilineEdit2 newMultilineEdit(MultilineEditCorrector2 corrector, MutableLines lines, HotPointControl hotPoint);
    }

    static public final class Params
    {
	public ControlContext context = null;
	public String name = "";
	public MutableLines content = null;
	public ChangeListener changeListener = null;
	public MultilineEditFactory multilineEditFactory = null;
    }

    protected final MutableLines content;
    protected final MultilineEditCorrector2 basicCorrector;
    protected String areaName = "";
    protected final ChangeListener changeListener;
    protected final MultilineEdit2 edit;

    public EditArea2(Params params)
    {
	super(params.context);
	NullCheck.notNull(params, "params");
	NullCheck.notNull(params.name, "params.name");
	this.areaName = params.name;
	this.content = params.content != null?params.content:new MutableLinesImpl();
	this.changeListener = params.changeListener;
this.basicCorrector = createBasicCorrector();
	MultilineEdit2 e = null;
	if (params.multilineEditFactory != null)
	    e = params.multilineEditFactory.newMultilineEdit(basicCorrector, content, this);
	if (e != null)
	    this.edit = e; else
	    this.edit = new MultilineEdit2(context, basicCorrector, regionPoint);
    }

    protected MultilineEditCorrector2 createBasicCorrector()
    {
	final MultilineEditCorrector2 corrector = new MultilineEditModelTranslator2(content, this);
	return new EditUtils2.CorrectorChangeListener(corrector){
	    @Override public void onMultilineEditChange()
	    {
		if (changeListener != null)
		    changeListener.onEditChange();
	    }};
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
	if (edit.onInputEvent(event))
	    return true;
	return super.onInputEvent(event);
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

    protected String getTabSeq()
    {
	return "\t";
    }
}
