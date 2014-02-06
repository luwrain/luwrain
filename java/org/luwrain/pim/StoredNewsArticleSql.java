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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;

class StoredNewsArticleSql implements StoredNewsArticle
{
    Connection con = null;
    public long id = 0;
    public long groupId = 0;

    public int state = 0;
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

    public StoredNewsArticleSql(Connection con)
    {
	this.con = con;
    }

  public   int getState()
    {
	return state;
    }

    public   void setState(int state) throws SQLException
    {
	PreparedStatement st = con.prepareStatement("UPDATE news_article SET state = ? WHERE id = ?;");
	st.setInt(1, state);
	st.setLong(2, id);
	st.executeUpdate();
	this.state = state;
    }

    public   String getSourceUrl()
    {
	return sourceUrl;
    }

    public   void setSourceUrl(String sourceUrl) throws SQLException
    {
	//FIXME:
    }

    public   String getSourceTitle()
    {
	return sourceTitle;
    }

    public   void setSourceTitle(String sourceTitle) throws SQLException
    {
	//FIXME:
    }

    public   String getUri()
    {
	return uri;
    }

    public   void setUri(String uri) throws SQLException
    {
	//FIXME:
    }

    public   String getTitle()
    {
	return title;
    }

    public   void setTitle(String title) throws SQLException
    {
	//FIXME:
    }

    public   String getExtTitle()
    {
	return extTitle;
    }

    public   void setExtTitle(String extTitle) throws SQLException
    {
	//FIXME:
    }

    public   String getUrl()
    {
	return url;
    }

    public   void setUrl(String url) throws SQLException
    {
	//FIXME:
    }

    public   String getDescr()
    {
	return descr;
    }

    public   void setDescr(String descr) throws SQLException
    {
	//FIXME:
    }

    public   String getAuthor()
    {
	return author;
    }

    public   void setAuthor(String authro) throws SQLException
    {
	//FIXME:
    }

    public   String getCategories()
    {
	return categories;
    }

    public   void setCategories(String categories) throws SQLException
    {
	//FIXME:
    }

    public   Date getPublishedDate()
    {
	return publishedDate;
    }

    public   void setPublishedDate(Date publishedDate) throws SQLException
    {
	//FIXME:
    }

    public   Date getUpdatedDate()
    {
	return updatedDate;
    }

    public   void setUpdatedDate(Date updatedDate) throws SQLException
    {
	//FIXME:
    }

    public   String getContent()
    {
	return content;
    }

    public   void setContent(String content) throws SQLException
    {
	//FIXME:
    }
}
