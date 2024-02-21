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
	//	final AtomicBoolean checkPoint1 = new AtomicBoolean(false);
	final AtomicReference<CompletableFuture<Object>> f = new AtomicReference<>(null);
		    c.getBindings("js").putMember("f", AsyncFunction.create(c, (args, res)->{
				f.set(res);
				/*
				new Thread(()->{
					//					try { Thread.sleep(5000); } catch(Exception ex) {}
					synchronized(checkPoint1) {
					    while (!checkPoint1.get())
						try { checkPoint1.wait(); } catch(Exception ex) { ex.printStackTrace(); }
					}
				System.out.println("Completing");
				res.complete("OK");
				}).start();
				*/
			    }));
            final Value fn = c.eval("js", "" +
                            "(async function () {" +
                            "  console.log('pausing...');" +
                            "  var foo = await f();" +
                            "  console.log('resumed with value ' + foo);" +
                            "})");
	    System.out.println("Executing");
            fn.execute();
	    System.out.println("Finished");
	    /*
	    	    					try { Thread.sleep(10000); } catch(Exception ex) {}
	    checkPoint1.set(true);
	    synchronized(checkPoint1) {
		checkPoint1.notify();
	    }
	    */
	    assertNotNull(f.get());
	    f.get().complete("Testing value");
	    System.out.println("Final");
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
