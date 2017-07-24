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
    private final Environment env;

    public Conversations(Luwrain luwrain, Environment env)
    {
	NullCheck.notNull(luwrain, "luwrain");
	NullCheck.notNull(env, "env");
	this.luwrain = luwrain;
	this.env = env;
    }

    public boolean quitConfirmation()
    {
	final YesNoPopup popup = new YesNoPopup(luwrain, luwrain.i18n().getStaticStr("QuitPopupName"), luwrain.i18n().getStaticStr("QuitPopupText"), true, Popups.DEFAULT_POPUP_FLAGS);
	env.popupIface(popup);
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
env.popupIface(popup);
if (popup.wasCancelled())
	    return null;
	return !popup.text().isEmpty()?popup.text():null;
    }




    public File openPopup()
    {
	final File current = new File(luwrain.currentAreaDir());
	final FilePopup popup = new FilePopup(luwrain, 
					      luwrain.i18n().getStaticStr("OpenPopupName"),
					      luwrain.i18n().getStaticStr("OpenPopupPrefix"), 
					      null, current, current,
					      Popups.loadFilePopupFlags(luwrain), Popups.DEFAULT_POPUP_FLAGS);
	env.popupIface(popup);
	if (popup.wasCancelled())
	    return null;
	return popup.result();
    }

    public boolean deleteDesktopItemConfirmation()
    {
	final YesNoPopup popup = new YesNoPopup(luwrain, 
						"Удаление элемента",
"Вы действительно хотите удалить элемент с рабочего стола?",
						false,
						Popups.DEFAULT_POPUP_FLAGS);
	env.popupIface(popup);
	return !popup.wasCancelled() && popup.result();
    }
}