
package org.luwrain.core;

class Braille
{
    private Registry registry;
    private org.luwrain.os.Braille braille;
    private boolean active = false;
    private String errorMessage = "";

    void init(Registry registry, org.luwrain.os.Braille braille,
	      EventConsumer eventConsumer)
    {
	NullCheck.notNull(registry, "registry");
	NullCheck.notNull(eventConsumer, "eventConsumer");
	this.braille = braille;
	if (braille == null)
	{
	    active = false;
	    errorMessage = "No braille support in the operating system";
	    return;
	}
	final InitResult res = braille.init(eventConsumer);
	//	System.out.println("res " + res.type());
	if (res.success())
	{
	    //	    System.out.println("success");
	    active = true;
	    errorMessage = "";
	} else
	{
	    //	    System.out.println("failure");
	    active = false;
	    errorMessage = res.message();
	    //	    System.out.println(res.message());
	}
    }

    void textToSpeak(String text)
    {
	NullCheck.notNull(text, "text");
    }

    boolean isActive()
    {
	return active;
    }

    String getDriver()
    {
	return braille != null?braille.getDriverName():"";
    }

    String getErrorMessage()
    {
	return errorMessage;
    }
}
