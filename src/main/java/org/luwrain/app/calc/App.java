/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.app.calc;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;

public class App implements Application
{
    private Luwrain luwrain = null;
    private Strings strings = null;
    private Base base = null;
    private EditArea editArea = null;

    @Override public InitResult onLaunchApp(Luwrain luwrain)
    {
	final Object o = luwrain.i18n().getStrings(Strings.NAME);
	if (o == null || !(o instanceof Strings))
	    return new InitResult(InitResult.Type.NO_STRINGS_OBJ, Strings.NAME);
	this.strings = (Strings)o;
	this.luwrain = luwrain;
	this.base = new Base(luwrain, strings);
	createArea();
	return new InitResult();
    }

    private void createArea()
    {
	final EditArea.Params params = new EditArea.Params();
	params.context = new DefaultControlEnvironment(luwrain);
	params.name = strings.appName();
	this.editArea = new EditArea(params){
		@Override public MultilineEdit.Model createMultilineEditModel(CorrectorWrapperFactory correctorWrapperFactory)
		{
		    final MultilineEdit.Model model = super.createMultilineEditModel(correctorWrapperFactory);
		    return createBlockingModel(model);
		}
		@Override public boolean onInputEvent(KeyboardEvent event)
		{
		     NullCheck.notNull(event, "event");
		     if (!event.isSpecial())
		     {
			 if (getHotPointY() >= getLineCount() - 3 )
			     return false;
			 switch(event.getChar())
			 {
			     case '\'':
			 case '\"':
			 case ';':
			     return false;
			 default:
			     return super.onInputEvent(event);
			 }
		     }
		     switch(event.getSpecial())
		     {
		     case BACKSPACE:
			 		     if (getHotPointY() >= getLineCount() - 3)
			 return false;
					     break;
					     		     case DELETE:

								 			 		     if (getHotPointY() >= getLineCount() - 3)
			 return false;
													     if (getHotPointY() == getLineCount() - 4 && getHotPointX() == getLine(getHotPointY()).length())
														 return false;
													     break;
			 		     }
		     return super.onInputEvent(event);
		     }
		@Override public boolean onSystemEvent(EnvironmentEvent event)
		{
		    NullCheck.notNull(event, "event");
		    if (event.getType() != EnvironmentEvent.Type.REGULAR)
			return super.onSystemEvent(event);
		    switch (event.getCode())
		    {
		    case CLIPBOARD_PASTE:
						if (getHotPointY() >= getLineCount() - 3)
			 return false;
						return super.onSystemEvent(event);
				    case CLEAR_REGION:
		    case CLIPBOARD_CUT:
			if (getHotPointY() >= getLineCount() - 3 ||
			    regionPoint.getHotPointY() >= getLineCount() - 3)
			 return false;
																     return super.onSystemEvent(event);
		    case CLEAR:
			setLines(new String[]{"0", "", "# 0", ""});
			setHotPoint(0, 0);
			return true;
		    case OK:
			{
			    final StringBuilder b = new StringBuilder();
			    final String[] lines = getLines();
			    for(int i = 0;i < lines.length - 3;i++)
				b.append(lines[i] + " ");
			    try {
				final Number res = base.calculate(new String(b));
				if (res == null)
				    return false;
				luwrain.message(luwrain.getSpokenText( res.toString(), Luwrain.SpokenTextType.PROGRAMMING), Luwrain.MessageType.OK);
							    return true;
			    }
			    catch(Exception e)
			    {
				luwrain.message(e.getClass().getName() + ":" + e.getMessage());
				return true;
			    }
			}
		    case CLOSE:
			closeApp();
			return true;
		    default:
			return super.onSystemEvent(event);
		    }
		}
		@Override public void announceLine(int index, String line)
	{
	    NullCheck.notNull(line, "line");
	    if (line.trim().isEmpty())
	    {
		luwrain.setEventResponse(DefaultEventResponse.hint(Hint.EMPTY_LINE));
		return;
	    }
		luwrain.setEventResponse(DefaultEventResponse.text(luwrain.getSpokenText(line, Luwrain.SpokenTextType.PROGRAMMING)));
	}

		
	    };
	editArea.getContent().setLines(new String[]{
		"0",
		"",
		"# 0",
		"",
	    });
    }

        @Override public String getAppName()
    {
	return strings.appName();
    }

    @Override public AreaLayout getAreaLayout()
    {
	return new AreaLayout(editArea);
    }

    @Override public void closeApp()
    {
	luwrain.closeApp();
    }

    static private MultilineEdit.Model createBlockingModel(MultilineEdit.Model origModel)
    {
	return new MultilineEdit.Model(){
	    @Override public int getLineCount()
	    {
		return origModel.getLineCount();
	    }
	    @Override public String getLine(int index)
	    {
		return origModel.getLine(index);
	    }
	    @Override public int getHotPointX()
	    {
		return origModel.getHotPointX();
	    }
	    @Override public int getHotPointY()
	    {
		return origModel.getHotPointY();
	    }
	    @Override public String getTabSeq()
	    {
		return origModel.getTabSeq();
	    }
	    @Override public char deleteChar(int pos, int lineIndex)
	    {
		return origModel.deleteChar(pos, lineIndex);
	    }
	    @Override public boolean deleteRegion(int fromX, int fromY, int toX, int toY)
	    {
		return origModel.deleteRegion(fromX, fromY, toX, toY);
	    }
	    @Override public boolean insertRegion(int x, int y, String[] lines)
	    {
		return origModel.insertRegion(x, y, lines);
	    }
	    @Override public boolean insertChars(int pos, int lineIndex, String str)
	    {
		return origModel.insertChars(pos, lineIndex, str);
	    }
	    @Override public boolean mergeLines(int firstLineIndex)
	    {
		return origModel.mergeLines(firstLineIndex);
	    }
	    @Override public String splitLines(int pos, int lineIndex)
	    {
		return origModel.splitLines(pos, lineIndex);
	    }
	};
    }
}
