/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

   This file is part of LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.interaction.graphical;

import org.luwrain.core.*;
import org.luwrain.core.events.*;

public interface Pdf
{
    public interface Listener
    {
	void onInputEvent(KeyboardEvent event);
    }

    boolean init();
    void close();
    int getPageCount();
    int getCurrentPageNum();
    boolean showPage(int index);
    float getScale();
    void setScale(float scale);
    double getOffsetX();
        double getOffsetY();
    boolean setOffsetX(double value);
    boolean setOffsetY(double value);
}
