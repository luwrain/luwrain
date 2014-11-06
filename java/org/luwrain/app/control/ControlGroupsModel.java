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

package org.luwrain.app.control;

import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.pim.*;

class ControlGroupsModel implements TreeModel
{
    private Luwrain luwrain;
    private ControlActions actions;
    private StringConstructor stringConstructor;
    private NewsStoring newsStoring;
    private MailStoring mailStoring;
    private String root;
    private String interaction;
    private String mail;
    private String news;
    private String[] rootItems;
    private StoredNewsGroup[] newsGroups;

    public ControlGroupsModel(Luwrain luwrain,
			      ControlActions actions,
			      StringConstructor stringConstructor)
    {
	this.actions = actions;
	this.stringConstructor = stringConstructor; 
	createItems();
    }

    public Object getRoot()
    {
	return root;
    }

    public boolean isLeaf(Object node)
    {
	if (node == root)
	    return false;
	if (node == news)
	    return newsGroups == null || newsGroups.length <= 0;
	return true;
    }

    public void beginChildEnumeration(Object node)
    {
	//FIXME:
    }

    public int getChildCount(Object parent)
    {
	if (parent == root)
	    return rootItems.length;
	if (parent == news)
	    return newsGroups != null?newsGroups.length:0;
	return 0;
    }

    public Object getChild(Object parent, int index)
    {
	if (parent == root && index < rootItems.length)
	    return rootItems[index];
	if (parent == news && newsGroups != null && index < newsGroups.length)
	    return newsGroups[index];
	return null;
    }

    public void endChildEnumeration(Object node)
    {
	//FIXME:
    }

    private void createItems()
    {
	root = stringConstructor.rootItemName();
	interaction = stringConstructor.interactionItemName();
	mail = stringConstructor.mailItemName();
	news = stringConstructor.newsItemName();
	rootItems = new String[3];
	rootItems[0] =interaction;
	rootItems[1] = mail;
	rootItems[2] = news; 
	newsStoring = luwrain.getPimManager().getNewsStoring();
	mailStoring = luwrain.getPimManager().getMailStoring();
	if (newsStoring != null)
	{
	    try {
		newsGroups = newsStoring.loadNewsGroups();
	    }
	    catch(Exception e)
	    {
		Log.error("control", "news groups loading problem:" + e.getMessage());
	    }
	}
    }

    public void insertItem()
    {

    }
}
