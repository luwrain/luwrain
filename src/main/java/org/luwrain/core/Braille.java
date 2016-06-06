
package org.luwrain.core;

class Braille
{
    private Registry registry;

    void init(Registry registry)
    {
	NullCheck.notNull(registry, "registry");

    }

void textToSpeak(String text)
    {
	NullCheck.notNull(text, "text");
    }

    boolean isActive()
    {
	return true;
    }

    String getDriver()
    {
	return "laplala";
    }

    String getErrorMessage()
    {
	return "Problemka";
    }



}
