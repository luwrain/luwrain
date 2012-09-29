/*
   Copyright 2012 Michael Pozhidaev <msp@altlinux.org>

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
import java.sql.SQLException;

public interface StoredNewsArticle
{
    int getState();
    void setState(int state) throws SQLException;
    String getSourceUrl();
    void setSourceUrl(String sourceUrl) throws SQLException;
    String getSourceTitle();
    void setSourceTitle(String sourceTitle) throws SQLException;
    String getUri();
    void setUri(String uri) throws SQLException;
    String getTitle();
    void setTitle(String title) throws SQLException;
    String getExtTitle();
    void setExtTitle(String extTitle) throws SQLException;
    String getUrl();
    void setUrl(String url) throws SQLException;
    String getDescr();
    void setDescr(String descr) throws SQLException;
    String getAuthor();
    void setAuthor(String authro) throws SQLException;
    String getCategories();
    void setCategories(String categories) throws SQLException;
    Date getPublishedDate();
    void setPublishedDate(Date publishedDate) throws SQLException;
    Date getUpdatedDate();
    void setUpdatedDate(Date updatedDate) throws SQLException;
    String getContent();
    void setContent(String content) throws SQLException;
}
