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

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public class SimpleArea extends NavigateArea
{
    private String name = "";
    private String[] content = null;

    public SimpleArea()
    {
    }

    public SimpleArea(String name)
    {
	this.name = name;
    }

    public SimpleArea(String name, String[] content)
    {
	this.name = name;
	this.content = content;
    }

    public int getLineCount()
    {
	if (content == null || content.length < 1)
	    return 1;
	return content.length;
    }

    public String getLine(int index)
    {
	if (content == null || content.length < 1)
	    return new String();
	if (index >= content.length || content[index] == null)
	    return new String();
	return content[index];
    }

    public void setContent(String[] content)
    {
	this.content = content;
	Luwrain.onAreaNewContent(this);
	fixHotPoint();
    }

    public String[] getContent()
    {
	return content;
    }

    public void setLine(int index, String line)
    {
	if (content == null || content.length < 1)
	{
	    if (index != 0)
		return;
	    content = new String[1];
	    content[0] = line;
	    return;
	}
	if (index >= content.length)
	    return;
	content[index] = line;
    }

    public void addLine(String line)
    {
	if (content == null || content.length < 1)
	{
	    content = new String[1];
	    content[0] = line;
	    Luwrain.onAreaNewContent(this);
	    fixHotPoint();
	    return;
	}
	String[] newContent = new String[content.length + 1];
	for(int i = 0;i < content.length;i++)
	    newContent[i] = content[i];
	newContent[newContent.length - 1] = line;
	content = newContent;
	Luwrain.onAreaNewContent(this);
	fixHotPoint();
    }

    public void insertLine(int index, String line)
    {
	if (content == null || content.length < 1)
	{
	    if (index != 0)
		return;
	    content = new String[1];
	    content[0] = line;
	    Luwrain.onAreaNewContent(this);
	    fixHotPoint();
	    return;
	}
	if (index > content.length)
	    return;
	String[] newContent = new String[content.length + 1];
	for(int i = 0;i < index;i++)
	    newContent[i] = content[i];
	newContent[index] = line;
	for(int i = index;i < content.length;i++)
	    newContent[i + 1] = content[i];
	content = newContent;
	Luwrain.onAreaNewContent(this);
	fixHotPoint();
    }

    public void removeLine(int index)
    {
	if (content == null || content.length < 1)
	    return;
	if (index >= content.length)
	    return;
	String[] newContent = new String[content.length - 1];
	for(int i = 0;i < index;i++)
	    newContent[i] = content[i];
	for(int i = index + 1;i < content.length;i++)
	    newContent[i - 1] = content[i];
	content = newContent;
	Luwrain.onAreaNewContent(this);
	fixHotPoint();
    }

    public void clear()
    {
	content = null;
	Luwrain.onAreaNewContent(this);
	fixHotPoint();
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	//Nothing here;
	return false;
    }

    public String getName()
    {
	if (name == null)
	    return "";
	return name;
    }

public void setName(String name)
{
    this.name = name;
    Luwrain.onAreaNewName(this);
}
}
