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

package org.luwrain.app.mail;

import java.util.*;
import org.luwrain.core.*;
import org.luwrain.controls.*;
import org.luwrain.pim.*;


class Base
{
    private Luwrain luwrain;
    private StringConstructor stringConstructor;
    private MailStoring mailStoring;
    private FoldersTreeModel foldersModel;
    private SummaryTableModel summaryModel;
    private SummaryTableAppearance summaryAppearance;

    public void init(Luwrain luwrain, StringConstructor stringConstructor)
    {
	this.luwrain = luwrain;
	this.stringConstructor = stringConstructor;
	mailStoring = luwrain.getPimManager().getMailStoring();
    }

    public boolean isValid()
    {
	return mailStoring != null;
    }

    public FoldersTreeModel getFoldersModel()
    {
	if (foldersModel != null)
	    return foldersModel;
	foldersModel = new FoldersTreeModel(mailStoring, stringConstructor);
	return foldersModel;
    }

    public SummaryTableModel getSummaryModel()
    {
	if (summaryModel != null)
	    return summaryModel;
	summaryModel = new SummaryTableModel(mailStoring);
	return summaryModel;
    }

    public SummaryTableAppearance getSummaryAppearance()
    {
	if (summaryAppearance != null)
	    return summaryAppearance;
	summaryAppearance = new SummaryTableAppearance();
	return summaryAppearance;
    }

    public boolean isStoredMailGroup(Object obj)
    {
	return obj != null && (obj instanceof StoredMailGroup);
    }

    public boolean openFolder(Object obj, TableArea summaryTable)
    {
	if (obj == null || !(obj instanceof StoredMailGroup))
	    return false;
	summaryModel.setCurrentMailGroup((StoredMailGroup)obj);
	summaryTable.refresh();//FIXME:Reset hot point position;
	return summaryModel.isValidState();
    }
}
