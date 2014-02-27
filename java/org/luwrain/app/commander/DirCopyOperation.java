
package org.luwrain.app.commander;

import java.io.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

class DirCopyOperation implements Runnable
{
    private Area area;
    private Task task;

    private File[]  filesToCopy;
    private File copyTo;

    private long bytesTotal = -1;
    private long bytesCopied = 0;
    private int lastPercent = 0;

    public DirCopyOperation(Area area,
			    Task task,
			    File[] filesToCopy,
			    File copyTo)
    {
	this.area = area;
	this.task = task;
	this.filesToCopy = filesToCopy;
	this.copyTo = copyTo;
    }

    public void run()
    {
	bytesCopied = 0;
	lastPercent = 0;
	bytesTotal = getSize();
	if (bytesTotal < 0)
	{
	    onFailed();
	    return;
	}
	if (bytesTotal == 0)
	{
	    onDone();
	    return;
	}
	try {
	    for(File f: filesToCopy)
		copyImpl(f, new File(copyTo, f.getName()));
	} 
	catch (IOException e)
	{
	    Log.error("commander", "copy problem:" + e.getMessage());
	    e.printStackTrace();
	    onFailed();
	    return;
	}
	onDone();
    }

    private long getSize()
    {
	try {
	    long res = 0;
	    if (filesToCopy != null)
		for(File f: filesToCopy)
		    res += getSizeImpl(f);
	    return res;
	}
	catch(IOException e)
	{
	    Log.error("commander", "collecting size:" + e.getMessage());
	    e.printStackTrace();
	    return -1;
	}
    }

    private long getSizeImpl(File file) throws IOException
    {
	if (file == null)
	    return 0;
	if (!file.isDirectory())
	    return file.length();
	long res = 0;
	File[] items = file.listFiles();
	if (items != null)
	    for(File f: items)
		res += getSizeImpl(f);
	return res;
    }

    private void copyImpl(File srcPath, File destPath) throws IOException
    {
	if (!srcPath.isDirectory())
	{
	    InputStream in = new FileInputStream(srcPath);
	    OutputStream out = new FileOutputStream(destPath);
	    byte[] buf = new byte[1024];
	    int length;
	    while ((length = in.read(buf)) > 0) 
	    {
		onNewData(length);
		out.write(buf, 0, length);
	    }
	    in.close();
	    out.close();
	    return;
	} 
	if (!destPath.exists())
	    destPath.mkdir();//FIXME:With all parent directories;
	String folder_contents[] = srcPath.list();
	for (String file : folder_contents) 
	{
	    File srcFile = new File(srcPath, file);
	    File destFile = new File(destPath, file);
	    copyImpl(srcFile, destFile);
	}
    }

    private void onDone()
    {
	Luwrain.enqueueEvent(new TaskStatusUpdateEvent(area, task, Task.DONE, 0));
    }

    private void onFailed()
    {
	Luwrain.enqueueEvent(new TaskStatusUpdateEvent(area, task, Task.FAILED, 0));
    }

    private void onNewData(int bytes)
    {
	bytesCopied += bytes;
	int percent = (int)((bytesCopied * 100) / bytesTotal); 
	if (percent == lastPercent)
	    return;
	lastPercent = percent;
	Luwrain.enqueueEvent(new TaskStatusUpdateEvent(area, task, Task.LAUNCHED, percent));
    }
}
