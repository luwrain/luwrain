

package org.luwrain.app.cpanel.sects;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SpeechParamsArea extends org.luwrain.cpanel.SimpleNavigateSection.Area
{
    static private final int STEP = 5;

    private Luwrain luwrain;

    SpeechParamsArea(Environment environment, String name)
    {
	super(environment, name);
	this.luwrain = environment.getLuwrain();
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
}
