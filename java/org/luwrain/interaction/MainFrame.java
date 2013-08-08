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

public class MainFrame extends Frame
{
    public Font font;
    private int tableWidth = 0;
    private int tableHeight = 0;
    private char[][] table;

    public MainFrame(String title, Font font)
    {
	super(title);
	this.font = font;
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

    public void initTable()
    {
	final int width = getSize().width / getFontWidth();
	final int height = getSize().height / getFontHeight();
	tableWidth = width >= 32?width:32;
	tableHeight = height >= 16?height:16;
	table = new char[tableWidth][];
	for(int i = 0;i < tableWidth;i++)
	    table[i] = new char[tableHeight];
	for(int i = 0;i < tableWidth;i++)
	    for(int j = 0;j < tableHeight;j++)
		table[i][j] = ' ';
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
}
