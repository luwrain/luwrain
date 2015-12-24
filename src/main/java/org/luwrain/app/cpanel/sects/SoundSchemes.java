

package org.luwrain.app.cpanel.sects;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.cpanel.*;

class SoundSchemes extends SimpleListSection
{
    static private class ClickHandler implements ListClickHandler
    {
	private Luwrain luwrain;

	ClickHandler(Luwrain luwrain)
	{
	    this.luwrain = luwrain;
	}

	@Override public boolean onListClick(ListArea area, int index, Object obj)
	{
	    return false;
	}
    };
    private final FixedListModel model = new FixedListModel();

    SoundSchemes()
    {
	super("Схемы", BasicSections.NONE, (luwrain, params)->{
		params.clickHandler = new ClickHandler(luwrain);
		params.model = new FixedListModel();
		params.appearance = new DefaultListItemAppearance(params.environment);
	    });
}
}
