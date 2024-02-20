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

package org.luwrain.registry.fsdir;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ValueLineParserTest
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
