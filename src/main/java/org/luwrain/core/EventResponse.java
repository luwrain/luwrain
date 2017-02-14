
package org.luwrain.core;

public interface EventResponse
{
    public enum Type {
	REGULAR,
	MESSAGE
    };

    public static final class Unit
    {
	public enum Type 
	{
	    CHAR,
	    TEXT,
	};

	private final Type type;
	private final char ch;
	private final String text;

	public Unit(String text)
	{
	    NullCheck.notNull(text, "text");
	    this.type = Type.TEXT;
	    this.text = text;
	    this.ch = '\0';
	}

	public Unit(char ch)
	{
	    this.type = Type.CHAR;
	    this.ch = ch;
	    this.text = null;
	}

	public Type getType()
	{
	    return type;
	}

	public char getChar()
	{
	    return ch;
	}

	public String getText()
	{
	    return text;
	}
    }

    public static final class Suggestion
    {
	public enum Type {
	    PREDEFINED,
	    TEXT,
	};

	private final Type type;
	private final Suggestions predefined;
	private final String text;

	public Suggestion(Suggestions predefined)
	{
	    NullCheck.notNull(predefined, "predefined");
	    this.type = Type.PREDEFINED;
	    this.predefined = predefined;
	    this.text = null;
	}

	public Suggestion(String text)
	{
	    NullCheck.notNull(text, "text");
	    this.type = Type.TEXT;
	    this.text = text;
	    this .predefined = null;
	}

	public Type getType()
	{
	    return type;
	}

	public Suggestions getPredefined()
	{
	    return predefined;
	}

	public String getText()
	{
	    return text;
	}
    }

    Type getResponseType();
    Unit getPrefix();
    Unit getResponseContent();
    Unit getPostfix();
    Suggestion getSuggestion();
}
