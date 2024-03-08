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

public class MultilineEditTranslatorTest
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

    @Disabled @Test public void emptyLinesSplitMerge()
    {
	final MutableLinesImpl lines = new MutableLinesImpl(new String[0]);
	final TestingHotPointControl hotPoint = new TestingHotPointControl();
	final MultilineEditTranslator translator = new MultilineEditTranslator(lines, hotPoint);
	assertTrue(translator.splitLine(0, 0).equals(""));
	assertTrue(lines.getLineCount() == 2);
	assertTrue(translator.getLineCount() == 2);
	assertTrue(translator.getLine(0).equals(""));
	assertTrue(translator.getLine(1).equals(""));
	assertTrue(hotPoint.getHotPointX() == 0);
	assertTrue(hotPoint.getHotPointY() == 1);
	translator.mergeLines(0);
	assertTrue(lines.getLineCount() == 0);
	assertTrue(translator.getLineCount() == 1);
	assertTrue(hotPoint.getHotPointX() == 0);
	assertTrue(hotPoint.getHotPointY() == 0);
    }

    @Disabled @Test public void deleteChar3x3()
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
			final MultilineEditTranslator translator = new MultilineEditTranslator(lines, hotPoint);
			final char res = translator.deleteChar(pos, lineIndex).getCharArg();
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

    @Disabled @Test public void linesMerge3x3()
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
		    final MultilineEditTranslator translator = new MultilineEditTranslator(lines, hotPoint);
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

    @Disabled @Test public void linesSplitting3x3()
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
			final String res = translator.splitLine(pos, lineIndex).getStringArg();
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
