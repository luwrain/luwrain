
package org.luwrain.registry;

import java.util.*;

public class PathParser
{
    public static Path parse(String str)
    {
	if (str == null)
	    throw new NullPointerException("str may not be null");
	if (str.isEmpty())
	    throw new IllegalArgumentException();
	ArrayList<String> items = new ArrayList<String>();
	String current = "";
	for(int i = 0;i < str.length();++i)
	{
	    final char c = str.charAt(i);
	    if (c == '/')
	    {
		if (!current.isEmpty())
		{
		    items.add(current);
		    current = "";
		}
		continue;
	    }
	    current += c;
	}
	if (items.isEmpty() && current.isEmpty())
	    return null;
	return new Path(str.charAt(0) == '/', items.toArray(new String[items.size()]), current);
    }

    public static Path parseAsDirectory(String str)
    {
	if (str == null)
	    throw new NullPointerException("str may not be null");
	if (str.isEmpty())
	    throw new IllegalArgumentException();
	ArrayList<String> items = new ArrayList<String>();
	String current = "";
	for(int i = 0;i < str.length();++i)
	{
	    final char c = str.charAt(i);
	    if (c == '/')
	    {
		if (!current.isEmpty())
		{
		    items.add(current);
		    current = "";
		}
		continue;
	    }
	    current += c;
	}
	if (!current.isEmpty())
	    items.add(current);
	if (items.isEmpty() && str.charAt(0) != '/')
	    return null;
	return new Path(str.charAt(0) == '/', items.toArray(new String[items.size()]), "");
    }
}
