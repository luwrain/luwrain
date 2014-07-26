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

public interface StoredDiaryEntry
{
    String getTitle();
    void setTitle(String value) throws Exception;
    String getComment();
    void setComment(String value) throws Exception;
    Date getDateTime();
    void setDateTime(Date value) throws Exception;
    int getDuration();
    void setDuration(int value) throws Exception;
    int getType();
    void setType(int type) throws Exception;
    int getStatus();
    void setStatus(int value) throws Exception;
    int getImportance();
    void setImportance(int value) throws Exception;
    String getAttributes();
    void setAttributes(String value) throws Exception;
    String getAttributesType();
    void setAttributesType(String value) throws Exception;
}
