/*
   Copyright 2012-2022 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core;

import java.util.List;

import org.luwrain.core.Job.Instance;

public class EmptyJobListener implements Job.Listener
{
    		@Override public void onInfoChange(Job.Instance instance, String type, List<String> value){}
    @Override public void onStatusChange(Instance instance) {}
    @Override public void onSingleLineStateChange(Instance instance) {}
    @Override public void onMultilineStateChange(Instance instance) {}
    @Override public void onNativeStateChange(Instance instance) {}
}
