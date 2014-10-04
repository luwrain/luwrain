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

package org.luwrain.langs.en;

import java.util.*;

public class NotepadStringConstructor implements org.luwrain.app.notepad.StringConstructor
{
    @Override public String appName()
    {
	return "Notepad";
    }

    @Override public String introduction()
    {
	return "Editing";
    }

    @Override public String newFileName()
    {
	return "New file.txt";
    }

    @Override public String errorOpeningFile()
    {
	return "An error occurred while reading the file";
    }

    public String errorSavingFile()
    {
	return "An error occurred while saving the file";
    }

    @Override public String fileIsSaved()
    {
	return "File is successfully saved";
    }

    @Override public String savePopupName()
    {
	return "Save file";
    }

    @Override public String savePopupPrefix()
    {
	return "Enter the name of the file to save as:";
    }

    @Override public String saveChangesPopupName()
    {
	return "Unsaved changes";
    }

    @Override public String saveChangesPopupQuestion()
    {
	return "Do you want to save changes?";
    }

    @Override public String noModificationsToSave()
    {
	return "There are no unsaved changes";
    }
}
