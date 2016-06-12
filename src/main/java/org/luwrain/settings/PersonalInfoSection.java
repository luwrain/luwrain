
package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

class PersonalInfoSection extends EmptySection
{
    private PersonalInfoArea area = null;

    @Override public SectionArea getSectionArea(ControlPanel controlPanel)
    {
	if (area == null)
	    area = new PersonalInfoArea(controlPanel);
	return area;
    }

    @Override public String toString()
    {
	return "Персональная информация";
    }

    @Override public boolean canCloseSection(ControlPanel controlPanel)
    {
	return false;
    }
}
