
package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
//import org.luwrain.util.*;

class UserInterface extends FormArea implements SectionArea
{
    private ControlPanel controlPanel;
    private Luwrain luwrain;
    private Settings.UserInterface settings;

    UserInterface(ControlPanel controlPanel)
    {
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()));
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	settings = Settings.createUserInterface(luwrain.getRegistry());
	fillForm();
    }

    private void fillForm()
    {
	addEdit("launch-greeting", "Текст голосового приветствия при запуске:", settings.getLaunchGreeting(""));
	addCheckbox("file-popup-skip-hidden", "Исключать скрытые файлы в о всплывающих окнах:", settings.getFilePopupSkipHidden(false));
	addCheckbox("empty-line-under-regular-lists", "Добавлять пустую строку в конце списков:", settings.getEmptyLineUnderRegularLists(false));
	addCheckbox("empty-line-above-popup-lists", "Добавлять пустую строку в начало всплывающих списков:", settings.getEmptyLineAbovePopupLists(false));
	addCheckbox("cycling-regular-lists", "Зацикливать навигацию по спискам:", settings.getCyclingRegularLists(false));
	addCheckbox("cycling-popup-lists", "Зацикливать навигацию по всплывающим спискам:", settings.getCyclingPopupLists(false));
    }

    @Override public boolean onKeyboardEvent(KeyboardEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onKeyboardEvent(event))
	    return true;
	return super.onKeyboardEvent(event);
    }

    @Override public boolean onEnvironmentEvent(EnvironmentEvent event)
    {
	NullCheck.notNull(event, "event");
	if (controlPanel.onEnvironmentEvent(event))
	    return true;
	return super.onEnvironmentEvent(event);
    }

    boolean save()
    {
/*
	final Luwrain luwrain = controlPanel.getCoreInterface();
	final Registry registry = luwrain.getRegistry();
	if (!registry.setString(registryKeys.desktopIntroductionFile(), getEnteredText("desktop-introduction-file")))
	    return false;
	if (!registry.setString(registryKeys.launchGreeting(), getEnteredText("launch-greeting")))
	    return false;
	return true;
*/
	return false;
    }

    static UserInterface create(ControlPanel controlPanel)
    {
	NullCheck.notNull(controlPanel, "controlPanel");
	return new UserInterface(controlPanel);
    }
}
