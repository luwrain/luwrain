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

package org.luwrain.app.message;

//TODO:Enter moves the cursor to next field;
//FIXME:Proper name;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

class MessageAreaHeaderItem
{
    public static final int EDIT = 0;

    public int type = EDIT;
    public String text = new String();
    public int offset = 1;
    public SingleLineEdit edit;
}

class MessageHeaderItemEditModel implements SingleLineEditModel
{
    private SimpleArea area;
    private String prefix;
    private int offset;
    private int lineNum;

    MessageHeaderItemEditModel(SimpleArea area, String prefix, int lineNum)
    {
	this.area = area;
	this.prefix = prefix;
	this.offset = prefix.length() + 1;
	this.lineNum = lineNum;
    }

    public String getLine()
    {
	try {
	    return area.getLine(lineNum).substring(offset);
	}
	catch (StringIndexOutOfBoundsException e)
	{
	    //FIXME:Log warning;
	    return "";
	}
    }

    public void setLine(String text)
    {
	area.setLine(lineNum, prefix + " " + text);
    }

    public int getHotPointX()
    {
	if (area.getHotPointY() != lineNum)
	    return 0;
	final int x = area.getHotPointX();
	if (x < offset)
	    return 0;
	return x - offset;
    }

    public void setHotPointX(int value)
    {
	if (area.getHotPointY() != lineNum)
	    return;
	area.setHotPoint(value + offset, lineNum);
    }

    public String getTabSeq()
    {
	return "\t";
    }
}

public class MessageArea extends SimpleArea
{
    private MessageActions actions;
    private MessageStringConstructor stringConstructor;
    private MultilinedEdit textEdit;
    private MessageAreaHeaderItem headerItems[];
    private int textEditOffset = 4;

    MessageArea(MessageActions actions, MessageStringConstructor stringConstructor)
    {
	this.actions = actions;
	this.stringConstructor = stringConstructor;
	createTextEdit();
	createHeaderItems();
	addLine(stringConstructor.to() + " ");
	addLine(stringConstructor.cc() + " ");
	addLine(stringConstructor.subject() + " ");
	addLine(stringConstructor.messageText());
	addLine("");
	setHotPoint(getLine(0).length(), 0);
    }

    public boolean onKeyboardEvent(KeyboardEvent event)//FIXME:boolean;
    {
	if (super.onKeyboardEvent(event))
	    return true;
	final int index = getHotPointY();
	if (index >= textEditOffset)
	    return textEdit.onKeyboardEvent(event);
	if (headerItems == null || index >= headerItems.length)
	    return false;
	if (headerItems[index].type != MessageAreaHeaderItem.EDIT || getHotPointX() < headerItems[index].offset)
	    return false;
	return headerItems[index].edit.onKeyboardEvent(event); 
	//FIXME:attachments;
    }

    public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	if (event.getCode() == EnvironmentEvent.CLOSE)
	{
	    actions.closeMessage();
	    return true;
	}
	return false;
    }

    private void createTextEdit()
    {
	final MessageArea thisArea = this;
	textEdit = new MultilinedEdit(new MultilinedEditModel(){
		private MessageArea area = thisArea;
		public String getLine(int index)
		{
		    String line = area.getLine(index + area.textEditOffset);
		    if (line == null)
			return new String();
		    return line;
		}
		public void setLine(int index, String text)
		{
		    area.setLine(index + area.textEditOffset, text);
		}
		public int getLineCount()
		{
		    final int count = area.getLineCount();
		    if (count <= textEditOffset)
			return 1;
		    return count - area.textEditOffset;
		}
		public int getHotPointX()
		{
		    if (area.getHotPointY() < area.textEditOffset)
			return 0;
		    return area.getHotPointX();
		}
		public int getHotPointY()
		{
		    final int y = area.getHotPointY();
		    if (y <= area.textEditOffset)
			return 0;
		    return y - area.textEditOffset;
		}
		public void setHotPoint(int x, int y)
		{
		    area.setHotPoint(x, y + area.textEditOffset);
		}
		public void removeLine(int index)
		{
		    area.removeLine(index + area.textEditOffset);
		}
		public void insertLine(int index, String text)
		{
		    area.insertLine(index + area.textEditOffset, text);
		}
		public String getTabSeq()
		{
		    return "\t";
		}
	    });
    }

    private void createHeaderItems()
    {
	final String toStr = stringConstructor.to();
	final String ccStr = stringConstructor.cc();
	final String subjectStr = stringConstructor.subject();
	SingleLineEdit toEdit = new SingleLineEdit(new MessageHeaderItemEditModel(this, toStr, 0));
	SingleLineEdit ccEdit = new SingleLineEdit(new MessageHeaderItemEditModel(this, ccStr, 1));
	SingleLineEdit subjectEdit = new SingleLineEdit(new MessageHeaderItemEditModel(this, subjectStr, 2));
	headerItems = new MessageAreaHeaderItem[3];
	for(int i = 0;i < headerItems.length;i++)
	    headerItems[i] = new MessageAreaHeaderItem();
	headerItems[0].type = MessageAreaHeaderItem.EDIT;
	headerItems[0].text = toStr;
	headerItems[0].offset = toStr.length() + 1;
	headerItems[0].edit = toEdit;
	headerItems[1].type = MessageAreaHeaderItem.EDIT;
	headerItems[1].text = ccStr;
	headerItems[1].offset = ccStr.length() + 1;
	headerItems[1].edit = ccEdit;
	headerItems[2].type = MessageAreaHeaderItem.EDIT;
	headerItems[2].text = subjectStr;
	headerItems[2].offset = subjectStr.length() + 1;
	headerItems[2].edit = subjectEdit;
    }
}
