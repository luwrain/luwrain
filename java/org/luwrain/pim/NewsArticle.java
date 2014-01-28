/*
   Copyright 2012-2014 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.pim;

import java.util.*;

public class NewsArticle
{
    public static final int NEW = 0;
    public static final int READ = 1;
    public static final int MARKED = 2;

    public String sourceUrl = new String();
    public String sourceTitle = new String();
    public String uri = new String();
    public String title = new String();
    public String extTitle = new String();
    public String url = new String();
    public String descr = new String();
    public String author = new String();
    public String categories = new String();
    public Date publishedDate = new Date();
    public Date updatedDate = new Date();
    public String content = new String();
}
