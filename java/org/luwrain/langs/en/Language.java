/*
   Copyright 2012-2013 Michael Pozhidaev <msp@altlinux.org>

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

package org.luwrain.langs.en;

public class Language implements org.luwrain.core.Language
{
    private LanguageStaticStrings staticStrings = new LanguageStaticStrings();

    public LanguageStaticStrings getStaticStrings()
    {
	return staticStrings;
    }

    public Object requestStringConstructor(String id)
    {
	if (id.equals("system-application"))
	    return new SystemAppStringConstructor(this);
	if (id.equals("news-reader"))
	    return new NewsReaderStringConstructor();
	if (id.equals("mail-reader"))
	    return new MailReaderStringConstructor();
	if (id.equals("message"))
	    return new MessageStringConstructor();
	if (id.equals("commander"))
	    return new CommanderStringConstructor();
	if (id.equals("notepad"))
	    return new NotepadStringConstructor();
	if (id.equals("fetch"))
	    return new FetchStringConstructor();
	if (id.equals("preview"))
	    return new PreviewStringConstructor();
	return null;
    }

    public String getActionTitle(String actionName)
    {
	if (actionName == null)
	    return null;
	if (actionName.trim().equals("main-menu"))
	    return "Main menu";
	if (actionName.trim().equals("quit"))
	    return "Quit Luwrain";
	if (actionName.trim().equals("ok"))
	    return "OK";
	if (actionName.trim().equals("cancel"))
	    return "Cancel";
	if (actionName.trim().equals("close"))
	    return "Close";
	if (actionName.trim().equals("save"))
	    return "Save";
	if (actionName.trim().equals("refresh"))
	    return "Refresh";
	if (actionName.trim().equals("describe"))
	    return "Describe";
	if (actionName.trim().equals("help"))
	    return "Help";
	if (actionName.trim().equals("switch-next-app"))
	    return "Go to the next application";
	if (actionName.trim().equals("switch-next-area"))
	    return "Go to the next window";
	if (actionName.trim().equals("notepad"))
	    return "Notepad";
	if (actionName.trim().equals("commander"))
	    return "Files commander";
	if (actionName.trim().equals("news"))
	    return "News";
	if (actionName.trim().equals("mail"))
	    return "Mail";
	if (actionName.trim().equals("fetch"))
	    return "Mail and news fetching";
	if (actionName.trim().equals("message"))
	    return "New message";
	if (actionName.trim().equals("preview"))
	    return "Documents preview";
	return "";
    }
}
