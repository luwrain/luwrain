
package org.luwrain.braille;

import org.a11y.BrlAPI.*;
import org.luwrain.core.*;

public class BrlApi implements Constants 
{
    public boolean connect()
    {
	ConnectionSettings settings = new ConnectionSettings();
	settings.host = "";
	try {
	    Log.debug("braille", "connecting to BrlAPI");
	    final Brlapi brlapi = new Brlapi(settings);
	    Log.debug("braille", "connected: fd=" + brlapi.getFileDescriptor());
	    Log.debug("braille", "using key file " + brlapi.getAuth());
	    Log.debug("braille", "driver is " + brlapi.getDriverName());
	    final DisplaySize size = brlapi.getDisplaySize();
	    Log.debug("braille", "display size is " + size.getWidth() + "x" + size.getHeight());
brlapi.enterTtyModeWithPath(new int[0]);
brlapi.writeText("aaaaa");
	    return true;
	}
	catch (org.a11y.BrlAPI.Error | UnsatisfiedLinkError e)
	{
	    Log.error("braille", "unable to connect to brltty");
	    e.printStackTrace();
	    return false;
	}
    }
}
    
