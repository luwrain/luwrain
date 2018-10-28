/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

//LWR_API 1.0

package org.luwrain.speech;

import java.util.*;

public interface Factory2 extends org.luwrain.base.ExtensionObject
{
    public enum Features {
	CAN_SYNTH_TO_STREAM,
	CAN_SYNTH_TO_SPEAKERS,
	CAN_NOTIFY_WHEN_FINISHED,
    };

        Set<Features>  getFeatures();
            Channel2 newChannel(Map<String, String> params);
}
