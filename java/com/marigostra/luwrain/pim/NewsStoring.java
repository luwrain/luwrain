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

package com.marigostra.luwrain.pim;

import java.sql.SQLException;

public interface NewsStoring
{
    StoredNewsGroup[] loadNewsGroups() throws SQLException;
    String[] loadNewsGroupSources(StoredNewsGroup group) throws SQLException;
    void saveNewsArticle(StoredNewsGroup newsGroup, NewsArticle article) throws SQLException;
    StoredNewsArticle[] loadNewsArticlesInGroup(StoredNewsGroup newsGroup) throws SQLException;
    StoredNewsArticle[] loadNewsArticlesInGroupWithoutRead(StoredNewsGroup newsGroup) throws SQLException;
    int countArticlesByUriInGroup(StoredNewsGroup newsGroup, String uri) throws SQLException;
}
