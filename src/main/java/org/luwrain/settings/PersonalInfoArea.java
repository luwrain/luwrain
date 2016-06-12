
package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

class PersonalInfoArea extends FormArea implements SectionArea
{
    private ControlPanel controlPanel;
    private final RegistryKeys registryKeys = new RegistryKeys();

    PersonalInfoArea(ControlPanel controlPanel)
    {
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()));
	this.controlPanel = controlPanel;
	NullCheck.notNull(controlPanel, "controlPanel");
	final RegistryAutoCheck check = new RegistryAutoCheck(controlPanel.getCoreInterface().getRegistry());
	addEdit("name", "Полное имя:", check.stringAny(registryKeys.personalFullName(), ""), null, true);
	addEdit("address", "Основной адрес электронной почты:", check.stringAny(registryKeys.personalDefaultMailAddress(), ""), null, true);
	activateMultilineEdit("Текст подписи в сообщениях электронной почты:", check.stringAny(registryKeys.personalSignature(), ""), true);
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
	if (!registry.setString(registryKeys.personalFullName(), getEnteredText("name")))
	    return false;
	if (!registry.setString(registryKeys.personalDefaultMailAddress(), getEnteredText("address")))
	    return false;
	if (!registry.setString(registryKeys.personalSignature(), getMultilineEditText()))
	    return false;
	return true;
    }
}
