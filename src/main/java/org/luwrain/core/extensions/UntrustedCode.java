
package org.luwrain.core.extensions;

import org.luwrain.core.*;

public class UntrustedCode
{
    private final Object syncObj = new Object();
    private volatile boolean done = false;
    private Object res = null;
    private final Runnable runnable;

    public UntrustedCode(Runnable runnable)
    {
	NullCheck.notNull(runnable, "runnable");
	this.runnable = runnable;
    }

    void run()
    {
	if (runnable != null)
	    runnable.run();
	    done = true;
	synchronized (syncObj) 
	{
	    syncObj.notifyAll();
	}
    }

    public final void waitUntilDone() throws InterruptedException
    {
	if (done)
	    return;
	synchronized (syncObj) {
	    while (!done)
		syncObj.wait();
	}
    }

    public Object getResult() throws InterruptedException
    {
	waitUntilDone();
	return res;
    }




}
