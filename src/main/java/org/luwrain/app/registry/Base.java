/*
   Copyright 2012-2020 Michael Pozhidaev <msp@luwrain.org>

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

package org.luwrain.app.registry;

import org.luwrain.core.*;

class Base
{
    private Luwrain luwrain;
    private Strings strings;
    private Registry registry;
    private DirectoriesTreeModel dirsModel;
    private ValuesListModel valuesModel;
    private ValuesListAppearance valuesAppearance;

    public boolean init(Luwrain luwrain, Strings strings)
    {
	this.luwrain = luwrain;
	this.strings = strings;
	if (luwrain == null)
	    throw new NullPointerException("luwrain may not be null");
	if (strings == null)
	    throw new NullPointerException("strings may not be null");
	registry = luwrain.getRegistry();
	return true;
    }

    public DirectoriesTreeModel getDirsModel()
    {
	if (dirsModel != null)
	    return dirsModel;
	dirsModel = new DirectoriesTreeModel(luwrain, strings);
	return dirsModel;
    }

    public ValuesListModel getValuesModel()
    {
	if (valuesModel != null)
	    return valuesModel;
	valuesModel = new ValuesListModel(registry);
	return valuesModel;
}

    public ValuesListAppearance getValuesAppearance()
    {
	if (valuesAppearance != null)
	    return valuesAppearance;
	valuesAppearance = new ValuesListAppearance(luwrain, strings);
	return valuesAppearance;
    }


    public boolean insertDir(Directory parentDir)
    {
	/*
	SimpleEditPopup popup = new SimpleEditPopup(luwrain, strings.newDirectoryTitle(), strings.newDirectoryPrefix(parent.toString()), "");//FIXME:Validator if not empty;
	luwrain.popup(popup);
	if (popup.closing.cancelled())
	    return;
	if (popup.text().trim().isEmpty())
	{
	    luwrain.message(strings.directoryNameMayNotBeEmpty());
	    return;
	}
	if (popup.text().indexOf("/") >= 0)
	{
	    luwrain.message(strings.directoryInsertionRejected(parent.toString(), popup.text()));
	    return;
	}
	if (!registry.addDirectory(parent.getPath() + "/" + popup.text()))
	{
	    luwrain.message(strings.directoryInsertionRejected(parent.toString(), popup.text()));
	    return;
	}
	    dirsArea.refresh();
	*/
	return true;
    }
}
