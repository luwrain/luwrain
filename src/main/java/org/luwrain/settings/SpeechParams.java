

package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SpeechParams extends NavigationArea implements SectionArea
{
    static private final int STEP = 5;

    private ControlPanel controlPanel;
    private Luwrain luwrain;
    private String name;

    SpeechParams(ControlPanel controlPanel,
		 ControlEnvironment env, String name)
    {
	super(env);
	NullCheck.notNull(controlPanel, "controlPanel");
	NullCheck.notNull(env, "env");
	NullCheck.notNull(name, "name");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
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
	if (controlPanel.onKeyboardEvent(event))
	    return true;
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

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    @Override public boolean saveSectionData()
    {
	return true;
    }

    static SpeechParams create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	return new SpeechParams(controlPanel, new DefaultControlEnvironment(controlPanel.getCoreInterface()), "Параметры речи");
    }
}
