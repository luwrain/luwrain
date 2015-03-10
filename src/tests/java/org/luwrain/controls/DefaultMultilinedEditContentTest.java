
package org.luwrain.controls;

import org.junit.*;

public class DefaultMultilinedEditContentTest extends Assert
{
    @Test public void storing()
    {
	DefaultMultilinedEditContent content = new DefaultMultilinedEditContent();
	content.addLine("123");
	content.addLine("qwe");
	content.addLine("asd");
	assertTrue(content.getLineCount() == 3);
	assertTrue(content.getLine(0).equals("123"));
	assertTrue(content.getLine(1).equals("qwe"));
	assertTrue(content.getLine(2).equals("asd"));
    }

    @Test public void deletion()
    {
	DefaultMultilinedEditContent content = new DefaultMultilinedEditContent();
	content.addLine("123");
	content.addLine("qwe");
	content.addLine("asd");
	assertTrue(content.getLineCount() == 3);
	content.removeLine(1);
	assertTrue(content.getLineCount() == 2);
	assertTrue(content.getLine(0).equals("123"));
	assertTrue(content.getLine(1).equals("asd"));
    }

    @Test public void insertion()
    {
	DefaultMultilinedEditContent content = new DefaultMultilinedEditContent();
	content.addLine("123");
	content.addLine("qwe");
	content.addLine("asd");
	assertTrue(content.getLineCount() == 3);
	content.insertLine(2, "!@#");
	assertTrue(content.getLineCount() == 4);
	assertTrue(content.getLine(0).equals("123"));
	assertTrue(content.getLine(1).equals("qwe"));
	assertTrue(content.getLine(2).equals("!@#"));
	assertTrue(content.getLine(3).equals("asd"));
    }

    @Test public void changing()
    {
	DefaultMultilinedEditContent content = new DefaultMultilinedEditContent();
	content.addLine("123");
	content.addLine("qwe");
	content.addLine("asd");
	assertTrue(content.getLineCount() == 3);
	content.setLine(1, "!@#");
	assertTrue(content.getLineCount() == 3);
	assertTrue(content.getLine(0).equals("123"));
	assertTrue(content.getLine(1).equals("!@#"));
	assertTrue(content.getLine(2).equals("asd"));
    }
}
