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

package org.luwrain.script;

import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.function.*;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.graalvm.polyglot.*;
import org.graalvm.polyglot.proxy.*;
import com.oracle.truffle.js.runtime.JSContextOptions;

public class AsyncFunctionTest
{
    private Context c = null;

    @Test void main()
    {
	final AtomicReference<CompletableFuture<Object>> f = new AtomicReference<>(null);
	final AtomicReference<String> finishedValue = new AtomicReference<>(null);
	c.getBindings("js").putMember("f", AsyncFunction.create(c, (args, res)->f.set(res)));
	c.getBindings("js").putMember("finished", (ProxyExecutable)(args)->{ finishedValue.set(args[0].asString()); return null; });
	c.getBindings("js").putMember("check", (ProxyExecutable)(args)->{ assertNull(f.get()); return null; });
	final Value fn = c.eval("js", "" +
				"(async function () {" +
				"check();" +
				"var foo = await f();" +
				"finished(foo);" +
				"})");
	fn.execute();
	assertNotNull(f.get());
	assertNull(finishedValue.get());
	f.get().complete("Testing value");
	assertEquals("Testing value", finishedValue.get());
    }

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
	asserTrue(res.hasMember("then"));
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
}
