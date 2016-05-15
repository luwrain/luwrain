
package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

class PersonalInfoArea extends FormArea implements SectionArea
{
    private Environment environment;
    private final RegistryKeys registryKeys = new RegistryKeys();

    PersonalInfoArea(Environment environment )
    {
	super(new DefaultControlEnvironment(environment.getLuwrain()));
	this.environment = environment;
	NullCheck.notNull(environment, "environment");
	final RegistryAutoCheck check = new RegistryAutoCheck(environment.getLuwrain().getRegistry());
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
		environment.gotoSectionsTree ();
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
	    environment.close();
	    return true;
	default:
	    return super.onEnvironmentEvent(event);
	}
    }

    @Override public String getAreaName()
    {
	return "Персональная информация";
    }

    boolean save()
    {
	final Luwrain luwrain = environment.getLuwrain();
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
