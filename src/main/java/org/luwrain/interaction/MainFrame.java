/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

//FIXME:It is good idea to exchange rows and columns in the table as it can make table drawing more efficient;

package org.luwrain.interaction;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import org.luwrain.core.Log;


class MainFrame extends Frame
{
    private static final int MIN_TABLE_WIDTH = 16;
    private static final int MIN_TABLE_HEIGHT = 8;

    private Font font;
    private int marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;
    private int hotPointX = -1, hotPointY = -1;
    private Color fontColor = new Color(255, 255, 255);
    private Color bkgColor = new Color(0, 0, 0);
    private Color splitterColor = new Color(128, 128, 128);

    private int tableWidth = 0;
    private int tableHeight = 0;
    private char[][] table;
    private OnScreenLineTracker[] vertLines;
    private OnScreenLineTracker[] horizLines;

    public MainFrame(String title)
    {
	super(title);
    }

    public void paint(Graphics g)
    {
	final int fontHeight = getFontHeight();
	final int fontWidth = getFontWidth();
	super.paint(g);
	if (table == null)
	    return;
	Image image = createImage(getSize().width, getSize().height);
	Graphics2D ig = (Graphics2D)image.getGraphics();
	ig.setColor(bkgColor);
	ig.fillRect(0, 0, getSize().width - 1, getSize().height - 1);
	ig.setFont(font);
	ig.setColor(fontColor);
	char[] chars = new char[tableWidth];
	LineMetrics metrics = font.getLineMetrics("a", ig.getFontRenderContext());
	final int baseLine = (int)metrics.getHeight(); 
	for(int i = 0;i < tableHeight;i++)
	{
	    for(int j = 0;j < tableWidth;j++)
		chars[j] = table[j][i];
	    ig.drawString(new String(chars), 
			  marginLeft, (i * fontHeight) + baseLine + marginTop);
	}

	ig.setColor(splitterColor);
	//Vertical lines;
	if (vertLines != null)
	    for(int i = 0;i < vertLines.length;i++)
		if (vertLines[i] != null)
		{
		    OnScreenLine[] lines = vertLines[i].getLines();
		    for(int k = 0;k < lines.length;k++)
		    {
			ig.fillRect(marginLeft + (i * fontWidth) + (fontWidth / 2) - (fontWidth / 6),
				    marginTop + (lines[k].pos1 * fontHeight),
				    (fontWidth / 3),
				    (lines[k].pos2 - lines[k].pos1 + 1) * fontHeight);
		    }
		}

	//Horizontal lines;
	if (horizLines != null)
	    for(int i = 0;i < horizLines.length;i++)
		if (horizLines[i] != null)
		{
		    OnScreenLine[] lines = horizLines[i].getLines();
		    for(int k = 0;k < lines.length;k++)
		    {
			ig.fillRect(marginLeft + (lines[k].pos1 * fontWidth),
				    marginTop + (i * fontHeight) + (fontHeight / 2) - (fontWidth / 6),
				    (lines[k].pos2 - lines[k].pos1 + 1) * fontWidth,
				    fontWidth / 3);
		    }
		}

	//Hot point;
	if (hotPointX >= 0 && hotPointY >= 0 &&
	    hotPointX < tableWidth && hotPointY < tableHeight)
	{
	    ig.setColor(fontColor);
	    ig.fillRect((hotPointX * fontWidth) + marginLeft,
			(hotPointY * fontHeight) + marginTop, 
			fontWidth, fontHeight);
	    ig.setColor(bkgColor);
	    String str = new String();
	    str += table[hotPointX][hotPointY];
	    ig.drawString(str,
			  (hotPointX * fontWidth) + marginLeft,
			  (hotPointY  * fontHeight) + baseLine + marginTop);
	}



	g.drawImage(image, 0, 0, new Color(0, 0, 0), this);
    }

    public void putString(int x, int y, String text)
    {
	if (table == null || x >= tableWidth || y >= tableHeight ||
	    x >= table.length || y >= table[x].length)
	    return;
	if (text == null)
	    return;
	final int bound = x + text.length() <= tableWidth?text.length():tableWidth - x;  
	for(int i = 0;i < bound;i++)
	    table[x + i][y] = text.charAt(i) != '\0'?text.charAt(i):' ';
    }

    public void clearRect(int left,
			  int top,
			  int right,
			  int bottom)
    {
	if (table == null || tableWidth <= 0 || tableHeight <= 0)
	    return;
	final int l = left >= 0?left:0;
	final int t = top >= 0?top:0;
	final int r = right < tableWidth?right:(tableWidth - 1);
	final int b = bottom < tableHeight?bottom:(tableHeight - 1);
	if (l > r || t > b)
	    return;
	for(int i = l;i <= r;i++)
	    for(int j = t;j <= b;j++)
		table[i][j] = ' ';
	if (vertLines != null)
	    for(int i = l;i <= r;i++)
		vertLines[i].uncover(t, b);
	if (horizLines != null)
	    for(int i = t;i <= b;i++)
		horizLines[i].uncover(l, r);
    }

