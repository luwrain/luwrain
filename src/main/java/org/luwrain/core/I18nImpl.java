/*
   Copyright 2012-2021 Michael Pozhidaev <msp@luwrain.org>

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

import org.luwrain.i18n.*;

final class I18nImpl implements I18n, I18nExtension
{
    static private final String LOG_COMPONENT = Base.LOG_COMPONENT;
    static private final String EN_LANG = "en";
    static private final String NO_CHOSEN_LANG = "#NO CHOSEN LANGUAGE#";

    private Lang chosenLang = null;
    private String chosenLangName = "";

    private final List<CommandTitle> commandTitles = new LinkedList();
    private final List<StringsObj> stringsObjs = new LinkedList();
    private final Map<String, Lang> langs = new HashMap();

    @Override public Lang getActiveLang()
    {
	return chosenLang;
    }

    @Override public Lang getLang(String langName)
    {
	NullCheck.notEmpty(langName, "langName");
	return langs.containsKey(langName)?langs.get(langName):null;
	    }

    String getSpeakableText(String text, Luwrain.SpeakableTextType speakableTextType)
    {
	NullCheck.notNull(text, "text");
	NullCheck.notNull(speakableTextType, "speakableTextType");
	if (chosenLang == null)
	    return NO_CHOSEN_LANG;
	final String value = chosenLang.getSpeakableText(text, speakableTextType);
	return value != null?value:"";
    }

    @Override public String getPastTimeBrief(Date date)
    {
	NullCheck.notNull(date, "date");
	if (chosenLang == null)
	    return NO_CHOSEN_LANG;
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
	NullCheck.notEmpty(command, "command");
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

    @Override public void addCommandTitle(String lang, String command, String title)
    {
	NullCheck.notEmpty(lang, "lang");
	NullCheck.notEmpty(command, "command");
	NullCheck.notEmpty(title, "title");
	for(CommandTitle t: commandTitles)
	    if (t.lang.equals(lang) && t.command.equals(command))
		return;
	commandTitles.add(new CommandTitle(lang, command, title));
    }

    @Override public Object getStrings(String component)
    {
	NullCheck.notEmpty(component, "component");
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

    @Override public void addStrings(String lang, String component, Object obj)
    {
	NullCheck.notEmpty(lang, "lang");
	NullCheck.notEmpty(component, "component");
	NullCheck.notNull(obj, "obj");
	for(StringsObj o: stringsObjs)
	    if (o.lang.equals(lang) && o.component.equals(component))
		return;
	stringsObjs.add(new StringsObj(lang, component, obj));
    }

    @Override public boolean addLang(String name, Lang lang)
    {
	NullCheck.notEmpty(name, "name");
	NullCheck.notNull(lang, "lang");
	if (langs.containsKey(name))
	    return false;
	langs.put(name, lang);
	return true;
    }

    boolean chooseLang(String name)
    {
	NullCheck.notEmpty(name, "name");
	if (langs.isEmpty())
	{
	    Log.error(LOG_COMPONENT, "no langs registered, unable to choose the default");
	    return false;
	}
	for(Map.Entry<String, Lang> l: langs.entrySet())
	    Log.debug(LOG_COMPONENT, "lang \'" + l.getKey() + "\' loaded");
	Lang desiredLang = null;
	String desiredLangName = "";
	Lang anyLang = null;
	String anyLangName = "";
	Lang enLang = null;
	for(Map.Entry<String, Lang> l: langs.entrySet())
	{
	    if (anyLang == null)//Preferably taking the first one
	    {
		anyLang = l.getValue();
		anyLangName = l.getKey();
	    }
	    if (l.getKey().equals(name))
	    {
		desiredLang = l.getValue();
		desiredLangName = name;
	    }
	    if (l.getKey().equals(EN_LANG))
		enLang = l.getValue();
	}
	if (desiredLang == null)
	    Log.warning(LOG_COMPONENT, "the desired language \'" + name + "\' not found");
	if (enLang == null)
	    Log.warning(LOG_COMPONENT, "English language not found");
	if (desiredLang != null)
	{
	    chosenLang = desiredLang;
	    chosenLangName = desiredLangName;
	} else
	    if (enLang != null)
	    {
		chosenLang = enLang;
		chosenLangName = EN_LANG;
	    } else
	    {
		chosenLang = anyLang;
		chosenLangName = anyLangName;
	    }
	Log.debug("core", "the chosen language is \'" + chosenLangName + "\'");
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

    static private final class CommandTitle
    {
	final String lang;
	final String command;
	final String title;
	CommandTitle(String lang, String command, String title)
	{
	    NullCheck.notEmpty(lang, "lang");
	    NullCheck.notEmpty(command, "command");
	    NullCheck.notEmpty(title, "title");
	    this.lang = lang;
	    this.command = command;
	    this.title = title;
	}
    }

    static private final class StringsObj
    {
	final String lang;
	final String component;
	final Object obj;
	StringsObj(String lang, String component, Object obj)
	{
	    NullCheck.notEmpty(lang, "lang");
	    NullCheck.notEmpty(component, "component");
	    NullCheck.notNull(obj, "obj");
	    this.lang = lang;
	    this.component = component;
	    this.obj = obj;
	}
    };
}
