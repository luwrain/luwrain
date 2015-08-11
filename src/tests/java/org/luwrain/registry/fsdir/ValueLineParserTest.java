
package org.luwrain.registry.fsdir;

import org.junit.*;

public class ValueLineParserTest extends Assert
{
    @Test public void valid()
    {
	final ValueLineParser parser = new ValueLineParser();

	assertTrue(parser.parse(""));
	assertTrue(parser.key.isEmpty());
	assertTrue(parser.value.isEmpty());

	assertTrue(parser.parse("\"aaa\"=\"\""));
	assertTrue(parser.key.equals("aaa"));
	assertTrue(parser.value.isEmpty());

	assertTrue(parser.parse("\"aaa\"=\"bbb\""));
	assertTrue(parser.key.equals("aaa"));
	assertTrue(parser.value.equals("bbb"));

	assertTrue(parser.parse("\"aaa\" = \"bbb\""));
	assertTrue(parser.key.equals("aaa"));
	assertTrue(parser.value.equals("bbb"));

	assertTrue(parser.parse("\" aaa \" = \" bbb \""));
	assertTrue(parser.key.equals(" aaa "));
	assertTrue(parser.value.equals(" bbb "));

	assertTrue(parser.parse("\" aaa \" = \" bbb \""));
	assertTrue(parser.key.equals(" aaa "));
	assertTrue(parser.value.equals(" bbb "));

	assertTrue(parser.parse("\" a\"\"a\"\"a \" = \" b\"\"b\"\"b \""));
	assertTrue(parser.key.equals(" a\"a\"a "));
	assertTrue(parser.value.equals(" b\"b\"b "));
    }
}
