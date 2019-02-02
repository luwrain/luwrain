
package org.luwrain.controls;

import java.util.*;

import org.luwrain.core.*;
import org.luwrain.util.*;

public final class EditUtils
{
        static public class DefaultCorrector implements MultilineEditCorrector
    {
	protected final MultilineEditCorrector wrappedCorrector;

	public DefaultCorrector(MultilineEditCorrector wrappedCorrector)
	{
	    NullCheck.notNull(wrappedCorrector, "wrappedCorrector");
	    this.wrappedCorrector = wrappedCorrector;
	}

    @Override public int getLineCount()
    {
	return wrappedCorrector.getLineCount();
    }

    @Override public String getLine(int index)
    {
	return wrappedCorrector.getLine(index);
    }

    @Override public int getHotPointX()
    {
	return wrappedCorrector.getHotPointX();
    }

    @Override public int getHotPointY()
    {
	return wrappedCorrector.getHotPointY();
    }

    @Override public String getTabSeq()
    {
	return wrappedCorrector.getTabSeq();
    }

    @Override public char deleteChar(int pos, int lineIndex)
    {
	return wrappedCorrector.deleteChar(pos, lineIndex);
    }

    @Override public boolean deleteRegion(int fromX, int fromY, int toX, int toY)
    {
	return wrappedCorrector.deleteRegion(fromX, fromY, toX, toY);
    }

    @Override public boolean insertRegion(int x, int y, String[] lines)
    {
	NullCheck.notNullItems(lines, "lines");
	return wrappedCorrector.insertRegion(x, y, lines);
    }

    @Override public boolean insertChars(int pos, int lineIndex, String str)
    {
	NullCheck.notNull(str, "str");
	return wrappedCorrector.insertChars(pos, lineIndex, str);
	}

    @Override public boolean mergeLines(int firstLineIndex)
    {
	    return wrappedCorrector.mergeLines(firstLineIndex);
    }

    @Override public String splitLine(int pos, int lineIndex)
    {
	return wrappedCorrector.splitLine(pos, lineIndex);
    }

    @Override public void doDirectAccessAction(DirectAccessAction action)
    {
	wrappedCorrector.doDirectAccessAction(action);
    }
}

    static public class WordWrapCorrector extends DefaultCorrector
    {
        static private final int ALIGNING_LINE_LEN = 60;

	public WordWrapCorrector(MultilineEditCorrector wrappedCorrector)
	{
	    super(wrappedCorrector);
	}

	    @Override public boolean insertChars(int pos, int lineIndex, String str)
    {
	NullCheck.notNull(str, "str");
	if (!wrappedCorrector.insertChars(pos, lineIndex, str))
	    return false;
	if (str.equals(" "))
	    alignParagraph(pos, lineIndex);
	return true;
    }


    protected void alignParagraph(int pos, int lineIndex)
    {
	doDirectAccessAction((lines,hotPoint)->{
		//Doing nothing on empty line
		if (lines.getLine(lineIndex).trim().isEmpty())
		    return;
		//Searching paragraph bounds
		int paraBegin = lineIndex;
		int paraEnd = lineIndex;
		while (paraBegin > 0 && !lines.getLine(paraBegin).trim().isEmpty())
	    	    --paraBegin;
		if (lines.getLine(paraBegin).trim().isEmpty())
		    ++paraBegin;
		while (paraEnd < lines.getLineCount() && !lines.getLine(paraEnd).trim().isEmpty())
		    ++paraEnd;
		//Looking for the first line where it's necessary to do correction from
		int startingLine = 0;
		for(startingLine = paraBegin;startingLine < paraEnd;++startingLine)
		    if (lines.getLine(startingLine).length() > ALIGNING_LINE_LEN)
			break;
		//Stopping, if there are no long lines at all
		if (startingLine == paraEnd)
		    return;
		doAligning(lines, hotPoint, startingLine, paraEnd);
	    });
    }

