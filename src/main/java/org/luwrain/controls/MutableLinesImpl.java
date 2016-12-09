
package org.luwrain.controls;

import java.util.Vector;

import org.luwrain.core.*;

public class MutableLinesImpl implements MutableLines
{
    protected final Vector<String> lines = new Vector<String>();

    public MutableLinesImpl()
    {
    }

    public MutableLinesImpl(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	this.lines.setSize(lines.length);
	for(int i = 0;i < lines.length;++i)
	    this.lines.set(i, lines[i]);
    }

    public MutableLinesImpl(String lines)
    {
	NullCheck.notNull(lines, "lines");
	if (lines.isEmpty())
	    return;
	final String[] l = lines.split("\n", -1);
	this.lines.setSize(l.length);
	for(int i = 0;i < l.length;++i)
	    this.lines.set(i, l[i]);
    }

    @Override public void beginLinesTrans()
    {
    }

    @Override public void endLinesTrans()
    {
    }

    @Override public int getLineCount()
    {
	return lines.size();
    }

    @Override public String getLine(int index)
    {
	if (index < 0 || index >= lines.size())
	    return "";
	return lines.get(index);
    }

    public void setLines(String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	this.lines.setSize(lines.length);
	for(int i = 0;i < lines.length;++i)
	    this.lines.set(i, lines[i]);
    }

    public String[] getLines()
    {
	return lines.toArray(new String[lines.size()]);
    }

    @Override public void setLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	while(lines.size() <= index)
	    lines.add("");
	lines.set(index, line);
    }

    @Override public void addLine(String line)
    {
	NullCheck.notNull(line, "line");
	lines.add(line);
    }

    //index is the position of newly inserted line
    @Override public void insertLine(int index, String line)
    {
	NullCheck.notNull(line, "line");
	if (index < lines.size())
	{
	    lines.insertElementAt(line, index);
	    return;
	}
	while(lines.size() < index)
	    lines.add("");
	lines.add(line);
    }

    @Override public void removeLine(int index)
    {
	if (index < 0 || index >= lines.size())
	    return;
	lines.remove(index);
    }

    public void clear()
    {
	lines.clear();
    }

    public String getWholeText()
    {
	if (lines.size() == 1)
	    return "";
	if (lines.size() == 1)
	    return lines.get(0);
	final StringBuilder res = new StringBuilder();
	res.append(lines.get(0));
	for(int i = 1;i < lines.size();++i)
	    res.append("\n" + lines.get(i));
	return new String(res);
    }
}
