//Reads  and saves

package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

class PersonalInfo extends FormArea implements SectionArea
{
    private final ControlPanel controlPanel;
    private final Luwrain luwrain;
    private final Registry registry;
    private final Settings.PersonalInfo sett;

    PersonalInfo(ControlPanel controlPanel)
    {
	super(new DefaultControlEnvironment(controlPanel.getCoreInterface()), controlPanel.getCoreInterface().i18n().getStaticStr("CpPersonalInfoSection"));
	NullCheck.notNull(controlPanel, "controlPanel");
	this.controlPanel = controlPanel;
	this.luwrain = controlPanel.getCoreInterface();
	this.registry = luwrain.getRegistry();
this.sett = Settings.createPersonalInfo(luwrain.getRegistry());
fillForm();
    }

    private void fillForm()
    {
	addEdit("name", luwrain.i18n().getStaticStr("CpPersonalInfoFullName"), sett.getFullName(""), null, true);
	addEdit("address", luwrain.i18n().getStaticStr("CpPersonalInfoMailAddress"), sett.getDefaultMailAddress(""), null, true);
	activateMultilineEdit(luwrain.i18n().getStaticStr("CpPersonalInfoSignature"), sett.getSignature(""), true);
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
}
