/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.interaction;

import java.awt.*;
import java.awt.event.*;
import org.luwrain.core.Log;

public class MainFrame extends Frame
{
    private static final int MIN_TABLE_WIDTH = 32;
    private static final int MIN_TABLE_HEIGHT = 16;

    private Font font;
    private int marginLeft = 0, marginTop = 0, marginRight = 0, marginBottom = 0;
    private int tableWidth = 0;
    private int tableHeight = 0;
    private char[][] table;

    public MainFrame(String title)
    {
	super(title);
    }

    public void paint(Graphics g)
    {
	super.paint(g);
	Image image = createImage(getSize().width, getSize().height);
	Graphics ig = image.getGraphics();
	ig.setColor(new Color(0, 0, 0));
	ig.fillRect(0, 0, getSize().width - 1, getSize().height - 1);
	ig.setFont(font);
	ig.setColor(new Color(255, 255, 255));
	if (table == null)
	    return;
	int fontHeight = getFontHeight();
	char[] chars = new char[tableWidth];
	for(int i = 0;i < tableHeight;i++)
	{
	    for(int j = 0;j < tableWidth;j++)
		chars[j] = table[j][i];
	    ig.drawString(new String(chars), 0, i * fontHeight);
	}
	g.drawImage(image, 0, 0, new Color(1, 1, 1), this);
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
	table = new char[tableWidth][];
	for(int i = 0;i < tableWidth;i++)
	    table[i] = new char[tableHeight];
	for(int i = 0;i < tableWidth;i++)
	    for(int j = 0;j < tableHeight;j++)
		table[i][j] = ' ';
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
	FontMetrics m = getGraphics().getFontMetrics(font);
return m.stringWidth("a");
    }

	public int getFontHeight()
	{
	FontMetrics m = getGraphics().getFontMetrics(font);
	return m.getHeight();
	}

    public void setFont(Font font)
    {
	this.font = font;
    }

    public void setMargin(int marginLeft,
			  int marginTop,
			  int marginRight,
int marginBottom)
    {
	this.marginLeft = marginLeft;
	this.marginTop = marginTop;
	this.marginRight = marginRight;
	this.marginBottom = marginBottom;
    }
}
