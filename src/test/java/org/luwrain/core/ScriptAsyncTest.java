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

package org.luwrain.core;

import java.util.concurrent.atomic.*;
import java.util.function.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;
import com.oracle.truffle.js.runtime.JSContextOptions;

public class ScriptAsyncTest
{
    private Context c = null;

    @Test void simpleAsyncFunc()
    {
	Value res = c.eval("js", "(async function(){ return \"proba\";})");
	assertNotNull(res);
	res = res.execute();
	assertNotNull(res);
	assertTrue(res.hasMember("then"));
	var called = new AtomicBoolean(false);
	res = res.invokeMember("then", (Consumer<Object>)(r)->{
		assertNotNull(r);
		assertEquals("proba", r.toString());
		called.set(true);
	    });
	assertTrue(called.get());
	assertNotNull(res);
	assertTrue(res.hasMember("then"));
    }

    @BeforeEach void init()
    {
    	c = Context.newBuilder()
	.allowExperimentalOptions(true)
	.allowHostAccess(HostAccess.ALL)
	.option(JSContextOptions.CONSOLE_NAME, "true")
	.option(JSContextOptions.INTEROP_COMPLETE_PROMISES_NAME, "false")
	.build();
    }

    @AfterEach void close()
    {
	c.close();
	c = null;
    }

    public interface Thenable {
        void then(Value onResolve, Value onReject);
    }
}
