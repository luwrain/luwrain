/*
   Copyright 2012-2015 Michael Pozhidaev <michael.pozhidaev@gmail.com>

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

package org.luwrain.core;

import java.util.Vector;

class I18nImpl implements I18n, I18nExtension
{
    private static final String EN_LANG = "en";

    class CommandTitle
    {
	public String lang = "";
	public String command = "";
	public String title = "";
    }

    class StringsObj
    {
	public String lang = "";
	public String component = "";
	public Object obj;
    };

    class LangObj
    {
	public String name = "";
	public Lang lang;
    }

    private Vector<CommandTitle> commandTitles = new Vector<CommandTitle>();
    private Vector<StringsObj> stringsObjs = new Vector<StringsObj>();
    private Vector<LangObj> langObjs = new Vector<LangObj>();

    private Lang chosenLang;
    private String chosenLangName = "";

    @Override public String staticStr(int code)
    {
	if (chosenLang == null)
	    return "#NO CHOSEN LANGUAGE#";
	final String value = chosenLang.staticStr(code);
	return value != null && !value.isEmpty()?value:"#NO STATIC VALUE#";
    }

    @Override public String hasSpecialNameOfChar(char ch)
    {
	return chosenLang != null?chosenLang.hasSpecialNameOfChar(ch):null;
    }

    @Override public String commandTitle(String command)
    {
	if (command == null)
	    throw new NullPointerException("command may not be null");
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

    public boolean chooseLang(String name)
    {
	if (name == null)
	    throw new NullPointerException("name may not be null");
	if (name.trim().isEmpty())
	    throw new IllegalArgumentException("name may not be empty");
	if (langObjs.isEmpty())
	    return false;
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
	{
	    chosenLang = desiredLang.lang;
	    chosenLangName = desiredLang.name;
	    return true;
	}
	if (enLang != null)
	{
	    chosenLang = enLang.lang;
	    chosenLangName = enLang.name;
	    return true;
	}
	LangObj l = langObjs.get(0);
	chosenLang = l.lang;
	chosenLangName = l.name;
	return true;
    }
}
