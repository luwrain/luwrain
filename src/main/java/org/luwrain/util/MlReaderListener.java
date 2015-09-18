/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

   This file is part of the LUWRAIN.

   LUWRAIN is free software; you can redistribute it and/or
   modify it under the terms of the GNU General Public
   License as published by the Free Software Foundation; either
   version 3 of the License, or (at your option) any later version.

   LUWRAIN is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.
*/

package org.luwrain.util;

import java.util.*;

public interface MlReaderListener
{
    void onMlTagOpen(String tagName, Map<String, String> attrs);
    void onMlText(String text, LinkedList<String> tagsStack);
    void onMlTagClose(String tagName);

    //Called on tag opening multiple times before  first false return, on each true closes one tag 
    boolean isMlAutoClosingNeededOnTagOpen(String newTagName, LinkedList<String> tagsStack);

    boolean mayMlAnticipatoryTagClose(String tagName, 
				      LinkedList<String> anticipatoryTags, LinkedList<String> tagsStack);



}
