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

package org.luwrain.core;

class  EventResponses
{
    static class Hint implements EventResponse
    {
	final org.luwrain.core.Hint hint;
	final String text;

	Hint(org.luwrain.core.Hint hint)
	{
	    NullCheck.notNull(hint, "hint");
	    this.hint = hint;
	    this.text = null;
	}

	Hint(org.luwrain.core.Hint hint, String text)
	{
	    NullCheck.notNull(hint, "hint");
	    NullCheck.notNull(text, "text");
	    this.hint = hint;
	    this.text = text;
	}

	@Override public void announce(Luwrain luwrain, Speech speech)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(speech, "speech");
	}
    }

    static class Text implements EventResponse
    {
	final String text;

	Text(String text)
	{
	    NullCheck.notNull(text, "text");
	    this.text = text;
	}

	@Override public void announce(Luwrain luwrain, Speech speech)
	{
	    NullCheck.notNull(text, "text");
	    NullCheck.notNull(speech, "speech");
	}
    }

    static class ListItem implements EventResponse
    {
	final Sounds sound;
	final String text;
	final Suggestions suggestion;

	ListItem(Sounds sound, String text, Suggestions suggestion)
	{
	    NullCheck.notNull(text, "text");
	    this.sound = sound;
	    this.text = text;
	    this.suggestion = suggestion;
	}

	@Override public void announce(Luwrain luwrain, Speech speech)
	{
	    NullCheck.notNull(luwrain, "luwrain");
	    NullCheck.notNull(speech, "speech");
	    if (sound != null)
		luwrain.playSound(sound); else
		luwrain.playSound(Sounds.LIST_ITEM);
	    if (suggestion == null)
	    {
		speech.speak(new String[]{text});
		return;
	    }
	    final String suggestionText = getSuggestionText(suggestion, luwrain.i18n());
	    if (suggestionText != null)
		speech.speak(new String[]{text, suggestionText}); else
		speech.speak(new String[]{text});
	    	}
    }


    //May return null
    static private String getSuggestionText(Suggestions suggestion, I18n i18n)
    {
	NullCheck.notNull(suggestion, "suggestion");
	NullCheck.notNull(i18n, "i18n");
switch(suggestion)
{
case CLICKABLE_LIST_ITEM:
    return "Элемент списка, нажмите Enter для активации";
case LIST_ITEM:
    return "Элемент списка";
case POPUP_LIST_ITEM:
    return "Элемент списка, нажмите Enter для выбора";
default:
    return null;
}
    }
    }
