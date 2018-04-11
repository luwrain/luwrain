
package org.luwrain.app.cmdtool;

import org.luwrain.core.*;

final class Actions
{
    private final Luwrain luwrain;

    Actions(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
	this.luwrain = luwrain;
    }
}
