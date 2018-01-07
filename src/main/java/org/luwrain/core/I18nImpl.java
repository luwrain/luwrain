/*
   Copyright 2012-2018 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

import java.util.*;

class I18nImpl implements I18n, I18nExtension
{
    static private final String LOG_COMPONENT = "core";
    static private final String EN_LANG = "en";

    private final Vector<CommandTitle> commandTitles = new Vector<CommandTitle>();
    private final Vector<StringsObj> stringsObjs = new Vector<StringsObj>();
    private final Vector<LangObj> langObjs = new Vector<LangObj>();

    private Lang chosenLang;
    private String chosenLangName = "";

    @Override public String getPastTimeBrief(Date date)
    {
	NullCheck.notNull(date, "date");
	if (chosenLang == null)
	    return "#NO CHOSEN LANGUAGE#";
	final String value = chosenLang.pastTimeBrief(date);
	return value != null?value:"";
    }

    @Override public String getExceptionDescr(Exception e)
    {
	NullCheck.notNull(e, "e");
	if (e instanceof java.nio.file.NoSuchFileException)
	    return e.getMessage() + ":нет такого файла";
	if (e instanceof java.nio.file.AccessDeniedException)
	    return e.getMessage() + ":отказано в доступе";
	if (e instanceof java.nio.file.DirectoryNotEmptyException)
	    return e.getMessage() + ":каталог не пуст";
	if (e instanceof java.nio.file.DirectoryNotEmptyException)
	    return e.getMessage() + ":каталог не пуст";
	if (e instanceof java.nio.file.FileAlreadyExistsException)
	    return e.getMessage() + ":файл уже существует";
	if (e instanceof java.nio.file.InvalidPathException)
	    return e.getMessage() + ":неверно оформленный путь к файлу";
	if (e instanceof java.nio.file.NotDirectoryException)
	    return e.getMessage() + ":не является каталогом";
	if (e instanceof java.nio.file.NotLinkException)
	    return e.getMessage() + ":не является ссылкой";
	if (e instanceof java.nio.file.ReadOnlyFileSystemException)
	    return e.getMessage() + ":файловая система доступна только для чтения";
	return e.getMessage() + ":" + e.getClass().getName();
    }

    @Override public String getNumberStr(int count, String entities)
    {
	NullCheck.notNull(entities, "entities");
	if (chosenLang == null)
	    return "#NO CHOSEN LANGUAGE#";
	final String value = chosenLang.getNumberStr(count, entities);
	return value != null?value:"";
    }

    @Override public String staticStr(LangStatic id)
    {
	NullCheck.notNull(id, "id");
	return getStaticStr(convertStaticValueName(id.toString()));
    }

    @Override public String getStaticStr(String id)
    {
	NullCheck.notNull(id, "id");
	if (chosenLang == null)
	    return "#NO CHOSEN LANGUAGE#";
	final String value = chosenLang.getStaticStr(id);
	return value != null && !value.isEmpty()?value:"#NO STATIC VALUE \'" + id + "\'#";
    }

    @Override public String hasSpecialNameOfChar(char ch)
    {
	return chosenLang != null?chosenLang.hasSpecialNameOfChar(ch):null;
    }

    @Override public String getCommandTitle(String command)
    {
	NullCheck.notNull(command, "command");
	if (command.trim().isEmpty())
	    throw new IllegalArgumentException("command may not be empty");
	String chosenLangValue = null;
	String enLangValue = null;
	String anyLangValue = null;
	for(CommandTitle t: commandTitles)
	{
	    if (!t.command.equals(command))
		continue;
	    if (anyLangValue == null)
		anyLangValue = t.title;
	    if (t.lang.equals(chosenLangName))
		chosenLangValue = t.title;
	    if (t.lang.equals(EN_LANG))
		enLangValue = t.title;
	}
	if (chosenLangValue != null)
	    return chosenLangValue;
	if (enLangValue != null)
	    return enLangValue;
	return anyLangValue != null?anyLangValue:command;
    }

    @Override public void addCommandTitle(String lang,
					  String command,
					  String title)
    {
	if (lang == null || lang.trim().isEmpty() ||
	    command == null || command.trim().isEmpty() ||
	    title == null || title.trim().isEmpty())
	    return;
	for(CommandTitle t: commandTitles)
	    if (t.lang.equals(lang) && t.command.equals(command))
		return;
	CommandTitle t = new CommandTitle();
	t.lang = lang;
	t.command = command;
	t.title = title;
	commandTitles.add(t);
    }

    @Override public Object getStrings(String component)
    {
	if (component == null)
	    throw new NullPointerException("component may not be null");
	if (component.trim().isEmpty())
	    throw new IllegalArgumentException("component may not be empty");
	Object chosenLangObj = null;
	Object enLangObj = null;
	Object anyLangObj = null;
	for(StringsObj o: stringsObjs)
	{
	    if (!o.component.equals(component))
		continue;
	    if (anyLangObj == null)
		anyLangObj = o.obj;
	    if (o.lang.equals(chosenLangName))
		chosenLangObj = o.obj;
	    if (o.lang.equals(EN_LANG))
		enLangObj = o.obj;
	}
	if (chosenLangObj != null)
	    return chosenLangObj;
	if (enLangObj != null)
	    return enLangObj;
	return anyLangObj != null?anyLangObj:null;
    }

    @Override public void addStrings(String lang,
			   String component,
			   Object obj)
    {
	if (lang == null || lang.trim().isEmpty() ||
	    component == null || component.trim().isEmpty() ||
	    obj == null)
	    return;
	for(StringsObj o: stringsObjs)
	    if (o.lang.equals(lang) && o.component.equals(component))
		return;
	StringsObj o = new StringsObj();
	o.lang = lang;
	o.component = component;
	o.obj = obj;
	stringsObjs.add(o);
    }

    @Override public void addLang(String name, Lang lang)
    {
	if (name == null || name.trim().isEmpty() ||
	    lang == null)
	    return;
	for(LangObj l: langObjs)
	    if (l.name.equals(name))
		return;
	LangObj l = new LangObj();
	l.name = name;
	l.lang = lang;
	langObjs.add(l);
    }

    boolean chooseLang(String name)
    {
	NullCheck.notEmpty(name, "name");
	Log.debug(LOG_COMPONENT, "trying to choose the language, \'" + name + "\' requested");
	if (langObjs.isEmpty())
	{
	    Log.warning(LOG_COMPONENT, "no langs registered, unable to choose the default");
	    return false;
	}
	for(LangObj l: langObjs)
	    Log.debug(LOG_COMPONENT, "lang \'" + l.name + "\' loaded");
	LangObj desiredLang = null;
	LangObj enLang = null;
	for(LangObj l: langObjs)
	{
	    if (l.name.equals(name))
		desiredLang = l;
	    if (l.name.equals(EN_LANG))
		enLang = l;
	}
	if (desiredLang != null)
	    Log.debug(LOG_COMPONENT, "desired lang found"); else
	    Log.debug(LOG_COMPONENT, "desired lang \'" + name + "\' not found");
	if (enLang != null)
	    Log.debug(LOG_COMPONENT, "English lang found"); else
	    Log.debug(LOG_COMPONENT, "English lang not found");
	if (desiredLang != null)
	{
	    chosenLang = desiredLang.lang;
	    chosenLangName = desiredLang.name;
	} else
	    if (enLang != null)
	    {
		chosenLang = enLang.lang;
		chosenLangName = enLang.name;
	    } else
	    {
	final LangObj l = langObjs.get(0);
	chosenLang = l.lang;
	chosenLangName = l.name;
	    }
	Log.info("core", "chosen lang is \'" + chosenLangName + "\'");
	return true;
    }

    String getChosenLangName()
    {
	return chosenLangName;
    }

    static private String convertStaticValueName(String name)
    {
	final StringBuilder b = new StringBuilder();
	boolean nextCap = true;
	for(int i = 0;i < name.length();++i)
	{
	    final char c = name.charAt(i);
	    if (c == '_')
	    {
		nextCap = true;
		continue;
	    }
	    if (nextCap)
		b.append(Character.toUpperCase(c)); else
		b.append(Character.toLowerCase(c));
	    nextCap = false;
	}
	return b.toString();
    }

    static private class CommandTitle
    {
	String lang = "";
	String command = "";
	String title = "";
    }

    static private class StringsObj
    {
	String lang = "";
	String component = "";
	Object obj;
    };

    static private class LangObj
    {
	String name = "";
	Lang lang;
    }

}
