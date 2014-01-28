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

package org.luwrain.langs.ru;

import java.util.*;

public class NotepadStringConstructor implements org.luwrain.app.notepad.NotepadStringConstructor
{
    public String appName()
    {
	return "Блокнот";
    }

    public String introduction()
    {
	return "Редактирование";
    }

    public String newFileName()
    {
	return "Новый файл.txt";
    }

    public String errorOpeningFile()
    {
	return "Невозможно открыть файл для редактирования";
    }

    public String errorSavingFile()
    {
	return "Произошла ошибка, файл не сохранён";
    }

    public String fileIsSaved()
    {
	return "Файл успешно записан!";
    }
}
