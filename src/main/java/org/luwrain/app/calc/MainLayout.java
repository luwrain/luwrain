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

package org.luwrain.app.calc;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.controls.MultilineEdit.ModificationResult;
import org.luwrain.app.base.*;

public final class MainLayout extends LayoutBase
{
    static private final String LOG_COMPONENT = "calc";

    private final App app;
    private final EditArea editArea;

    MainLayout(App app)
    {
	super(app);
	this.app = app;
	this.editArea = new EditArea(null/*base.createEditParams(()->hotUpdate())*/){
		@Override public boolean onInputEvent(InputEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (!event.isSpecial())
			switch(event.getChar())
			{
			case '\'':
			case '\"':
			case ';':
			    return false;
			case '=':
			    return onSystemEvent(new SystemEvent(SystemEvent.Code.OK));
			default:
			    if (getContent().getLineCount() == 4 &&
				getContent().getLine(0).equals("0") &&
				getHotPointX() == 0 &&
				getHotPointY() == 0)
				getContent().setLine(0, "");
			    return super.onInputEvent(event);
			}
		    return super.onInputEvent(event);
		}
		@Override public boolean onSystemEvent(SystemEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != SystemEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch (event.getCode())
		    {
		    case CLEAR:
			setLines(new String[]{"0", "", "# 0", ""});
			setHotPoint(0, 0);
			return true;
		    case OK:
			try {
			    final Number res = app.calculate(getLinesToEval());
			    if (res != null)
				app.message(getLuwrain().getSpeakableText(formatNum(res), Luwrain.SpeakableTextType.PROGRAMMING), Luwrain.MessageType.OK); else
				app.message("0", Luwrain.MessageType.OK);
			    return true;
			}
			catch(Exception e)
			{
			    Log.debug(LOG_COMPONENT, "calculation faild:" + e.getClass().getName() + ":" + e.getMessage());
			    e.printStackTrace();
			    getLuwrain().playSound(Sounds.ERROR);
				return true;
			    }
		    default:
			return super.onSystemEvent(event);
		    }
		}
	    };
	editArea.getContent().setLines(new String[]{
		"0",
		"",
		"# 0",
		"",
	    });
    }

    private void hotUpdate()
    {
	try {
	    final Number res = app.calculate(getLinesToEval());
	    if (res != null)
		putResLine("# " + formatNum(res)); else
		putResLine("# 0");
	}
	catch(Exception e)
	{
	    //FIXME:putResLine("# " + strings.error());
	}
    }

    private void putResLine(String text)
    {
	NullCheck.notNull(text, "text");
	editArea.getDirectContent().setLine(editArea.getLineCount() - 2, text);
    }

    private String[] getLinesToEval()
    {
	final String[] lines = editArea.getLines();
	final List<String> res = new LinkedList();
	for(int i = 0;i < lines.length - 3;i++)
	    res.add(lines[i]);
	return res.toArray(new String[res.size()]);
    }

    private String formatNum(Number num)
    {
	if (num instanceof Integer || num instanceof Long)
	    return "" + num.intValue();
	return String.format("%.5f", num.floatValue());
    }


    /*
    static private MultilineEdit.Model createBlockingModel(MultilineEdit.Model origModel)
    {
	return new MultilineEdit.Model(){
	    @Override public int getLineCount()
	    {
		return origModel.getLineCount();
	    }
	    @Override public String getLine(int index)
	    {
		return origModel.getLine(index);
	    }
	    @Override public int getHotPointX()
	    {
		return origModel.getHotPointX();
	    }
	    @Override public int getHotPointY()
	    {
		return origModel.getHotPointY();
	    }
	    @Override public String getTabSeq()
	    {
		return origModel.getTabSeq();
	    }
	    @Override public char deleteChar(int pos, int lineIndex)
	    {
		if (lineIndex >= getLineCount() - 3)
		    return '\0';
		return origModel.deleteChar(pos, lineIndex);
	    }
	    @Override public boolean deleteRegion(int fromX, int fromY, int toX, int toY)
	    {
		final int x;
		final int y;
		if (fromY < toY)
		{
		    y = toY;
		    x = toX;
		} else
		    if (fromY > toY)
		    {
			y = fromY;
			x = fromX;
		    } else
		    {
			y = fromY;
			x = Math.max(fromX, toX);
		    }
		final int count = getLineCount();
		if (y >= count - 2)
		    return false;
		if (y == count - 3 && x > 0)
		    return false;
		//There may not be less than 4 lines
		if (count - (Math.abs(toY - fromY)) < 4)
		    return false;
		return origModel.deleteRegion(fromX, fromY, toX, toY);
	    }
	    @Override public boolean insertRegion(int x, int y, String[] lines)
	    {
		NullCheck.notNullItems(lines, "lines");
		if (y >= getLineCount() - 3)
		    return false;
		for(String s: lines)
		{
		    		    if (s.indexOf(";") >= 0)
			return false;
		    if (s.indexOf("=") >= 0)
			return false;
		    if (s.indexOf("\'") >= 0)
			return false;
		    if (s.indexOf("\"") >= 0)
			return false;
		}
		return origModel.insertRegion(x, y, lines);
	    }
	    @Override public boolean insertChars(int pos, int lineIndex, String str)
	    {
		if (lineIndex >= getLineCount() - 3)
		    return false;
		return origModel.insertChars(pos, lineIndex, str);
	    }
	    @Override public boolean mergeLines(int firstLineIndex)
	    {
		final int count = getLineCount();
		if (count <= 4)
		    return false;
		if (firstLineIndex >= count - 3)
		    return false;
		if (firstLineIndex == count - 4 && !getLine(firstLineIndex).isEmpty())
		    return false;
		return origModel.mergeLines(firstLineIndex);
	    }
	    @Override public String splitLine(int pos, int lineIndex)
	    {
		if (lineIndex >= getLineCount() - 3)
		    return null;
		return origModel.splitLine(pos, lineIndex);
	    }
	};
    }
    */

