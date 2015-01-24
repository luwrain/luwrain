/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.controls;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class CalendarArea implements Area
{
    private ControlEnvironment environment;
    private Calendar calendar;
    private int[][] table;
    private int tableX = 0;
    private int tableY = 0;

    public CalendarArea(ControlEnvironment environment, Calendar calendar)
    {
	this.environment = environment;
	this.calendar = calendar;
	refresh();
    }

    public int getHotPointX()
    {
	return tableX * 4;
    }

    public int getHotPointY()
    {
	return tableY + 1;
    }

    public int getLineCount()
    {
	return 1 + (table != null?table.length:0);
    }

    public String getLine(int index)
    {
	if (index == 0)
	    return "Mon Tue Wen Thu Fri Sat Sun";//FIXME:
	if (table == null || index - 1 >= table.length)
	    return "";
	return constructLine(index - 1);
    }

    public boolean onKeyboardEvent(KeyboardEvent event)
    {
	if (!event.isCommand() || event.isModified())
	    return false;
	switch(event.getCommand())
	{
	case KeyboardEvent.ARROW_RIGHT:
	    calendar.add(Calendar.DAY_OF_MONTH, 1);
	    break;
	case KeyboardEvent.ARROW_LEFT:
	    calendar.add(Calendar.DAY_OF_MONTH, -1);
	    break;
	case KeyboardEvent.ARROW_DOWN:
	    calendar.add(Calendar.WEEK_OF_MONTH, 1);
	    break;
	case KeyboardEvent.ARROW_UP:
	    calendar.add(Calendar.WEEK_OF_MONTH, -1);
	    break;
	case KeyboardEvent.PAGE_DOWN:
	    calendar.add(Calendar.MONTH, 1);
	    break;
	case KeyboardEvent.PAGE_UP:
	    calendar.add(Calendar.MONTH, -1);
	    break;
	default:return false;
	}
	refresh();
	environment.onAreaNewContent(this);
	environment.onAreaNewHotPoint(this);
	environment.onAreaNewName(this);
	Speech.say(constructDayStringForSpeech(calendar));
	return true;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	return false;
    }

    public String getName()
    {
	//FIXME:Customizable behaviour;
	//FIXME:ROOT locale;
	return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ROOT) + ", " + calendar.get(Calendar.YEAR);
    }

    public void setCalendar(Calendar calendar)
    {
	this.calendar = calendar;
	refresh();
    }

    public Calendar getCalendar()
    {
	return calendar;
    }

    public String constructDayStringForSpeech(Calendar c)
    {
	//FIXME:ROOT locale;
	return c.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ROOT) + " " + 
	c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.ROOT) + " " +
	c.get(Calendar.DAY_OF_MONTH) + " " + c.get(Calendar.YEAR);
    }

    private void refresh()
    {
	table = fillTable();
	tableX = 0;
	tableY = 0;
	if (table == null)
	    return;
	final int day = calendar.get(Calendar.DAY_OF_MONTH);
	for(int i = 0;i < table.length;++i)
	    for(int j = 0;j < table[i].length;++j)
		if (table[i][j] == day)
		{
		    tableY = i;
		    tableX = j;
		}
    }

    private int[][] fillTable()
    {
	Calendar c = (Calendar)calendar.clone();
	int[][] res = new int[c.getActualMaximum(Calendar.WEEK_OF_MONTH)][];
	for(int i = 0;i < res.length;++i)
	    res[i] = new int[7];
	for(int i = 0;i < res.length;++i)
	    for(int j = 0;j < res[i].length;++j)
	    res[i][j] = -1;
	final int count = c.getActualMaximum(Calendar.DAY_OF_MONTH);
	for(int i = 1;i <= count;++i)
	{
	    c.set(Calendar.DAY_OF_MONTH, i);
	    final int week = c.get(Calendar.WEEK_OF_MONTH);
	    final int dayOfWeek = c.get(Calendar.DAY_OF_WEEK)  > 1?c.get(Calendar.DAY_OF_WEEK)  - 1:7;
	    res[week - 1][dayOfWeek - 1] = i;
	}
	return res;
    }

    private String constructLine(int index)
    {
	if (table == null || index >= table.length)
	    return "";
	String line = "";
	for(int i = 0;i < table[index].length;++i)
	    if (table[index][i] >= 0)
	    {
		if (table[index][i] < 10)
		    line += table[index][i] + "   "; else
		    line += table[index][i] + "  ";
	    } else
		line += "    ";
	return line;
    }
}
