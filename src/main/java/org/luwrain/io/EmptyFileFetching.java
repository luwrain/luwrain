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

import org.luwrain.core.*;

import static org.luwrain.core.NullCheck.*;

public class EmptyFileFetching implements FileFetcher.Fetching
{
        protected String destFile, srcUrl;
    protected float progress;
    protected boolean completed;

    public EmptyFileFetching(String destFile, String srcUrl, float progress, boolean completed)
    {
	notNull(destFile, "destFile");
	notNull(srcUrl, "srcUrl");
	this.destFile = destFile;
	this.srcUrl = srcUrl;
	this.progress = progress;
	this.completed = completed;
    }

    public EmptyFileFetching(String destFile, String srcUrl)
    {
	this(destFile, srcUrl, 0, false);
    }

    public void progress(float progress)
    {
	this.progress = progress;
    }

    public void completed()
    {
	this.progress = 100;
	this.completed = true;
    }

    @Override public float getProgress() { return progress; }
    @Override public boolean isCompleted() { return completed; }
    @Override public String getDestinationFile() { return destFile; }
    @Override public String getSourceUrl() { return srcUrl; }
    }
