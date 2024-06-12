/*
   Copyright 2012-2024 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.core;

import java.util.*;

public interface FileFetcher extends ExtensionObject
{
    public enum Flags { RESUMABLE, SPECIFIC };
    public enum Status { RUNNING, COMPLETED, CANCELLED };

    boolean canHandleUrl(String url);
    Set<Flags> getFlags();
    Fetching fetchUrl(String url, String destDir, String destFileName);

    public interface Fetching
    {
	float getProgress();
	boolean isCompleted();
	String getDestinationFile();
	String getSourceUrl();
    }

    public interface Listener
    {
	void onFetchingStatus(Fetching fetching, Status status, float progress);
    }
}
