/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

   This file is part of the Luwrain.

   Luwrain is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   Luwrain is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.langs.en;

import java.util.*;

public class NewsReaderStringConstructor implements org.luwrain.app.news.NewsReaderStringConstructor
{
    public String appName()
    {
	return "News reading";
    }

    public String groupAreaName()
    {
	return "News group list";
    }

    public String summaryAreaName()
    {
	return "News article list";
    }

    public String viewAreaName()
    {
	return "News article preview";
    }

    public     String errorReadingArticles()
    {
	return "An error occurred while news fetching";
    }

    public String readPrefix()
    {
	return "Read";
    }

    public String markedPrefix()
    {
	return "Marked";
    }

    public String noSummaryItems()
    {
	return "No news articles";
    }

    public String noSummaryItemsAbove()
    {
	return "Beginning of news article list";
    }

    public String noSummaryItemsBelow()
    {
	return "End of news article list";
    }
}
