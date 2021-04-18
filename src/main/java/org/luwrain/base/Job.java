/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.base;

import java.util.*;

public interface Job extends ExtensionObject
{
    public enum Flags {WITH_SHORTCUT, INTERACTIVE_SHORTCUT};
    public enum Status {RUNNING, FINISHED};

    public interface Listener
    {
	void onStatusChange(Instance instance);
	void onSingleLineStateChange(Instance instance);
	void onMultilineStateChange(Instance instance);
	void onNativeStateChange(Instance instance);
    }

    public interface Instance
    {
	String getInstanceName();
	Status getStatus();
	int getExitCode();
	boolean isFinishedSuccessfully();
	String getSingleLineState();
	String[] getMultilineState();
	String[] getNativeState();
	void stop();
    }

    Instance launch(Listener listener, String[] args);
    Set<Flags> getJobFlags();
}
