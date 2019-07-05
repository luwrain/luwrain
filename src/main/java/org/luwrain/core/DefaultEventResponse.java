
//LWR_API 1.0

package org.luwrain.core;

public class DefaultEventResponse
{
static public EventResponse text(String text) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.Text(null, text);
    }

    /**
     * Speak a text with simultaneous sound.
     *
     * @param sound A sound, may be null (no sound needed)
     * @param text A text to say
     */
    static public EventResponse text(Sounds sound, String text) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.Text(sound, text);
    }

    static public EventResponse letter(char letter)
    {
	if (Character.isSpace(letter) || letter == 160)//with non-breaking space
	    return new EventResponses.Hint(Hint.SPACE);
	return new EventResponses.Letter(letter);
    }

        static public EventResponse hint(Hint hint) 
    {
	NullCheck.notNull(hint, "hint");
	return new EventResponses.Hint(hint);
    }

    static public EventResponse hint(Hint hint, String text) 
    {
	NullCheck.notNull(hint, "hint");
	NullCheck.notNull(text, "text");
	return new EventResponses.Hint(hint, text);
    }

    static public EventResponse listItem(String text) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.ListItem(null, text, null);
    }

    static public EventResponse listItem(String text, Suggestions suggestion) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.ListItem(null, text, suggestion);
    }

    static public EventResponse listItem(Sounds sound, String text, Suggestions suggestion) 
    {
	NullCheck.notNull(text, "text");
	return new EventResponses.ListItem(sound, text, suggestion);
    }

    static public EventResponse treeItem(EventResponses.TreeItem.Type type, String text, int level, Suggestions suggestion)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(text, "text");
	return new EventResponses.TreeItem(type, text, level, suggestion);
    }

        static public EventResponse treeItem(EventResponses.TreeItem.Type type, String text, int level)
    {
	NullCheck.notNull(type, "type");
	NullCheck.notNull(text, "text");
	return new EventResponses.TreeItem(type, text, level);
    }
}
