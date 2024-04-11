/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.controls.edit;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.luwrain.core.*;
import org.luwrain.controls.edit.MultilineEdit.ModificationResult;
import org.luwrain.controls.*;

import org.luwrain.controls.edit.MultilineCorrector.*;

public class MultilineTranslatorTest
{
    @Disabled @Test public void emptyLines()
    {
	final MutableLinesImpl lines = new MutableLinesImpl(new String[0]);
	final TestingHotPointControl hotPoint = new TestingHotPointControl();
	final MultilineEditTranslator translator = new MultilineEditTranslator(lines, hotPoint);
	assertTrue(lines.getLineCount() == 0);
	assertTrue(translator.getLineCount() == 1);
	assertTrue(lines.getLine(0).equals(""));
	translator.putChars(0, 0, "a");
	assertTrue(lines.getLineCount() == 1);
	assertTrue(lines.getLine(0).equals("a"));
	assertTrue(translator.getLineCount() == 1);
	assertTrue(translator.getLine(0).equals("a"));
	assertTrue(hotPoint.getHotPointX() == 1);
	assertTrue(hotPoint.getHotPointY() == 0);
	final ModificationResult res = translator.deleteChar(0, 0);
	final char deleted = res.getCharArg();
	assertTrue(deleted == 'a');
	assertTrue(lines.getLineCount() == 0);
	assertTrue(translator.getLineCount() == 1);
	assertTrue(translator.getLine(0).equals(""));
	assertTrue(hotPoint.getHotPointX() == 0);
	assertTrue(hotPoint.getHotPointY() == 0);
    }

    @Test public void emptyLinesSplitMerge()
    {
	final var lines = new MutableLinesImpl(new String[0]);
	final var hotPoint = new TestingHotPointControl();
	final var translator = new MultilineTranslator(lines, hotPoint);
	Change c = new SplitLineChange(0, 0);
	translator.change(c);
	assertNotNull(c.getResult());
	assertEquals("", c.getResult().getStringArg());
	assertEquals(2, lines.getLineCount());
	assertTrue(lines.getLine(0).isEmpty());
	assertTrue(translator.getLine(1).isEmpty());
	assertEquals(0, hotPoint.getHotPointX());
	assertEquals(1, hotPoint.getHotPointY());
	c = new MergeLinesChange(0);
	translator.change(c);
	assertEquals(0, lines.getLineCount());
	assertEquals(0, hotPoint.getHotPointX());
	assertEquals(0, hotPoint.getHotPointY());
    }

    @Test public void deleteChar3x3()
    {
	final var initial = new String[]{"123", "456", "789"};
	for(int x = 0;x < 3;++x)
	    for(int y = 0;y < 3;++y)
		for(int lineIndex = 0;lineIndex < 3;++lineIndex)
		    for(int pos = 0;pos < 3;++pos)
		    {
			final var lines = new MutableLinesImpl(initial);
			final var hotPoint = new TestingHotPointControl();
			hotPoint.x = x;
			hotPoint.y = y;
			final var translator = new MultilineTranslator(lines, hotPoint, false);
			final var c = new DeleteCharChange(lineIndex, pos);
			translator.change(c);
			assertNotNull(c.getResult());
			assertEquals(initial[lineIndex].charAt(pos), c.getResult().getCharArg());
			assertEquals(initial[lineIndex].substring(0, pos) + initial[lineIndex].substring(pos + 1), lines.getLine(lineIndex));
			if (y == lineIndex)
			{
			    if (x <= pos)
				assertEquals(x, hotPoint.x); else
				assertEquals(x - 1, hotPoint.x);
			} else
			{
			    assertEquals(x, hotPoint.x);
			    assertEquals(y, hotPoint.y);
			}
		    }
    }

    @Disabled @Test public void insertChar3x3()
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
			final MultilineEditTranslator translator = new MultilineEditTranslator(lines, hotPoint);
			translator.putChars(pos, lineIndex, " ");
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
	final var initial = new String[]{"123", "456", "789"};
	for(int x = 0;x < 10;++x)
	    for(int y = 0;y < 10;++y)
		for(int lineIndex = 0;lineIndex < 2;++lineIndex)
		{
		    final var lines = new MutableLinesImpl(initial);
		    final TestingHotPointControl hotPoint = new TestingHotPointControl();
		    hotPoint.x = x;
		    hotPoint.y = y;
		    final var translator = new MultilineTranslator(lines, hotPoint, false);
		    translator.change(new MergeLinesChange(lineIndex));
		    assertEquals(2, lines.getLineCount());
		    assertEquals(initial[lineIndex] + initial[lineIndex + 1], lines.getLine(lineIndex));
		    //If hot point on the second of two merged lines
		    if (y == lineIndex + 1)
		    {
			assertEquals(x + initial[lineIndex].length(), hotPoint.x);
			assertEquals(lineIndex, hotPoint.y);
		    } else
			if (y <= lineIndex)
			{
			    //Hot point left unchanged
			    assertEquals(x, hotPoint.x);
			    assertEquals(y, hotPoint.y);
			} else
			{
			    assertEquals(x, hotPoint.x);
			    assertEquals(y - 1, hotPoint.y);
			}
		}
    }

    @Test public void linesSplitting3x3()
    {
	final var initial = new String[]{"123", "456", "789"};
	for(int x = 0;x < 10;++x)
	    for(int y = 0;y < 10;++y)
		for(int lineIndex = 0;lineIndex < 3;++lineIndex)
		    for(int pos = 0;pos <= 3;++pos)
		    {
			final var lines = new MutableLinesImpl(initial);
			final var hotPoint = new TestingHotPointControl();
			hotPoint.x = x;
			hotPoint.y = y;
			final var translator = new MultilineTranslator(lines, hotPoint, false);
			final var c = new SplitLineChange(lineIndex, pos);
			translator.change(c);
			assertNotNull(c.getResult());
			final String res = c.getResult().getStringArg();
			assertNotNull(res);
			assertEquals(4, lines.getLineCount());
			assertEquals(initial[lineIndex].substring(0, pos), lines.getLine(lineIndex));
			assertEquals(initial[lineIndex].substring(pos), lines.getLine(lineIndex + 1));
			assertEquals(res, lines.getLine(lineIndex + 1));
			if (y == lineIndex)
			{
			    if (x < pos)
			    {
				assertEquals(x, hotPoint.x);
				assertEquals(y, hotPoint.y);
			    }else
			    {
				assertEquals(x - pos, hotPoint.x);
				assertEquals(y + 1, hotPoint.y);
			    }
			} else
			{
			    assertEquals(x, hotPoint.x);
			    if (y > lineIndex)
				assertEquals(y + 1, hotPoint.y); else
				assertEquals(y, hotPoint.y);
			}
		    }
    }

    //FIXME:insertRegion
    //FIXME:deleteRegion
}