        static private MultilineEdit.Model createBlockingModel(MultilineEdit.Model origModel)
    {
	return new MultilineEdit.Model(){
	    @Override public int getLineCount()
	    {
		return origModel.getLineCount();
	    }
	    @Override public String getLine(int index)
	    {
		return origModel.getLine(index);
	    }
	    @Override public int getHotPointX()
	    {
		return origModel.getHotPointX();
	    }
	    @Override public int getHotPointY()
	    {
		return origModel.getHotPointY();
	    }
	    @Override public String getTabSeq()
	    {
		return origModel.getTabSeq();
	    }
	    @Override public ModificationResult deleteChar(int pos, int lineIndex)
	    {
		if (lineIndex >= getLineCount() - 3)
		    return new ModificationResult(false);
		return origModel.deleteChar(pos, lineIndex);
	    }
	    @Override public ModificationResult deleteRegion(int fromX, int fromY, int toX, int toY)
	    {
		final int x;
		final int y;
		if (fromY < toY)
		{
		    y = toY;
		    x = toX;
		} else
		    if (fromY > toY)
		    {
			y = fromY;
			x = fromX;
		    } else
		    {
			y = fromY;
			x = Math.max(fromX, toX);
		    }
		final int count = getLineCount();
		if (y >= count - 2)
		    return new ModificationResult(false);
		if (y == count - 3 && x > 0)
		    return new ModificationResult(false);
		//There must be not less than 4 lines
		if (count - (Math.abs(toY - fromY)) < 4)
		    return new ModificationResult(false);
		return origModel.deleteRegion(fromX, fromY, toX, toY);
	    }
	    @Override public ModificationResult insertRegion(int x, int y, String[] lines)
	    {
		NullCheck.notNullItems(lines, "lines");
		if (y >= getLineCount() - 3)
		    return new ModificationResult(false);
		for(String s: lines)
		{
		    if (s.indexOf(";") >= 0)
			return new ModificationResult(false);
		    if (s.indexOf("=") >= 0)
			return new ModificationResult(false);
		    if (s.indexOf("\'") >= 0)
			return new ModificationResult(false);
		    if (s.indexOf("\"") >= 0)
			return new ModificationResult(false);
		}
		return origModel.insertRegion(x, y, lines);
	    }
	    @Override public ModificationResult putChars(int pos, int lineIndex, String str)
	    {
		if (lineIndex >= getLineCount() - 3)
		    return new ModificationResult(false);
		return origModel.putChars(pos, lineIndex, str);
	    }
	    @Override public ModificationResult mergeLines(int firstLineIndex)
	    {
		final int count = getLineCount();
		if (count <= 4)
		    return new ModificationResult(false);
		if (firstLineIndex >= count - 3)
		    return new ModificationResult(false);
		if (firstLineIndex == count - 4 && !getLine(firstLineIndex).isEmpty())
		    return new ModificationResult(false);
		return origModel.mergeLines(firstLineIndex);
	    }
	    @Override public ModificationResult splitLine(int pos, int lineIndex)
	    {
		if (lineIndex >= getLineCount() - 3)
		    return new ModificationResult(false);
		return origModel.splitLine(pos, lineIndex);
	    }
	};
    }

        EditArea.Params createEditParams(EditArea.ChangeListener listener)
    {
	NullCheck.notNull(listener, "listener");
	final EditArea.Params params = new EditArea.Params();
	params.context = getControlContext();
	params.name = app.getStrings().appName();
	params.appearance = new EditUtils.DefaultEditAreaAppearance(params.context){
		@Override public void announceLine(int index, String line)
		{
		    NullCheck.notNull(line, "line");
		    NavigationArea.defaultLineAnnouncement(context, index, getLuwrain().getSpeakableText(line, Luwrain.SpeakableTextType.PROGRAMMING));
		}
	    };
	params.editFactory = (editParams)->{
	    editParams.model = createBlockingModel(editParams.model);
	    return new MultilineEdit(editParams);
	};
	params.changeListener = listener;
		return params;
    }


}
