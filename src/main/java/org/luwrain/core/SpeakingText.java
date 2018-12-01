

package org.luwrain.core;

class SpeakingText
{
    static String  processText(String text)
    {
	NullCheck.notNull(text, "text");
	return text.replaceAll("\\h", " ");
    }
}
