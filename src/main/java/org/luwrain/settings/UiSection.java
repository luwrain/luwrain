
package org.luwrain.settings;

import org.luwrain.core.*;
import org.luwrain.core.events.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;
import org.luwrain.util.*;

class UiSection extends EmptySection
{
    private UiArea area = null;

    @Override public Area getSectionArea(Environment environment)
    {
	if (area == null)
	    area = new UiArea(environment);
	return area;
    }

    @Override public String toString()
    {
	return "Интерфейс";
    }

    @Override public boolean canCloseSection(Environment environment)
    {
	if (area == null)
	    return true;
	if (!area.save())
	    environment.getLuwrain().message("Во время сохранения сделанных изменений произошла непредвиденная ошибка", Luwrain.MESSAGE_ERROR);
	return true;
    }
}