    //The fragment of lines between lineFrom and lineTo may not contain empty strings
    protected void doAligning(MutableLines lines, HotPointControl hotPoint, int lineFrom, int lineTo)
    {
	Log.debug("proba", "doAligning(" + lineFrom + "," + lineTo + ")");
	NullCheck.notNull(lines, "lines");
	NullCheck.notNull(hotPoint, "hotPoint");
	if (lineFrom < 0 || lineFrom >= lines.getLineCount())
	    throw new IllegalArgumentException("lineFrom (" + lineFrom + ") must be less than " + lines.getLineCount() + " and non-negative");
	if (lineTo < 0 || lineTo > lines.getLineCount())
	    throw new IllegalArgumentException("lineTo (" + lineTo + ") must be less than " + lines.getLineCount() + " and non-negative");
	if (lineFrom >= lineTo)
	    throw new IllegalArgumentException("lineFrom (" + lineFrom + ") must be less than lineTo (" + lineTo + ")");
	final int hotPointX = hotPoint.getHotPointX();
	final int hotPointY = hotPoint.getHotPointY();
	if (hotPointY < lineFrom || hotPointY >= lineTo)
	{
	    final TextAligning t = new TextAligning(ALIGNING_LINE_LEN);
	    t.origLines = new String[lineTo - lineFrom];
	    for(int i = lineFrom;i < lineTo;++i)
		t.origLines[i - lineFrom] = lines.getLine(i);
	    t.align();
	    //FIXME:do it more effectively
	    for(int i = lineFrom;i < lineTo;++i)
		lines.removeLine(lineFrom);
	    int k = 0;
	    for(String s: t.res)
		lines.insertLine(lineFrom + (k++), s);
	} else
	{
	    final TextAligning t = new TextAligning(ALIGNING_LINE_LEN);
	    t.origLines = new String[lineTo - lineFrom];
	    for(int i = lineFrom;i < lineTo;++i)
		t.origLines[i - lineFrom] = lines.getLine(i);
	    t.origHotPointX = hotPointX;
	    t.origHotPointY = hotPointY - lineFrom;
	    	    Log.debug("proba", "before " + t.origHotPointX + " " + t.origHotPointY);
		    //Taking care of positioning of the hot point outsite of line bounds, may be on one character on the right
	    final boolean hotPointXShifted;
	    if (t.origHotPointX >= t.origLines[t.origHotPointY].length())
	    {
		if (t.origLines[t.origHotPointY].isEmpty())
		    throw new IllegalArgumentException("lines array contains the empty string at position " + (t.origHotPointY + lineFrom));
		--t.origHotPointX;
		hotPointXShifted = true; 
	    } else
		hotPointXShifted = false;
	    //	    Log.debug("shifted=" + hotPointXShifted);
	    t.align();
	    Log.debug("proba", "after " + t.hotPointX + " " + t.hotPointY);
	    //FIXME:do it more effectively
	    for(int i = lineFrom;i < lineTo;++i)
		lines.removeLine(lineFrom);
	    int k = 0;
	    for(String s: t.res)
		lines.insertLine(lineFrom + (k++), s);
	    if (t.hotPointX >= 0 && t.hotPointY >= 0)
	    {
			    hotPoint.setHotPointY(t.hotPointY + lineFrom);
		hotPoint.setHotPointX(t.hotPointX + (hotPointXShifted?1:0));
	    }
	}
    }
}

class TextModel extends MultilineEditModelTranslator
{
    private final int maxLineLen = 60;

    TextModel(MutableLines lines, HotPointControl hotPoint)
    {
	super(lines, hotPoint);
    }

    @Override public  boolean insertChars(int pos , int lineIndex, String str)
    {
	if (!super.insertChars(pos, lineIndex, str))
	    return false;
	processLine(lineIndex);
	return true;
    }

    private void processLine(int index)
    {
	final String line = getLine(index);
	if (line == null || line.length() <= maxLineLen)
	    return;
	int pos = maxLineLen;
	while(pos >= 0 && !Character.isSpace(line.charAt(pos)))
	    --pos;
	if (pos < 0)
	{
	    pos = maxLineLen;
	    while (pos < line.length() && !Character.isSpace(line.charAt(pos)))
		++pos;
	    if (pos >= line.length())//There are no spaces in the line at all
		return;
	}
	if (pos + 1 >= line.length())
	    return;
	if (splitLine(pos, index) == null)
	    return;
	while(true)
	{
	final String newLine = getLine(index + 1);
	if (newLine.isEmpty() || !Character.isSpace(newLine.charAt(0)))
	    break;
	deleteChar(0, index + 1);
	}
	processLine(index + 1);

    }
}

    
}
