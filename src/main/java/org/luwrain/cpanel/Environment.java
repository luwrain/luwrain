
package org.luwrain.cpanel;

import org.luwrain.core.*;

public interface Environment extends org.luwrain.controls.ControlEnvironment
{
    void popup(Popup popup);
    void refreshSectionsTree();
    Luwrain getLuwrain();
}
