
package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

class UiArea extends FormArea implements SectionArea
{
    private ControlPanel controlPanel;
    private final RegistryKeys registryKeys = new RegistryKeys();

    UiArea(ControlPanel controlPanel)
    {
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()));
	this.controlPanel = controlPanel;
	NullCheck.notNull(controlPanel, "controlPanel");
	final RegistryAutoCheck check = new RegistryAutoCheck(controlPanel.getCoreInterface().getRegistry());
	addEdit("desktop-introduction-file", "Имя файла для отображения на рабочем столе:", check.stringAny(registryKeys.desktopIntroductionFile(), ""), null, true);
	addEdit("launch-greeting", "Текст голосового приветствия при старте системы:", check.stringAny(registryKeys.launchGreeting(), ""), null, true);
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
	return "Настройки интерфейса";
    }

    boolean save()
    {
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final Registry registry = luwrain.getRegistry();
	if (!registry.setString(registryKeys.desktopIntroductionFile(), getEnteredText("desktop-introduction-file")))
	    return false;
	if (!registry.setString(registryKeys.launchGreeting(), getEnteredText("launch-greeting")))
	    return false;
	return true;
    }
}
