
package org.luwrain.controls;

import org.junit.*;

public class MultilineEditModelsTranslatorTest extends Assert
{
    private TestingMultilineEditLowLevelModel lowLevel;
    private MultilineEditModelsTranslator translator;

    @Before public void prepareLowLevelModel()
    {
	lowLevel = new TestingMultilineEditLowLevelModel();
	lowLevel.addLine("123");
	lowLevel.addLine("456");
	lowLevel.addLine("789");
	translator = new MultilineEditModelsTranslator(lowLevel);
    }

    @Test public void deleteCharMiddle()
    {
	for(int x = 0;x < 5;++x)
	    for(int y = 0;y < 5;++y)
	    {
		final String oldValue = lowLevel.getLine(1);
		lowLevel.setHotPointX(x);
		lowLevel.setHotPointY(y);
	assertTrue(translator.deleteChar(1, 1) == '5');
	assertTrue(lowLevel.getLineCount() == 3);
	assertTrue(lowLevel.getLine(1).equals("46"));
	assertTrue(lowLevel.getHotPointY() == y);
	if (y != 1)
	    assertTrue(lowLevel.getHotPointX() == x); else
	{
	    if (x > 1)
		assertTrue(lowLevel.getHotPointX() == x -1); else
		assertTrue(lowLevel.getHotPointX() == x);
	}
	lowLevel.setLine(1, oldValue);
	    }
    }

    @Test public void deleteCharBegin()
    {
	for(int x = 0;x < 5;++x)
	    for(int y = 0;y < 5;++y)
	    {
		final String oldValue = lowLevel.getLine(1);
		lowLevel.setHotPointX(x);
		lowLevel.setHotPointY(y);
	assertTrue(translator.deleteChar(0, 1) == '4');
	assertTrue(lowLevel.getLineCount() == 3);
	assertTrue(lowLevel.getLine(1).equals("56"));
	assertTrue(lowLevel.getHotPointY() == y);
	if (y != 1)
	    assertTrue(lowLevel.getHotPointX() == x); else
	{
	    if (x > 0)
		assertTrue(lowLevel.getHotPointX() == x -1); else
		assertTrue(lowLevel.getHotPointX() == 0);
	}
	lowLevel.setLine(1, oldValue);
	    }
    }

    @Test public void deleteCharEnd()
    {
	for(int x = 0;x < 5;++x)
	    for(int y = 0;y < 5;++y)
	    {
		final String oldValue = lowLevel.getLine(1);
		lowLevel.setHotPointX(x);
		lowLevel.setHotPointY(y);
	assertTrue(translator.deleteChar(2, 1) == '6');
	assertTrue(lowLevel.getLineCount() == 3);
	assertTrue(lowLevel.getLine(1).equals("45"));
	assertTrue(lowLevel.getHotPointY() == y);
	if (y != 1)
	    assertTrue(lowLevel.getHotPointX() == x); else
	{
	    if (x > 2)
		assertTrue(lowLevel.getHotPointX() == x -1); else
		assertTrue(lowLevel.getHotPointX() == x);
	}
	lowLevel.setLine(1, oldValue);
	    }
    }

    @Test public void deleteCharOut()
    {
	for(int x = 0;x < 5;++x)
	    for(int y = 0;y < 5;++y)
	    {
		final String oldValue = lowLevel.getLine(1);
		lowLevel.setHotPointX(x);
		lowLevel.setHotPointY(y);
	assertTrue(translator.deleteChar(3, 1) == '\0');
	assertTrue(lowLevel.getLineCount() == 3);
	assertTrue(lowLevel.getLine(1).equals("456"));
	assertTrue(lowLevel.getHotPointY() == y);
	if (y != 1)
	    assertTrue(lowLevel.getHotPointX() == x); else
	{
	    if (x > 3)
		assertTrue(lowLevel.getHotPointX() == x -1); else
		assertTrue(lowLevel.getHotPointX() == x);
	}
	lowLevel.setLine(1, oldValue);
	    }
    }

    @Test public void deleteRegionSingleLine()
    {
	for(int x = 0;x < 5;++x)
	    for(int y = 0;y < 5;++y)
	    {
		final String oldValue = lowLevel.getLine(1);
		lowLevel.setHotPointX(x);
		lowLevel.setHotPointY(y);
		assertTrue(translator.deleteRegion(1, 1, 3, 1));
	assertTrue(lowLevel.getLineCount() == 3);
	assertTrue(lowLevel.getLine(1).equals("4"));
	assertTrue(lowLevel.getHotPointY() == y);
	if (y != 1)
	    assertTrue(lowLevel.getHotPointX() == x); else
	{
	    if (x <= 0)
		assertTrue(lowLevel.getHotPointX() == x); else
		if (x <= 3)
		    assertTrue(lowLevel.getHotPointX() == 1); else
		    assertTrue(lowLevel.getHotPointX() == x - 2);
	}
	lowLevel.setLine(1, oldValue);
	    }
    }

    @Test public void deleteRegion()
    {
	for(int x = 0;x < 5;++x)
	    for(int y = 0;y < 5;++y)
	    {
		lowLevel.setHotPointX(x);
		lowLevel.setHotPointY(y);
		assertTrue(translator.deleteRegion(1, 0, 1, 2));
	assertTrue(lowLevel.getLineCount() == 1);
	assertTrue(lowLevel.getLine(0).equals("189"));
	if (y < 3)
	    assertTrue(lowLevel.getHotPointY() == 0); else
	    assertTrue(lowLevel.getHotPointY() == y - 2);

	if (y == 0 && x <= 1)
	    assertTrue(lowLevel.getHotPointX() == x); else
	    if ((y == 0 && x >= 2) ||
y == 1 ||
		(y == 2 && x <= 1))
		assertTrue(lowLevel.getHotPointX() == 1); else
		    assertTrue(lowLevel.getHotPointX() == x);
	lowLevel.clear();
	lowLevel.addLine("123");
	lowLevel.addLine("456");
	lowLevel.addLine("789");

	    }
    }


}
