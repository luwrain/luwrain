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

public class NotepadStringConstructor implements org.luwrain.app.notepad.StringConstructor
{
    @Override public String appName()
    {
	return "Блокнот";
    }

    @Override public String introduction()
    {
	return "Редактирование";
    }

    @Override public String newFileName()
    {
	return "Новый файл.txt";
    }

    @Override public String errorOpeningFile()
    {
	return "Произошла ошибка при чтении файла";
    }

    public String errorSavingFile()
    {
	return "Произошла ошибка при сохранении файла";
    }

    @Override public String fileIsSaved()
    {
	return "Файл сохранён успешно";
    }

    @Override public String savePopupName()
    {
	return "Сохранение файла";
    }

    @Override public String savePopupPrefix()
    {
	return "Введите имя файла для сохранения:";
    }

    @Override public String saveChangesPopupName()
    {
	return "Несохранённые изменения";
    }

    @Override public String saveChangesPopupQuestion()
    {
	return "Вы хотите сохранить изменения?";
    }

    @Override public String noModificationsToSave()
    {
	return "Нет изменений для сохранения";
    }
}