    public void setHotPoint(int x, int y)
    {
	//	Log.debug("awt", "new hot point: (" + x + "," + y + ")");
	if (x < 0 || y < 0)
	{
	    hotPointX = -1;
	    hotPointY = -1;
	    return;
	}
	hotPointX = x;
	hotPointY = y;
    }

    public boolean initTable()
    {
	int width = getSize().width;
	int height = getSize().height;
	if (width < marginLeft + marginRight)
	{
	    Log.error("awt", "table initialization failure: left + right margins are greater than window width (" + marginLeft + "+" + marginRight + "<" + width + ")");
	    return false;
	}
	if (height < marginTop + marginBottom)
	{
	    Log.error("awt", "table initialization failure: top + bottom margins are greater than window height (" + marginTop + "+" + marginBottom + "<" + height + ")");
	    return false;
	}
	width -= (marginLeft + marginRight);
	height -= (marginTop + marginBottom);
width /= getFontWidth();
height /= getFontHeight();
	if (width < MIN_TABLE_WIDTH || height < MIN_TABLE_HEIGHT)
	{
	    Log.error("awt", "too small table for initialization:" + width + "x" + height);
	    return false;
	}
	tableWidth = width;
	tableHeight = height;
	table = new char[tableWidth][];
	for(int i = 0;i < tableWidth;i++)
	    table[i] = new char[tableHeight];
	for(int i = 0;i < tableWidth;i++)
	    for(int j = 0;j < tableHeight;j++)
		table[i][j] = ' ';
	vertLines = new OnScreenLineTracker[tableWidth];
	for(int i = 0;i < tableWidth;i++)
	    vertLines[i] = new OnScreenLineTracker();
	horizLines = new OnScreenLineTracker[tableHeight];
	for(int i = 0;i < tableHeight;i++)
	    horizLines[i] = new OnScreenLineTracker();
	    Log.info("awt", "table is initialized with size " + width + "x" + height);
	return true;
    }

    public int getTableWidth()
    {
	return tableWidth;
    }

    public int getTableHeight()
    {
	return tableHeight;
    }

    public int getFontWidth()
    {
	if (font == null)
	{
	    Log.error("awt", "trying to calculate font width but font is not created");
	    return 0;
	}
	FontMetrics m = getGraphics().getFontMetrics(font);
	final int aWidth = m.stringWidth("A");
	final int spaceWidth = m.stringWidth(" ");
	final int oneWidth = m.stringWidth("1");
	//	Log.debug("awt", "\'A\' character has width " + aWidth);
	//	Log.debug("awt", "\' \' character has width " + spaceWidth);
	//	Log.debug("awt", "\'1\' character has width " + oneWidth);
	if (aWidth != spaceWidth || spaceWidth != oneWidth)
	    Log.warning("awt", "characters \'A\', \' \' and \'1\' have different width: " + aWidth + "," + spaceWidth + " and " + oneWidth);
	return aWidth;
    }

	public int getFontHeight()
	{
	if (font == null)
	{
	    Log.error("awt", "trying to calculate font height but font is not created");
	    return 0;
	}

	FontMetrics m = getGraphics().getFontMetrics(font);
	return m.getHeight();
	}

    public void setInteractionFont(Font font)
    {
	this.font = font;
Log.info("awt", "actual family of the  chosen font:" + this.font.getFamily());
Log.info("awt", "actual size of the chosen font:" + this.font.getSize());
    }

    public void setMargin(int marginLeft,
			  int marginTop,
			  int marginRight,
			  int marginBottom)
    {
	//FIXME:May not be negative;
	this.marginLeft = marginLeft;
	this.marginTop = marginTop;
	this.marginRight = marginRight;
	this.marginBottom = marginBottom;
    }

    public void setColors(Color fontColor,
			  Color bkgColor,
			  Color splitterColor)
    {
	if (fontColor == null || bkgColor == null || splitterColor == null)
	    return;
	this.fontColor = fontColor;
	this.bkgColor = bkgColor;
	this.splitterColor = splitterColor;
    }

    public void drawVerticalLine(int top,
				 int bottom,
				 int x)
    {
	if (vertLines == null)
	    return;
	if (x >= vertLines.length)
	{
	    Log.warning("awt", "unable to draw vertical line at column " + x + ", max vertical line is allowed at " + (vertLines.length - 1));
	    return;
	}
	if (vertLines[x] != null)
	    vertLines[x].cover(top, bottom);
    }

    public void drawHorizontalLine(int left,
				   int right,
				   int y)
    {
	if (horizLines == null)
	    return;
	if (y >= horizLines.length)
	{
	    Log.warning("awt", "unable to draw horizontal line at row " + y + ", max horizontal line is allowed at " + (horizLines.length - 1));
	    return;
	}
	if (horizLines[y] != null)
	    horizLines[y].cover(left, right);
    }
}
