
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
	final Settings.Braille settings = Settings.createBraille(registry);
	if (!settings.getEnabled(false))
	    return;
	final InitResult res = braille.init(eventConsumer);
	if (res.success())
	{
	    active = true;
	    errorMessage = "";
	} else
	{
	    active = false;
	    errorMessage = res.message();
	}
    }

    void textToSpeak(String text)
    {
	NullCheck.notNull(text, "text");
	if (braille == null)
	    return;
	braille.writeText(text);
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

    int getDisplayWidth()
    {
	return braille != null?braille.getDisplayWidth():0;
    }

    int getDisplayHeight()
    {
	return braille != null?braille.getDisplayHeight():0;
    }
}
