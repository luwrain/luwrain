
package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

class PersonalInfoArea extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private final Registry registry;
    private final Settings.PersonalInfo sett;

    PersonalInfoArea(ControlPanel controlPanel)
    {
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()));
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.registry = luwrain.getRegistry();
this.sett = Settings.createPersonalInfo(luwrain.getRegistry());
addEdit("name", "Полное имя:", sett.getFullName(""), null, true);
addEdit("address", "Основной адрес электронной почты:", sett.getDefaultMailAddress(""), null, true);
activateMultilineEdit("Текст подписи в сообщениях электронной почты:", sett.getSignature(""), true);
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (event.isSpecial() && !event.isModified())
	    switch(event.getSpecial())
	    {
	    case TAB:
		controlPanel.gotoSectionsTree ();
		return true;
	    }
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	switch(event.getCode())
	{
	case CLOSE:
	    controlPanel.close();
	    return true;
	default:
	    return super.onEnvironmentEvent(event);
	}
    }

    @Override public String getAreaName()
    {
	return "Персональная информация";
    }

    @Override public boolean saveSectionData()
    {
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final Registry registry = luwrain.getRegistry();
	sett.setFullName(getEnteredText("name"));
	sett.setDefaultMailAddress(getEnteredText("address"));
	sett.setSignature(getMultilineEditText());
	return true;
    }
}
