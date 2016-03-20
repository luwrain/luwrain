
package org.luwrain.braille;

import org.a11y.BrlAPI.*;
import org.luwrain.core.*;

public class BrlApi implements Constants 
{
    private Brlapi brlApi = null;

    public boolean connect()
    {
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

    public void writeText(String text)
    {
	NullCheck.notNull(text, "text");
	if (brlApi == null)
	    return;
	brlApi.writeText(text);
    }
}
