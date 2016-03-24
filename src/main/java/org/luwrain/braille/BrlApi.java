
package org.luwrain.braille;

import java.util.concurrent.*;

import org.a11y.BrlAPI.*;
import org.luwrain.core.*;

public class BrlApi implements Constants 
{
    private final int STEP_DELAY = 1000;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private FutureTask task = null;
    private Brlapi brlApi = null;

    private Luwrain luwrain;

    public boolean connect(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
	ConnectionSettings settings = new ConnectionSettings();
	settings.host = "";
	try {
	    Log.debug("braille", "connecting to BrlAPI");
brlApi = new Brlapi(settings);
	    Log.debug("braille", "connected: fd=" + brlApi.getFileDescriptor());
	    Log.debug("braille", "using key file " + brlApi.getAuth());
	    Log.debug("braille", "driver is " + brlApi.getDriverName());
	    final DisplaySize size = brlApi.getDisplaySize();
	    Log.debug("braille", "display size is " + size.getWidth() + "x" + size.getHeight());
brlApi.enterTtyModeWithPath(new int[0]);
task = createTask();
executor.execute(task);
Log.debug("braille", "braile keys service started");
	    return true;
	}
	catch (UnsatisfiedLinkError | java.lang.Exception e)
	{
	    Log.error("braille", "unable to connect to brltty");
	    e.printStackTrace();
	    brlApi = null;
	    return false;
	}
    }

    synchronized public void writeText(String text)
    {
	NullCheck.notNull(text, "text");
	if (brlApi == null)
	    return;
	brlApi.writeText(text);
    }

    synchronized private void readKeys()
    {
	if (brlApi == null)
	    return;
	try {
	    final long key = brlApi.readKey(false);
	    if (key != -1)
	    {
		System.out.println("braille " + key);
		luwrain.runInMainThread(()->luwrain.message("" + key));
	    }
	}
	catch(java.lang.Exception e)
	{
	    e.printStackTrace();

	}
    }

    private FutureTask createTask()
    {
	return new FutureTask(()->{
	    while(!Thread.currentThread().isInterrupted())
	    {
		try {
		    Thread.sleep(STEP_DELAY);
		    readKeys();
		}
		catch(InterruptedException e)
		{
		    Thread.currentThread().interrupt();
		}
	    }
	}, null);
    }
}
