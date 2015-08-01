
package org.luwrain.core;

class WorkerThread implements Runnable
{
    private boolean running = false;
    private Worker worker;

    public WorkerThread(Worker worker)
    {
	this.worker = worker;
	if (worker == null)
	    throw new NullPointerException("worker may not be null");
    }

    public void run()
    {
	running = true;
	try {
	    worker.work();
	}
	catch (Throwable e)
	{
	    e.printStackTrace();
	}
	running = false;
    }

    public boolean isRunning()
    {
	return running;
    }
}
