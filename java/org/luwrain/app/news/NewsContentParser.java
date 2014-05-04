
package org.luwrain.app.news;

import java.util.*;

class NewsContentParser
{
    private ArrayList<String> lines = new ArrayList<String>();
    private String line = "";
    private String text;
    private int pos = 0;

    public void parse(String str)
    {
	text = str;
	if (text == null || text.isEmpty())
	    return;
	pos = 0;
	while(pos < text.length())
	{
	    final char c = text.charAt(pos);
	    switch(c)
	    {
	    case '<':
		onTag();
		continue;
	    case ' ':
		onSpace();
		continue;
	    default:
		line += c;
		++pos;
	    };
	}
	if (!line.trim().isEmpty())
	    lines.add(line.trim());
    }

    public String[] getLines()
    {
	return lines != null?lines.toArray(new String[lines.size()]):new String[0];
    }

    private void onTag()
    {
	while(pos < text.length() && text.charAt(pos) != '>')
	    ++pos;
	++pos;
    }

    private void onSpace()
    {
	++pos;
	if (line.isEmpty() || line.charAt(line.length() - 1) == ' ')
	    return;
	if (line.length() > 50)
	{
	    lines.add(line.trim());
	    line = "";
	    return;
	}
	line += " ";
    }

}
