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

package org.luwrain.io.download;

import java.util.*;

public interface DownloadManager
{
    public enum Flags{
    };

    String startDownload(String url, String destFilePath, int attemptCount, Set<Flags> flags);
    void closeDownload(String downloadId);
    boolean isDownloadFinished(String downloadId);
    String getUrl(String downloadId);
    String getDestFilePath(String downloadId);
    LocalDownload startLocalDownload(String url, String destFilePath, int attemptCount, Set<Flags> flags);
}
