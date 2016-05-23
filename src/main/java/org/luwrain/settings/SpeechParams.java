

package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SpeechParams extends NavigateArea implements SectionArea
{
    static private final int STEP = 5;

    private Luwrain luwrain;
    private String name;

    SpeechParams(Luwrain luwrain,
		 ControlEnvironment env, String name)
    {
	super(env);
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(env, "env");
	NullCheck.notNull(name, "name");
	this.luwrain = luwrain;
	this.name = name;
    }

    @Override public String getLine(int index)
    {
	switch(index)
	{
	case 0:
	    return "Скорость речи: " + (100 - luwrain.getSpeechRate());
	case 1:
	    return "Высота речи: " + luwrain.getSpeechPitch();
	case 2:
	    return "Пунктуация:";
	default:
	    return "";
	}
    }

    @Override public int getLineCount()
    {
	return 4;
    }

    @Override public String getAreaName()
    {
	return name;
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (!event.isSpecial() && !event.isModified())
	    switch(event.getChar())
	{
	case '+':
	case '=':
	    switch(getHotPointY())
	    {
	    case 0:
		luwrain.setSpeechRate(luwrain.getSpeechRate() - STEP);
		luwrain.onAreaNewContent(this);
		return true;
	    case 1:
		luwrain.setSpeechPitch(luwrain.getSpeechPitch() + STEP);
		luwrain.onAreaNewContent(this);
		return true;
	    }
	    break;
	case '-':
	    switch(getHotPointY())
	    {
	    case 0:
		luwrain.setSpeechRate(luwrain.getSpeechRate() + STEP);
		luwrain.onAreaNewContent(this);
		return true;
	    case 1:
		luwrain.setSpeechPitch(luwrain.getSpeechPitch() - STEP);
		luwrain.onAreaNewContent(this);
		return true;
	    }
	    break;
	}
	return super.onKeyboardEvent(event);
    }

    static SpeechParams create(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	return new SpeechParams(luwrain, new DefaultControlEnvironment(luwrain), "Параметры речи");
    }
}
