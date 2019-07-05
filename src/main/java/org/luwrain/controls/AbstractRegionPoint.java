
//LWR_API 1.0

package org.luwrain.controls;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public interface AbstractRegionPoint extends HotPoint
{
    boolean onSystemEvent(EnvironmentEvent event, int hotPointX, int hotPointY);
    boolean isInitialized();
    void set(int hotPointX, int hotPointY);
    void reset();
}
