
package org.luwrain.util;

public class StringIterator
{
    public class OutOfBoundsException extends Exception
    {
	public OutOfBoundsException(int pos, int len)
	{
	    super("" + pos + " >= " + len);
	}
    }

    private String text;
    private int pos;

    public StringIterator(String text)
    {
	this.text = text;
	this.pos = 0;
	if (text == null)
	    throw new NullPointerException("text may not be null");
    }

    public StringIterator(String text, int pos) throws OutOfBoundsException
    {
	this.text = text;
	this.pos = pos;
	if (text == null)
	    throw new NullPointerException("text may not be null");
	if (pos >= text.length())
	    throw new OutOfBoundsException(pos, text.length());
    }

    public int pos()
    {
	return pos;
    }

    public void skipBlank() throws OutOfBoundsException
    {
	checkBounds();
	while (pos < text.length() && blankChar(text.charAt(pos)))
	    ++pos;
    }

    public String getUntilBlankOr(String chars) throws OutOfBoundsException
    {
	checkBounds();
	StringBuilder b = new StringBuilder();
	while(pos < text.length() && !blankChar(text.charAt(pos)))
	{
	    final char c = text.charAt(pos);
	    for(int i = 0;i < chars.length();++i)
		if (chars.charAt(i) == c)
		    return b.toString();
	    b.append(c);
	    ++pos;
	}
	return b.toString();
    }

    public String getUntil(String chars) throws OutOfBoundsException
    {
	checkBounds();
	StringBuilder b = new StringBuilder();
	while(pos < text.length())
	{
	    final char c = text.charAt(pos);
	    for(int i = 0;i < chars.length();++i)
		if (chars.charAt(i) == c)
		    return b.toString();
	    b.append(c);
	    ++pos;
	}
	return b.toString();
    }

    public char currentChar() throws OutOfBoundsException
    {
	checkBounds();
	return text.charAt(pos);
    }

    public boolean isCurrentBlank() throws OutOfBoundsException
    {
	checkBounds();
	return blankChar(text.charAt(pos));
    }

    public boolean isStringHere(String s) throws OutOfBoundsException
    {
	checkBounds();
	if (s.length() > text.length() - pos)
	    return false;
	for(int i = 0;i < s.length();++i)
	    if (s.charAt(i) != text.charAt(pos + i))
		return false;
	return true;
    }

    public boolean isStringAfterBlank(String s) throws OutOfBoundsException
    {
	checkBounds();
	int p = pos;
	while (p < text.length() && blankChar(text.charAt(p)))
	    ++p;
	if (s.length() + p> text.length())
	    return false;
	for(int i = 0;i < s.length();++i)
	    if (s.charAt(i) != text.charAt(p + i))
		return false;
	return true;
    }

    public void moveNext() throws OutOfBoundsException
    {
	checkBounds();
	if (pos + 1 >= text.length())
	    throw new OutOfBoundsException(pos + 1, text.length());
	++pos;
    }

    private void checkBounds() throws OutOfBoundsException
    {
	if (pos >= text.length())
	    throw new OutOfBoundsException(pos, text.length());
    }

    static public boolean blankChar(char c)
    {
	return c == ' ' || c == '\t' || c == '\r' || c == '\n' || Character.isSpace(c);
    }
}
