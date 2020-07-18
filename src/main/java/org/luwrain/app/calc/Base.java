/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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
import java.net.*;
import javax.script.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.controls.MultilineEdit.ModificationResult;

final class Base
{
    static private final String RESOURCE_PATH = "org/luwrain/app/calc/prescript.js";

    final Luwrain luwrain;
    final Strings strings;
    final ScriptEngine engine;

    Base(Luwrain luwrain, Strings strings)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(strings, "strings");
	this.luwrain = luwrain;
	this.strings = strings;
	final ScriptEngineManager manager = new ScriptEngineManager();
	this.engine = manager.getEngineByName("nashorn");
    }

    Number calculate(String[] expr) throws Exception
    {
	NullCheck.notNullItems(expr, "expr");
	final StringBuilder text = new StringBuilder();
	for(String s: expr)
	{
	    final String str = s.replaceAll("//", "#");
	    final int pos = str.indexOf("#");
	    if (pos < 0)
		text.append(str + " "); else
		text.append(str.substring(0, pos) + " ");
	}
	final String prescript = readPrescript();
	final Object res = engine.eval(prescript + new String(text) + ";");
	if (res != null && res instanceof Number)
	    return (Number)res;
	return null;
    }

    EditArea.Params createEditParams(EditArea.ChangeListener listener)
    {
	NullCheck.notNull(listener, "listener");
	final EditArea.Params params = new EditArea.Params();
	params.context = new DefaultControlContext(luwrain);
	params.name = strings.appName();
	params.appearance = new EditUtils.DefaultEditAreaAppearance(params.context){
		@Override public void announceLine(int index, String line)
		{
		    NullCheck.notNull(line, "line");
		    NavigationArea.defaultLineAnnouncement(context, index, luwrain.getSpeakableText(line, Luwrain.SpeakableTextType.PROGRAMMING));
		}
	    };
	params.editFactory = (editParams)->{
	    editParams.model = createBlockingModel(editParams.model);
	    return new MultilineEdit(editParams);
	};
	params.changeListener = listener;
		return params;
    }

    private String readPrescript()
    {
	final StringBuilder b = new StringBuilder();
	final URL url = this.getClass().getClassLoader().getResource(RESOURCE_PATH);
	try {
	    final InputStream is = url.openStream();
	    try {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = reader.readLine();
		while (line != null)
		{
		    b.append(line + "\n");
		    line = reader.readLine();
		}
		return new String(b);
	    }
	    finally {
		is.close();
	    }
	}
	catch(IOException e)
	{
	    luwrain.crash(e);
	    return "";
	}
    }

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
}
