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

public class MultilineEditModelTranslatorTest extends Assert
{
    @Test public void emptyLines()
    {
	final MutableLinesImpl lines = new MutableLinesImpl(new String[0]);
	final TestingHotPointControl hotPoint = new TestingHotPointControl();
	final MultilineEditModelTranslator translator = new MultilineEditModelTranslator(lines, hotPoint);
	assertTrue(lines.getLineCount() == 0);
	assertTrue(translator.getLineCount() == 1);
	assertTrue(lines.getLine(0).equals(""));
	translator.insertChars(0, 0, "a");
	assertTrue(lines.getLineCount() == 1);
	assertTrue(lines.getLine(0).equals("a"));
	assertTrue(translator.getLineCount() == 1);
	assertTrue(translator.getLine(0).equals("a"));
	assertTrue(hotPoint.x == 1);
	assertTrue(hotPoint.y == 0);
	final char deleted = translator.deleteChar(0, 0);
	assertTrue(deleted == 'a');
	assertTrue(lines.getLineCount() == 0);
	assertTrue(translator.getLineCount() == 1);
	assertTrue(translator.getLine(0).equals(""));
	assertTrue(hotPoint.x == 0);
	assertTrue(hotPoint.y == 0);
    }

    @Test public void emptyLinesSplitMerge()
    {
	final MutableLinesImpl lines = new MutableLinesImpl(new String[0]);
	final TestingHotPointControl hotPoint = new TestingHotPointControl();
	final MultilineEditModelTranslator translator = new MultilineEditModelTranslator(lines, hotPoint);
	assertTrue(translator.splitLine(0, 0).equals(""));
	assertTrue(lines.getLineCount() == 2);
	assertTrue(translator.getLineCount() == 2);
	assertTrue(translator.getLine(0).equals(""));
	assertTrue(translator.getLine(1).equals(""));
	assertTrue(hotPoint.x == 0);
	assertTrue(hotPoint.y == 1);
	translator.mergeLines(0);
	assertTrue(lines.getLineCount() == 0);
	assertTrue(translator.getLineCount() == 1);
	assertTrue(hotPoint.x == 0);
	assertTrue(hotPoint.y == 0);
    }

    @Test public void deleteChar3x3()
    {
	final String[] initial = new String[]{"123", "456", "789"};
	for(int x = 0;x < 3;++x)
	    for(int y = 0;y < 3;++y)
		for(int lineIndex = 0;lineIndex < 3;++lineIndex)
		    for(int pos = 0;pos < 3;++pos)
		    {
			final MutableLinesImpl lines = new MutableLinesImpl(initial);
			final TestingHotPointControl hotPoint = new TestingHotPointControl();
			hotPoint.x = x;
			hotPoint.y = y;
			final MultilineEditModelTranslator translator = new MultilineEditModelTranslator(lines, hotPoint);
			final char res = translator.deleteChar(pos, lineIndex);
			assertTrue(res == initial[lineIndex].charAt(pos));
			assertTrue(lines.getLine(lineIndex).equals(initial[lineIndex].substring(0, pos) + initial[lineIndex].substring(pos + 1)));
			if (y == lineIndex)
			{
			    if (x <= pos)
				assertTrue(hotPoint.x == x); else
				assertTrue(hotPoint.x == x - 1);
			} else
			{
			    assertTrue(hotPoint.x == x);
			    assertTrue(hotPoint.y == y);
			}
		    }
    }

    @Test public void insertChar3x3()
    {
	final String[] initial = new String[]{"123", "456", "789"};
	for(int x = 0;x <= 3;++x)
	    for(int y = 0;y <= 3;++y)
		for(int lineIndex = 0;lineIndex < 3;++lineIndex)
		    for(int pos = 0;pos <= 3;++pos)
		    {
			final MutableLinesImpl lines = new MutableLinesImpl(initial);
			final TestingHotPointControl hotPoint = new TestingHotPointControl();
			hotPoint.x = x;
			hotPoint.y = y;
			final MultilineEditModelTranslator translator = new MultilineEditModelTranslator(lines, hotPoint);
			translator.insertChars(pos, lineIndex, " ");
			assertTrue(lines.getLine(lineIndex).equals(initial[lineIndex].substring(0, pos) + " " + initial[lineIndex].substring(pos)));
			if (y == lineIndex)
			{
			    if (x < pos)
				assertTrue(hotPoint.x == x); else
				assertTrue(hotPoint.x == x + 1);
			} else
			{
			    assertTrue(hotPoint.x == x);
			    assertTrue(hotPoint.y == y);
			}
		    }
    }

    @Test public void linesMerge3x3()
    {
	final String[] initial = new String[]{"123", "456", "789"};
	for(int x = 0;x <= 3;++x)
	    for(int y = 0;y <= 3;++y)
		for(int lineIndex = 0;lineIndex < 2;++lineIndex)
		{
		    final MutableLinesImpl lines = new MutableLinesImpl(initial);
		    final TestingHotPointControl hotPoint = new TestingHotPointControl();
		    hotPoint.x = x;
		    hotPoint.y = y;
		    final MultilineEditModelTranslator translator = new MultilineEditModelTranslator(lines, hotPoint);
		    translator.mergeLines(lineIndex);
		    assertTrue(lines.getLineCount() == 2);
		    assertTrue(lines.getLine(lineIndex).equals(initial[lineIndex] + initial[lineIndex + 1]));
		    if (y == lineIndex + 1)
		    {
			assertTrue(hotPoint.x == x + initial[lineIndex].length());
			assertTrue(hotPoint.y == lineIndex);
		    } else
			if (y <= lineIndex)
			{
			    assertTrue(hotPoint.x == x);
			    assertTrue(hotPoint.y == y);
			} else
			{
			    assertTrue(hotPoint.x == x);
			    assertTrue(hotPoint.y == y - 1);
			}
		}
    }

    @Test public void linesSplitting3x3()
    {
	final String[] initial = new String[]{"123", "456", "789"};
	for(int x = 0;x <= 3;++x)
	    for(int y = 0;y <= 3;++y)
		for(int lineIndex = 0;lineIndex < 3;++lineIndex)
		    for(int pos = 0;pos <= 3;++pos)
		    {
			final MutableLinesImpl lines = new MutableLinesImpl(initial);
			final TestingHotPointControl hotPoint = new TestingHotPointControl();
			hotPoint.x = x;
			hotPoint.y = y;
			final MultilineEditModelTranslator translator = new MultilineEditModelTranslator(lines, hotPoint);
			final String res = translator.splitLine(pos, lineIndex);
			assertNotNull(res);
			assertTrue(lines.getLineCount() == 4);
			assertTrue(lines.getLine(lineIndex).equals(initial[lineIndex].substring(0, pos)));
			assertTrue(lines.getLine(lineIndex + 1).equals(initial[lineIndex].substring(pos)));
			assertTrue(lines.getLine(lineIndex + 1).equals(res));
			if (y == lineIndex)
			{
			    if (x < pos)
			    {
				assertTrue(hotPoint.x == x);
				assertTrue(hotPoint.y == y);
			    }else
			    {
				assertTrue(hotPoint.x == x - pos);
				assertTrue(hotPoint.y == y + 1);
			    }
			} else
			{
			    assertTrue(hotPoint.x == x);
			    if (y > lineIndex)
				assertTrue(hotPoint.y == y + 1); else
				assertTrue(hotPoint.y == y);
			}
		    }
    }

    //FIXME:insertRegion
    //FIXME:deleteRegion
}
