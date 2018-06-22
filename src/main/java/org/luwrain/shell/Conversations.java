/*
   Copyright 2012-2017 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.shell;

import java.util.*;
import java.io.*;

import org.luwrain.core.*;
import org.luwrain.core.queries.*;
import org.luwrain.popups.*;

public class Conversations
{
    private final Luwrain luwrain;

    public Conversations(Luwrain luwrain)
    {
	NullCheck.notNull(luwrain, "luwrain");
		this.luwrain = luwrain;
    }

    public boolean quitConfirmation()
    {
	final YesNoPopup popup = new YesNoPopup(luwrain, luwrain.i18n().getStaticStr("QuitPopupName"), luwrain.i18n().getStaticStr("QuitPopupText"), true, Popups.DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	return !popup.wasCancelled() && popup.result();
    }

    public String commandPopup(String[] allCommands)
    {
	NullCheck.notNullItems(allCommands, "allCommands");
	final EditListPopup popup = new EditListPopup(luwrain, new EditListPopupUtils.FixedModel(allCommands),
						      luwrain.i18n().getStaticStr("CommandPopupName"), luwrain.i18n().getStaticStr("CommandPopupPrefix"), "", EnumSet.noneOf(Popup.Flags.class)){
		@Override public boolean onAreaQuery(AreaQuery query)
		{
		    NullCheck.notNull(query, "query");
		    switch(query.getQueryCode())
		    {
		    case AreaQuery.OBJECT_UNIREF:
			if (text.trim().isEmpty())
			    return false;
			((ObjectUniRefQuery)query).answer("command:" + text().trim());
			return true;
		    default:
			return super.onAreaQuery(query);
		    }
		}
	    };
	luwrain.popup(popup);
	if (popup.wasCancelled())
	    return null;
	return !popup.text().isEmpty()?popup.text():null;
    }

    public File openPopup()
    {
	final File current = new File(luwrain.getActiveAreaDir());
	final FilePopup popup = new FilePopup(luwrain, 
					      luwrain.i18n().getStaticStr("OpenPopupName"),
					      luwrain.i18n().getStaticStr("OpenPopupPrefix"), 
					      null, current, current,
					      Popups.loadFilePopupFlags(luwrain), Popups.DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	if (popup.wasCancelled())
	    return null;
	return popup.result();
    }

    boolean deleteDesktopItemConfirmation(String name)
    {
	NullCheck.notNull(name, "name");
	final YesNoPopup popup = new YesNoPopup(luwrain, 
						"Удаление элемента",//FIXME:
						"Вы действительно хотите удалить элемент \"" + name + "\" с рабочего стола?",//FIXME:
						false,
						Popups.DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	return !popup.wasCancelled() && popup.result();
    }

    boolean deleteDesktopItemsConfirmation(int count)
    {
	final YesNoPopup popup = new YesNoPopup(luwrain, 
						"Удаление элемента",//FIXME:
						"Вы действительно хотите удалить " + luwrain.i18n().getNumberStr(count, "items") + " с рабочего стола?",//FIXME:
						false,
						Popups.DEFAULT_POPUP_FLAGS);
	luwrain.popup(popup);
	return !popup.wasCancelled() && popup.result();
    }
}
