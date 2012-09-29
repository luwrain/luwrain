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

package org.luwrain.langs.ru;

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
	    return new SystemAppStringConstructor();
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
	return null;
    }
}
